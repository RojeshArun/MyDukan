package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.FeedProfileFollowActivity;
import org.app.mydukan.activities.ProductDescriptionActivity;
import org.app.mydukan.adapters.CustomBaseAdapter;
import org.app.mydukan.adapters.KeySpecificationAdapter;
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
public class KeySpecification extends Fragment {


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

    ListView ksListView;

    public KeySpecification() {
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
        mView =  inflater.inflate(R.layout.fragment_key_specification, container, false);
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
        mOthersHeaderView = (TextView) mView.findViewById(R.id.othersHeader);
        mOthersTextView = (TextView) mView.findViewById(R.id.othersTextView);
        ksListView = (ListView)  mView.findViewById(R.id.list_Keyspecification);
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
                        setupOthers();
                    }
                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        setupOthers();
                    }
                });
    }

    private void setupOthers(){
        String othersStr = "";

        for (String attKey: mProduct.getAttributes().keySet()) {
            String value = mProduct.getAttributes().get(attKey);

            if(!mApp.getUtils().isStringEmpty(othersStr) && !othersStr.endsWith(", ")){
                othersStr += ", ";
            }

            if(!mApp.getUtils().isStringEmpty(value)){
                if(attKey.equalsIgnoreCase("androidversion")){
                    othersStr += value + " " + getString(R.string.androidversion);
                } else if(attKey.equalsIgnoreCase("cameramegapixel")){
                    othersStr += value + " " + getString(R.string.camera);
                } else if(attKey.equalsIgnoreCase("displaysize")){
                    othersStr += value + " " + getString(R.string.size);
                } else if(attKey.equalsIgnoreCase("ramrom")){
                    othersStr += value + " " + getString(R.string.ram);
                }
            }
        }

        if(!mApp.getUtils().isStringEmpty(othersStr)){
           // mOthersTextView.setText(othersStr);
            // showProgress(false);
         // KeySpecificationAdapter adapter = new KeySpecificationAdapter(context,  mProduct.getAttributes());
           // ksListView.setAdapter(adapter);
            KeySpecificationAdapter adapter = new KeySpecificationAdapter(context, mProduct.getAttributes());
            ksListView.setAdapter(adapter);

        } else {
            mOthersTextView.setVisibility(View.GONE);
            mOthersTextView.setText("ot available");
        }
    }

}
