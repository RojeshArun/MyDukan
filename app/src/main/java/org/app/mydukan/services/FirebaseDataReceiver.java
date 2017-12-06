package org.app.mydukan.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.moengage.push.PushManager;
import com.moengage.pushbase.push.MoEngageNotificationUtils;

import org.app.mydukan.R;
import org.app.mydukan.activities.MainActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.utils.AppContants;


import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;




public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "FirebaseDataReceiver";
    HashMap<String, Object> notificationInfo;

    public void onReceive(Context context, Intent intent) {
        Answers.getInstance().logCustom(new CustomEvent("Notification Received")
                .putCustomAttribute("BroadcastReceiver", "Notification Received"));

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String title = "";
            String message = "";
            Bitmap imageBitmap = null;

            if( null == bundle)return;
            if( MoEngageNotificationUtils.isFromMoEngagePlatform(bundle)){
                //If the message is not sent from MoEngage it will be rejected
                PushManager.getInstance().getPushHandler().handlePushPayload(context.getApplicationContext(), bundle);
                Answers.getInstance().logCustom(new CustomEvent("Notification Received")
                        .putCustomAttribute("BroadcastReceiverMoEngage", "Notification Received"));
                return;
            }
            if (bundle.containsKey("message")) {
                message = bundle.get("message").toString();
            }

            if (bundle.containsKey("image")) {
                String image = bundle.get("image").toString();
                imageBitmap = getBitmapfromUrl(image);
            }

            if (bundle.containsKey("title")) {
                title = bundle.get("title").toString();
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationBadge", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationBadge", true);
            editor.apply();
//            ShortcutBadger.applyCount(context, 1); //for 1.1.4+

            if (imageBitmap != null) {
//                sendNotification(title, message, imageBitmap, context);

                String image = bundle.get("image").toString();
                notificationInfo = new HashMap<>();
                notificationInfo.put("notificationTitle", title);
                notificationInfo.put("notificationMessage", message);
                notificationInfo.put("notificationImage", image);
                sentBigTextNotification(title,message,imageBitmap,context);

            }
        }
    }

    private void sentBigTextNotification(String title, String message, Bitmap imageBitmap, Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppContants.NOTIFICATION,notificationInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap image = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(imageBitmap))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

// Gets an instance of the NotificationManager service
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

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

    Bitmap scaleDownLargeImageWithAspectRatio(Bitmap image) {
        int imaheVerticalAspectRatio, imageHorizontalAspectRatio;
        float bestFitScalingFactor = 0;
        float percesionValue = (float) 0.2;

        //getAspect Ratio of Image
        int imageHeight = (int) (Math.ceil((double) image.getHeight() / 100) * 100);
        int imageWidth = (int) (Math.ceil((double) image.getWidth() / 100) * 100);
        int GCD = BigInteger.valueOf(imageHeight).gcd(BigInteger.valueOf(imageWidth)).intValue();
        imaheVerticalAspectRatio = imageHeight / GCD;
        imageHorizontalAspectRatio = imageWidth / GCD;

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
        while ((imageHorizontalAspectRatio * bestFitScalingFactor <= containerWidth) &&
                (imaheVerticalAspectRatio * bestFitScalingFactor <= containerHeight)) {
            bestFitScalingFactor += percesionValue;
        }

        //return bestFit bitmap
        int bestFitHeight = (int) (imaheVerticalAspectRatio * bestFitScalingFactor);
        int bestFitWidth = (int) (imageHorizontalAspectRatio * bestFitScalingFactor);
        image = Bitmap.createScaledBitmap(image, bestFitWidth, bestFitHeight, true);

        //Position the bitmap centre of the container
        int leftPadding = (containerWidth - image.getWidth()) / 2;
        int topPadding = (containerHeight - image.getHeight()) / 2;
        Bitmap backDrop = Bitmap.createBitmap(containerWidth, containerHeight, Bitmap.Config.RGB_565);
        Canvas can = new Canvas(backDrop);
        can.drawBitmap(image, leftPadding, topPadding, null);

        return backDrop;
    }

    private void showNotifications(final String notificationTitle, final String notificationMessage, Context context) {

        try {
            Intent intent = new Intent(context, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context);
            Notification notification = builder.setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.ic_mydukan_logo).setTicker(notificationTitle).setWhen(0)
                    .setAutoCancel(true).setContentTitle(notificationTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationTitle))
                    .setSound(defaultSoundUri)
                    .setContentText(notificationMessage).build();

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(01, notification);

        } catch (Exception ex) {
            Log.d(MyDukan.LOGTAG, "addNotifications onFailure: " + ex.getMessage());
        }
    }

}
