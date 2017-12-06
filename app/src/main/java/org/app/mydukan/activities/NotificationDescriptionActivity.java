package org.app.mydukan.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Notification;
import org.app.mydukan.utils.AppContants;

import java.util.HashMap;

public class NotificationDescriptionActivity extends Activity {

    //Variables
    private MyDukan mApp;
    private HashMap mNotification;
    private String mSupplierName;

    //UI reference
    private TextView mNotificationTextView;
    private TextView mSupplierNameView;
    private ImageView mNotificationImage;
    private ImageButton backButton,shareButton;

    private WebView wv1;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_description);

        mApp = (MyDukan) getApplicationContext();
        //initialization of adview in this activity//

        MobileAds.initialize(this,"ca-app-pub-1640690939729824/9566634197");
        AdView cALdview=(AdView)findViewById(R.id.adView_MainFragment_one);//adView_MainFragment
        AdRequest adRequest= new AdRequest.Builder().build();
        cALdview.loadAd(adRequest);

        //end of adview mobAds//
        mSupplierNameView= (TextView) findViewById(R.id.tv_msgTitle);
        mNotificationTextView= (TextView) findViewById(R.id.tv_msgBody);
        mNotificationImage = (ImageView) findViewById(R.id.img_Notification);
        backButton = (ImageButton) findViewById(R.id.ibtn_Back);
        shareButton = (ImageButton) findViewById(R.id.ibtn_share);

        wv1=(WebView)findViewById(R.id.webview);
        wv1.setWebViewClient(new MyBrowser());
        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(AppContants.NOTIFICATION)) {
                mNotification = (HashMap) bundle.getSerializable(AppContants.NOTIFICATION);
            }
            setLayoutView(mNotification);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationDescriptionActivity.this, MainActivity.class));
                finish();
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTheLink(mNotification);
            }
        });
    }
    private void shareTheLink(HashMap nNotification) {
        String nTitle= String.valueOf(nNotification.get("notificationTitle"));
        String nMessage= String.valueOf(nNotification.get("notificationMessage"));
        String nImageURL= String.valueOf(nNotification.get("notificationImage"));

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_msg_subject));
        String url = "https://play.google.com/store/apps/details?mCatId=org.app.mydukan";
        share.putExtra(Intent.EXTRA_TEXT,nTitle + "\n "+nMessage+ "\n " + "\n"+getResources().getString(R.string.share_msg_text) + "\n " + url);
        startActivity(Intent.createChooser(share, "Share link!"));
    }



    /*
    notificationInfo.put("notificationTitle",notificationTitle);
                            notificationInfo.put("notificationMessage",notificationMessage);
                            notificationInfo.put("notificationImage",notificationImage);
                            notificationInfo.put("mUid",mUid);
     */


    private void setLayoutView(HashMap mNotification) {

        String mTitle= String.valueOf(mNotification.get("notificationTitle"));
        String mMessage= String.valueOf(mNotification.get("notificationMessage"));
        String mImageURL= String.valueOf(mNotification.get("notificationImage"));

        mSupplierNameView.setText(mTitle );
        mNotificationTextView.setText(mMessage);
        if (mImageURL!=null && !mImageURL.isEmpty()){
           /* Picasso.with(this).load(mImageURL)
                    .resize(400,400)
                    .into(mNotificationImage);

                      @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        */

            wv1.getSettings().setLoadsImagesAutomatically(true);
            wv1.getSettings().setJavaScriptEnabled(true);
            wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wv1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            wv1.getSettings().setBuiltInZoomControls(true);
            wv1.getSettings().setDisplayZoomControls(false);
            wv1.setHorizontalScrollbarOverlay(true);
            wv1.setScrollbarFadingEnabled(true);

            wv1.getSettings().setLoadWithOverviewMode(true);
            wv1.getSettings().setUseWideViewPort(true);
            wv1.getSettings().setLoadsImagesAutomatically(true);
            wv1.loadUrl(mImageURL);

        }else{
         wv1.setVisibility(View.GONE);
        }
    }

    private class MyBrowser extends WebViewClient {

      @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress = new ProgressDialog(NotificationDescriptionActivity.this);
            mProgress.setTitle("Please wait");
            mProgress.setMessage("Page is loading..");
            mProgress.setCancelable(false);
            mProgress.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

}
