package org.app.mydukan.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.app.mydukan.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private WebView wv1;
    Button btn_Terms_AND_COND,btn_Purchase_Returns,btn_PravacyPolicy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        wv1=(WebView)findViewById(R.id.webview);

        wv1.setVisibility(View.GONE);
        btn_Terms_AND_COND= (Button) findViewById(R.id.btn_TERMS_AND_CONDITIONS);
        btn_Purchase_Returns= (Button) findViewById(R.id.btn_Purchase_and_Returns);
        btn_PravacyPolicy= (Button) findViewById(R.id.btn_privacy);


        btn_PravacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv1.setVisibility(View.VISIBLE);
                wv1.setWebViewClient(new MyBrowser());


                String url ="https://s3-ap-southeast-1.amazonaws.com/mydukan/images/Mydukan/Privacy+policy.html";

                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
                wv1.getSettings().setBuiltInZoomControls(true);
                wv1.getSettings().setDisplayZoomControls(false);
                wv1.setHorizontalScrollbarOverlay(true);
                wv1.setScrollbarFadingEnabled(true);
                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.loadUrl(url);



            }
        });

        btn_Terms_AND_COND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv1.setVisibility(View.VISIBLE);
                wv1.setWebViewClient(new MyBrowser());


                String url ="https://s3-ap-southeast-1.amazonaws.com/mydukan/images/Mydukan/Mydukan+Terms.Html";

                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
                wv1.getSettings().setBuiltInZoomControls(true);
                wv1.getSettings().setDisplayZoomControls(false);
                wv1.setHorizontalScrollbarOverlay(true);
                wv1.setScrollbarFadingEnabled(true);
                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.loadUrl(url);
            }
        });
        btn_Purchase_Returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv1.setVisibility(View.VISIBLE);
                wv1.setWebViewClient(new MyBrowser());

                String url ="https://s3-ap-southeast-1.amazonaws.com/mydukan/images/Mydukan/Mydukan+Refund.html";
                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv1.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
                wv1.getSettings().setBuiltInZoomControls(true);
                wv1.getSettings().setDisplayZoomControls(false);
                wv1.setHorizontalScrollbarOverlay(true);
                wv1.setScrollbarFadingEnabled(true);
                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.loadUrl(url);
            }
        });


    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
