package org.app.mydukan.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import android.widget.RemoteViews;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moengage.push.PushManager;
import com.moengage.pushbase.push.MoEngageNotificationUtils;

import org.app.mydukan.R;
import org.app.mydukan.activities.MainActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NotificationUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arpithadudi on 9/28/16.
 */

public class MydukanMessagingService extends FirebaseMessagingService {

    Bitmap bitmap;
    private MyDukan mApp;
    private ArrayList<Supplier> mSupplerlist = new ArrayList<>();
    String supplierId = null;
    HashMap<String, Object> notificationInfo;
    RemoteViews contentView;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (MyDukan) getApplicationContext();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Answers.getInstance().logCustom(new CustomEvent("Notification FireBaseReceived")
                .putCustomAttribute("FireBase ReceiverMoEngage", "Notification Received"));
        Log.d(MyDukan.LOGTAG, "onMessageReceived called" + remoteMessage);

        NotificationUtils utils = new NotificationUtils(this);

        Log.d(MyDukan.LOGTAG, "From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(MyDukan.LOGTAG, "Message data payload: " + remoteMessage.getData());
            remoteMessage.getMessageId();
            remoteMessage.getNotification();
            remoteMessage.getTo();


        }

        if( null == remoteMessage)return;
        if( MoEngageNotificationUtils.isFromMoEngagePlatform(remoteMessage.getData())){
            //If the message is not sent from MoEngage it will be rejected
            PushManager.getInstance().getPushHandler().handlePushPayload(getApplicationContext(), remoteMessage.getData());
            Answers.getInstance().logCustom(new CustomEvent("Notification FireBaseReceived").putCustomAttribute("FireBase ReceiverMoEngage", "Notification Received"));
            return;
        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(MyDukan.LOGTAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            remoteMessage.getNotification().getTitleLocalizationArgs();
        }

        //title
        String notificationtitle = remoteMessage.getData().get("title");
        //message will contain the Push Message
        String pushmessage = remoteMessage.getData().get("message");
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");
        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
        String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
        remoteMessage.getData().get("category");
        //To get a Bitmap image from the URL received


        notificationInfo = new HashMap<>();
        notificationInfo.put("notificationTitle",notificationtitle);
        notificationInfo.put("notificationMessage",pushmessage);
        notificationInfo.put("notificationImage",imageUri);
        //==================================================


        bitmap = getBitmapfromUrl(imageUri);
        String message = null;
        Map<String, String> payload = remoteMessage.getData();
        if (payload.size() > 0) {
            String command = payload.get(utils.COMMAND);
            payload.get("category");
           supplierId = payload.get(utils.SUPPLIERID);
           //fetchSupplierdata();
            if (!mApp.getUtils().isStringEmpty(command)) {
                //If command is refresh.
              if (command.equalsIgnoreCase(utils.REFRESH)) {
//                    //Then get the supplier id and refresh the groups
//                    //for the supplier

                   supplierId = payload.get(utils.SUPPLIERID);

            } else if (command.equalsIgnoreCase(utils.RECORD)) {
                    //Else show the notifications and add it to the database.
                    message = payload.get(utils.MESSAGE);


                }
            }
        }

        //=================================================
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationBadge_1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationBadge_1", true);
        editor.putString("NotificationSupplierId", supplierId);
        editor.apply();

//        ShortcutBadger.applyCount(this, 1); //for 1.1.4+
        //==================================================

        if (bitmap != null) {
          sendNotification(notificationtitle,notificationtitle, bitmap, TrueOrFlase);
          //  sentBigTextNotification(notificationtitle,notificationtitle,bitmap,TrueOrFlase);
        } else {
            //Check the message.
            if (!mApp.getUtils().isStringEmpty(message)) {
                showNotifications(notificationtitle,pushmessage, utils);
               // sentBigTextNotification(notificationtitle,pushmessage,bitmap, String.valueOf(utils));
            }
        }
    }


