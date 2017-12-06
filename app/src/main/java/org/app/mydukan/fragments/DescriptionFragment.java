package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.activities.ProductDescriptionActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import static org.app.mydukan.activities.ProductDescriptionActivity.mApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {

    View mView;
    Context context;
    private Product mProduct;


    private String mProductDesc;
    private SupplierBindData mSupplier;

    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mOthersHeaderView;
    private TextView mOthersTextView;
    private TextView mDescTextView;
    private TextView mStockTextView;
    LinearLayout addTocart_Btn;
    private WebView mDescWebView;
    private ProgressDialog mProgress;
    Product product;
    boolean isCartShow=false;
    private Button btn_DownloadProductPage;

    public DescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_description, container, false);
        context = mView.getContext();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            try {
                mProduct = (Product) extras.getSerializable(AppContants.PRODUCT);
                mSupplier = (SupplierBindData) extras.getSerializable(AppContants.SUPPLIER);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        mDescWebView = (WebView) mView.findViewById(R.id.descWebView);
        mDescTextView = (TextView) mView.findViewById(R.id.descTextView);

        btn_DownloadProductPage = (Button) mView.findViewById(R.id.btn_DownloadProductPage);
        btn_DownloadProductPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
//        setupDescription();
        fetchProductAndShow();
        return mView;

    }


    public void showProgress() {

        try {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
        } catch (Exception e) {

        }
    }

    public void dismissProgress() {
        try {
            if ( mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = null;
        } catch (Exception e) {

        }
    }

    private void fetchProductAndShow(){
        showProgress();
        ApiManager.getInstance(context).getProductDetails(mProduct.getProductId(),
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        product = (Product)data;
                        if(product != null) {
                            mProduct.setDescription(product.getDescription());
                            mProduct.setUrl(product.getUrl());
                            mProduct.setAttributes(product.getAttributes());
                        }

                        dismissProgress();
                        setupDescription();
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        setupDescription();
                    }
                });
    }

    private void setupDescription(){
        String url = mProduct.getUrl();
        String desc = mProduct.getDescription();
        if(!mApp.getUtils().isStringEmpty(url)){
            mProductDesc = url;
        } else if(mApp.getUtils().isStringEmpty(desc)){
            if(getActivity()!=null){
                mProductDesc =getActivity().getResources().getString( R.string.Specifications_Not_Available1 );
                btn_DownloadProductPage.setVisibility(View.GONE);
            }else{
                mProductDesc="Not_Available";
                btn_DownloadProductPage.setVisibility(View.GONE);
            }
        }

        mDescWebView.getSettings().setJavaScriptEnabled(true);
        mDescWebView.setWebViewClient(new MyWebViewClient());
        mDescWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mDescWebView.getSettings().setBuiltInZoomControls(true);
        mDescWebView.getSettings().setDisplayZoomControls(false);
        mDescWebView.setHorizontalScrollbarOverlay(true);
        mDescWebView.setScrollbarFadingEnabled(true);
        mDescWebView.getSettings().setLoadsImagesAutomatically(true);

        if(!mApp.getUtils().isStringEmpty(mProductDesc)) {
            if (mProductDesc.contains("https://") || mProductDesc.contains("http://")) {
                mDescWebView.loadUrl(mProductDesc);
            } else {
                mDescWebView.loadData(mProductDesc, "text/html; charset=UTF-8", null);
            }
        }

        if(!mApp.getUtils().isStringEmpty(desc)){
            mDescTextView.setText(desc);
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
            mProgress = new ProgressDialog(context);
          //  mProgress.setTitle(getString(R.string.Please_wait));
            mProgress.setMessage(getString(R.string.Page_is_loading));
            mProgress.setCancelable(true);
            mProgress.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null) {
                {
                    mProgress.dismiss();
                }
                mProgress = null;
            }
        }
    }
}
