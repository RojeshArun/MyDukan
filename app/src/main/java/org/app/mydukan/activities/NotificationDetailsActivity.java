package org.app.mydukan.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Notification;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.utils.AppContants;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class NotificationDetailsActivity extends BaseActivity {

    //UI reference
    private TextView mNotificationTextView;
    private TextView mSupplierNameView;
    private WebView mDescriptionView;
    private ProgressDialog mProgress;

    //Variables
    private MyDukan mApp;
    private Notification mNotification;
    private String mSupplierName;
    private TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationdetails);

        mApp = (MyDukan) getApplicationContext();

        //Set up the actionbar
        setupActionBar();

        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(AppContants.NOTIFICATION)) {
                mNotification = (Notification) bundle.getSerializable(AppContants.NOTIFICATION);
            }

            if (bundle.containsKey(AppContants.SUPPLIER_NAME)){
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
        }

        mNotificationTextView = (TextView) findViewById(R.id.notificationtext);
        mSupplierNameView = (TextView) findViewById(R.id.supplierName);
        mDescriptionView = (WebView) findViewById(R.id.description);

        setSchemeData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.notificationdetails_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setSchemeData(){
        //Scheme details
        mNotificationTextView.setText(mNotification.getNotificationText().toUpperCase());
        setupDescriptionView();

        //Supplier details
        mSupplierNameView.setText(mSupplierName.toUpperCase());
    }

    private void setupDescriptionView(){
        mDescriptionView.getSettings().setJavaScriptEnabled(true);
        mDescriptionView.setWebViewClient(new MyWebViewClient());
        mDescriptionView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mDescriptionView.getSettings().setBuiltInZoomControls(true);
        mDescriptionView.getSettings().setDisplayZoomControls(false);
        mDescriptionView.setHorizontalScrollbarOverlay(true);
        mDescriptionView.setScrollbarFadingEnabled(true);
        mDescriptionView.getSettings().setLoadsImagesAutomatically(true);

        mDescriptionView.getSettings().setLoadWithOverviewMode(true);
        mDescriptionView.getSettings().setUseWideViewPort(true);

        String notificationDesc = "";
        String url = mNotification.getUrl();
        String desc = mNotification.getMessage();
        if(!mApp.getUtils().isStringEmpty(url)){
            notificationDesc = url;
        } else if(!mApp.getUtils().isStringEmpty(desc)){
            notificationDesc = desc;
        } else {
            notificationDesc = getString(R.string.Specifications_Not_Available);
        }

        if (notificationDesc.contains("https://") ||notificationDesc.contains("http://")) {
            mDescriptionView.loadUrl(notificationDesc);
        } else {
            mDescriptionView.loadData(notificationDesc, "text/html; charset=UTF-8", null);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress = new ProgressDialog(NotificationDetailsActivity.this);
            mProgress.setTitle("Please wait");
            mProgress.setMessage("Page is loading..");
            mProgress.setCancelable(false);
            mProgress.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }
}