    private void showNotifications(final String notificationTitle, final String notificationMessage, NotificationUtils utils) {

        try {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(AppContants.NOTIFICATION, notificationInfo);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/sound_two");

            Bitmap image = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getApplicationContext());
            Notification notification = builder.setContentIntent(resultPendingIntent)
                    .setLargeIcon(image)
                    .setSmallIcon(R.mipmap.ic_launcher).setTicker(notificationMessage).setWhen(0)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true).setContentTitle(notificationTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationTitle))
                    .setContentText(notificationMessage).build();

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(01, notification);

            //Add it to the database.
            utils.saveNotification(mApp.getFirebaseAuth().getCurrentUser().getUid(), notificationMessage);

        } catch (Exception ex) {
            Log.d(MyDukan.LOGTAG, "addNotifications onFailure: " + ex.getMessage());
        }
    }



    private void sentBigTextNotification(String title, String message, Bitmap imageBitmap, String context) {

        //To set large icon in notification
        Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

//Assign inbox style notification
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText("By:MyDukan");



//build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(icon1);


/*

        //get the bitmap to show in notification bar
        Bitmap bitmap_image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_logo_app);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
        s.setSummaryText("Summary text appears on expanding the notification");
        mBuilder.setStyle(s);
*/

// Gets an instance of the NotificationManager service
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//to post your notification to the notification bar
        mNotificationManager.notify(0, mBuilder.build());

    }

    private void sendNotification(String title, String message, Bitmap image, String TrueOrFalse) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppContants.NOTIFICATION,notificationInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", TrueOrFalse);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        /*    Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/sound_two");
       */
        Bitmap  bitmap_image = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(bitmap_image)
                 .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Bitmap scaled = scaleDownLargeImageWithAspectRatio(bitmap);
            return scaled;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
    Bitmap scaleDownLargeImageWithAspectRatio(Bitmap image)
    {
        int imaheVerticalAspectRatio,imageHorizontalAspectRatio;
        float bestFitScalingFactor=0;
        float percesionValue=(float) 0.2;

        //getAspect Ratio of Image
        int imageHeight=(int) (Math.ceil((double) image.getHeight()/100)*100);
        int imageWidth=(int) (Math.ceil((double) image.getWidth()/100)*100);
        int GCD= BigInteger.valueOf(imageHeight).gcd(BigInteger.valueOf(imageWidth)).intValue();
        imaheVerticalAspectRatio=imageHeight/GCD;
        imageHorizontalAspectRatio=imageWidth/GCD;

        //getContainer Dimensions
        int displayWidth = 1024;
        int displayHeight = 512;
        //I wanted to show the image to fit the entire device, as a best case. So my ccontainer dimensions were displayWidth & displayHeight. For your case, you will need to fetch container dimensions at run time or you can pass static values to these two parameters

        int leftMargin = 0;
        int rightMargin = 0;
        int topMargin = 0;
        int bottomMargin = 0;
        int containerWidth = displayWidth - (leftMargin + rightMargin);
        int containerHeight = displayHeight - (topMargin + bottomMargin);

        //iterate to get bestFitScaleFactor per constraints
        while((imageHorizontalAspectRatio*bestFitScalingFactor <= containerWidth) &&
                (imaheVerticalAspectRatio*bestFitScalingFactor<= containerHeight))
        {
            bestFitScalingFactor+=percesionValue;
        }

        //return bestFit bitmap
        int bestFitHeight=(int) (imaheVerticalAspectRatio*bestFitScalingFactor);
        int bestFitWidth=(int) (imageHorizontalAspectRatio*bestFitScalingFactor);
        image=Bitmap.createScaledBitmap(image, bestFitWidth,bestFitHeight, true);

        //Position the bitmap centre of the container
        int leftPadding=(containerWidth-image.getWidth())/2;
        int topPadding=(containerHeight-image.getHeight())/2;
        Bitmap backDrop=Bitmap.createBitmap(containerWidth, containerHeight, Bitmap.Config.RGB_565);
        Canvas can = new Canvas(backDrop);
        can.drawBitmap(image, leftPadding, topPadding, null);

        return backDrop;
    }


}
