package org.app.mydukan.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jraska.falcon.Falcon;
import com.squareup.picasso.Picasso;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.QRCodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GenerateQRCodeActivity extends BaseActivity implements OnClickListener {

    private String LOG_TAG = "GenerateQRCode";
    private MyDukan mApp;
    private String myDukhan_UserId;
    private User userdetails;
    private int mViewType = 2;

    //TextView
    EditText qrInput;
    ImageView qrcode_Image,shop_Image;
    LinearLayout shopimage_layout;
    LinearLayout bottomBar_LayoutButtons;
    private Button saveBtn,shareBtn,nextBtn;
   TextView printName,printAddress,printPhoneNumber;

    File file;
    private static Bitmap bitMap;
    private View mView;
    User user=new User();
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        showProgress();
        mApp = (MyDukan) getApplicationContext();
        printName= (TextView) findViewById(R.id.tv_UserName);
        printAddress= (TextView) findViewById(R.id.tv_UserAddress);
        printPhoneNumber = (TextView) findViewById(R.id.tv_UserPhoneNumber);
        qrcode_Image = (ImageView) findViewById(R.id.img_QRcode);
        shop_Image = (ImageView) findViewById(R.id.img_ShopImage);
        shopimage_layout = (LinearLayout) findViewById(R.id.layoutshop_img);
        saveBtn = (Button) findViewById(R.id.save_btn);
        shareBtn = (Button) findViewById(R.id.editProfile_btn);
        nextBtn= (Button) findViewById(R.id.next_btn);
        bottomBar_LayoutButtons= (LinearLayout) findViewById(R.id.linearLayout_Buttons);
        nextBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        bottomBar_LayoutButtons.setVisibility(View.GONE);
      //  myDukhan_UserId = mApp.getFirebaseAuth().getCurrentUser().getUid();

       Bundle extras = getIntent().getExtras();
        if (extras != null) {
          //  userdetails =  getIntent().getExtras().getParcelable("UserDetails"); //The key argument here must match that used in the other activity
            myDukhan_UserId =extras.getString("MyDhukhan_UserId");
            userdetails = (User) getIntent().getExtras().getSerializable("UserDetails");
          if (userdetails!=null){
              initView(GenerateQRCodeActivity.this, myDukhan_UserId, userdetails);//Initialise the Activity View.
            //  upLoadQRCode(bitmap);
          }
        }else{
            getUserProfile();
        }
    }

    private void upLoadQRCode(Bitmap bitmap) {
        showProgress();
        if (bitmap != null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] data = bytes.toByteArray();

            ApiManager.getInstance(GenerateQRCodeActivity.this).uploadqrcode(data, new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    Uri qrcode_card = (Uri) data;
                    if (qrcode_card != null) {
                        user.setQrcodedurl(qrcode_card.toString());
                        ///update to firebase db here....
                        addQrCodeUrl(user.getQrcodedurl());
                    }
                    return;
                }
                @Override
                public void onFailure(String response) {
                    dismissProgress();
                }
            });
        }
    }

    private void getUserProfile() {
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(GenerateQRCodeActivity.this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    userdetails = (User) data;
                    myDukhan_UserId=mApp.getFirebaseAuth().getCurrentUser().getUid();
                    if (userdetails!=null){
                        initView(GenerateQRCodeActivity.this, myDukhan_UserId, userdetails);//Initialise the Activity View.
                    }
                    dismissProgress();
                    return;
                }
            }
            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase
                dismissProgress();
            }
        });
    }


    private void initView(Context context, String myDhukhan_userId, User userdetails) {
        showProgress();

        String myDhukhan_UserAddress=userdetails.getUserinfo().getAddressinfo().getStreet().toString().trim() +
                "," + userdetails.getUserinfo().getAddressinfo().getCity().toString().trim()+
                "," + userdetails.getUserinfo().getAddressinfo().getState().toString().trim() +
                "," + userdetails.getUserinfo().getAddressinfo().getCountry().toString().trim() +
                "," + userdetails.getUserinfo().getAddressinfo().getPincode().toString().trim();

        String qrInputText =userdetails.getUserinfo().getName().toString()+" ||| "+ myDukhan_UserId +" ||| "+myDhukhan_UserAddress + " ||| " + userdetails.getUserinfo().getNumber().toString();
        Log.v(LOG_TAG, qrInputText);


        printName.setText( mApp.getUtils().toSameCase(userdetails.getUserinfo().getName().trim().toString()));
        printPhoneNumber.setText("PhoneNumber: "+userdetails.getUserinfo().getNumber().toString());
        printAddress.setText("Address: "+mApp.getUtils().toSameCase(userdetails.getUserinfo().getAddressinfo().getStreet().trim().toString()));
        //draw the QR code for passed value(QR code is Text_Type)
        dwawQRCode(context, qrInputText);

        //Loading Image from URL
        String mImageShop =  userdetails.getCompanyinfo().getCardurl();
        if(mImageShop!=null){
            mImageShop= mImageShop.toString();
            if(!mImageShop.isEmpty()){
                shopimage_layout.setVisibility(View.VISIBLE);
                Picasso.with(this).load(userdetails.getCompanyinfo().getCardurl())
                        .resize(400,400).placeholder(R.drawable.ic_loading)                      // optional
                        .into(shop_Image,new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //Success image already loaded into the view
                        dismissProgress();
                        bottomBar_LayoutButtons.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        //Error placeholder image already loaded into the view, do further handling of this situation here
                        //shopimage_layout.setVisibility(View.GONE);
                        bottomBar_LayoutButtons.setVisibility(View.VISIBLE);
                        dismissProgress();
                    }
                }
                );
            }else{
               // shopimage_layout.setVisibility(View.GONE);
                bottomBar_LayoutButtons.setVisibility(View.VISIBLE);
            }
        }else{
           // shopimage_layout.setVisibility(View.GONE);
            bottomBar_LayoutButtons.setVisibility(View.VISIBLE);
        }
        //Loading QRCOde from URL
      /*  String mQRCode =  userdetails.getQrcodedurl();
        if((mQRCode==null)||(mQRCode.isEmpty())){
            dwawQRCode(context, qrInputText);
        }else{
            Picasso.with(this).load(mQRCode)
                    .resize(400,400).placeholder(R.color.cardview_shadow_start_color)                      // optional
                    .into(qrcode_Image);
            dismissProgress();
        }
*/
    }

    private void dwawQRCode(Context context, String qrInputText) {
        try { //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);

             bitmap = qrCodeEncoder.encodeAsBitmap();
            qrcode_Image.setImageBitmap(bitmap);

            dismissProgress();
        } catch (WriterException e) {
            dismissProgress();
            e.printStackTrace();
        }

    }
    private void addQrCodeUrl(String status) {
        ApiManager.getInstance(GenerateQRCodeActivity.this).setQRCodeURL(myDukhan_UserId, status,new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    dismissProgress();
                    Intent mainactivity = new Intent(GenerateQRCodeActivity.this, MainActivity.class);
                    startActivity(mainactivity);
                    finish();
                }
            }

            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase
                dismissProgress();
            }
        });
    }
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save_btn:{

                bottomBar_LayoutButtons.setVisibility(View.GONE);
              /* //Create Path to save Image
                String filepath = Environment.getExternalStorageDirectory().toString();
                File dir = new File(filepath + "/folder name/");
                dir.mkdirs();
                File file = new File(dir, bitmap + ".png"); // Imagename.png
                // Falcon.takeScreenshot(this,saveBitMap(GenerateQRCodeActivity.this));
              */
               takeScreenshot();
                bottomBar_LayoutButtons.setVisibility(View.VISIBLE);

            }
            break;

            case R.id.editProfile_btn:
            {
                Intent profile = new Intent(this, UserProfile.class);
                profile.putExtra(AppContants.VIEW_TYPE, AppContants.MY_PROFILE);
                startActivity(profile);
                finish();

            }
            break;

            case R.id.next_btn:{
                bottomBar_LayoutButtons.setVisibility(View.GONE);
                bitmap= Falcon.takeScreenshotBitmap(this);
                upLoadQRCode(bitmap);
                bottomBar_LayoutButtons.setVisibility(View.VISIBLE);
              /*  Intent mainactivity = new Intent(this, MainActivity.class);
                startActivity(mainactivity);
                finish();
                */
            }
            break;

            default:
                break;
            // More buttons go here (if any) ...
        }
    }
    public void takeScreenshot() {
        File screenshotFile = getScreenshotFile();

        Falcon.takeScreenshot(this, screenshotFile);

       // String message = "Screenshot captured to " + screenshotFile.getAbsolutePath();
        Toast.makeText(this, " Image has been saved successfully", Toast.LENGTH_LONG).show();

        Uri uri = Uri.fromFile(screenshotFile);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(scanFileIntent);
    }
    protected File getScreenshotFile() {
        File screenshotDirectory;
        try {
            screenshotDirectory = getScreenshotsDirectory(getApplicationContext());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault());

        String screenshotName = "mydukan"+dateFormat.format(new Date()) + ".png";
        return new File(screenshotDirectory, screenshotName);
    }

    private static File getScreenshotsDirectory(Context context) throws IllegalAccessException {
        String dirName = "screenshots" ;

        File rootDir;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            rootDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            rootDir = context.getDir("screens", MODE_PRIVATE);
        }

        File directory = new File(rootDir, dirName);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalAccessException("Unable to create screenshot directory " + directory.getAbsolutePath());
            }
        }

        return directory;
    }


//===================Taking a screen and saving to sd card================
    /*public Bitmap getBitmapOFRootView(View v) {
        View rootview = v.getRootView();
        rootview.setDrawingCacheEnabled(true);
        Bitmap bitmap1 = rootview.getDrawingCache();
        return bitmap1;
    }
*/
    //=======================================================================

    private void shareImage(String s) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(s));
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    private File saveBitMap(Context context  ){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator+ System.currentTimeMillis()+".png";
        File pictureFile = new File(filename);
       /* try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        */
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

