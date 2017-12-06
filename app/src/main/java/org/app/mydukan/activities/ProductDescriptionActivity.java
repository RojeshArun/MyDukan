package org.app.mydukan.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.fragments.DescriptionFragment;
import org.app.mydukan.fragments.KeySpecification;
import org.app.mydukan.fragments.OneFragment;
import org.app.mydukan.fragments.TwoFragment;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shivayogi Hiremath on 23-10-2016.
 */
public class ProductDescriptionActivity extends BaseActivity {

    private Product mProduct;
    public static MyDukan mApp;
    private String mProductDesc;
    private SupplierBindData mSupplier;

    private TextView mNameTextView;
    private TextView mPriceTextView;
    //    private TextView mOthersHeaderView;
//    private TextView mOthersTextView;
    private TextView mDescTextView;
    private TextView mStockTextView;
    LinearLayout addTocart_Btn;
    private WebView mDescWebView;
    private ProgressDialog mProgress;
    Product product;
    boolean isCartShow=false;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        mApp = (MyDukan) getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                mProduct = (Product) extras.getSerializable(AppContants.PRODUCT);
                mSupplier = (SupplierBindData) extras.getSerializable(AppContants.SUPPLIER);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        setupActionBar();

        mNameTextView = (TextView) findViewById(R.id.productname);
        mPriceTextView = (TextView) findViewById(R.id.priceDetails);
//        mOthersHeaderView = (TextView) findViewById(R.mCatId.othersHeader);
//        mOthersTextView = (TextView) findViewById(R.mCatId.othersTextView);
//        mDescWebView = (WebView) findViewById(R.mCatId.descWebView);
//        mDescTextView = (TextView) findViewById(R.mCatId.descTextView);
        addTocart_Btn =(LinearLayout) findViewById(R.id.btn_AddTOCart);
        mStockTextView = (TextView) findViewById(R.id.tv_stockDetail);

        viewPager = (ViewPager) findViewById(R.id.viewpager_product);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs_product);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        fetchProductAndShow();


        //====================================================
        if(isCartShow){
            addTocart_Btn.setVisibility(View.VISIBLE);

            if(mProduct.getStockremaining() <= 0){
                addTocart_Btn.setVisibility(View.INVISIBLE);
                mStockTextView.setText("No Stock");
            } else if(mProduct.getStockremaining() <= 5){
                mStockTextView.setText("Limited Stock");
            } else {
                mStockTextView.setVisibility(View.GONE);
            }
        } else {
            addTocart_Btn.setVisibility(View.GONE);
        }
        //====================================================



        addTocart_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(product!=null){
                    showTheCartAlert(product);
                }
            }
        });

    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        if(!mSupplier.getId().equals("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2")){
            // Supplier id for free Version "RcJ1L4mWaZeIe2wRO3ejHOmcSxf2" =====
            final TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabOne.setText("Key Specifications");
            // tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
            tabLayout.getTabAt(0).setCustomView(tabOne);

            final TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabTwo.setText("Description");
            // tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
            tabLayout.getTabAt(1).setCustomView(tabTwo);
        }else{
            final TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabTwo.setText("Description");
            // tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
            tabLayout.getTabAt(0).setCustomView(tabTwo);
        }

       /* final TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Key Specifications");
        // tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);


        final TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Description");
        // tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);*/


      /*  TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("MyDukanPosts");
      //  tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);*/
    }

    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ProductDescriptionActivity.ViewPagerAdapter adapter = new ProductDescriptionActivity.ViewPagerAdapter(getSupportFragmentManager());
        /*adapter.addFrag(new KeySpecification(), "ONE");
        adapter.addFrag(new DescriptionFragment(), "TWO");*/
        if(!mSupplier.getId().equals("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2")){
            adapter.addFrag(new KeySpecification(), "ONE");
            adapter.addFrag(new DescriptionFragment(), "TWO");
        }else{
            adapter.addFrag(new DescriptionFragment(), "ONE");
        }
        //  adapter.addFrag(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
            getSupportActionBar().setTitle(getString(R.string.Productinfo));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchProductAndShow(){
        showProgress();
        ApiManager.getInstance(ProductDescriptionActivity.this).getProductDetails(mProduct.getProductId(),
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
                        setupData();
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        setupData();
                    }
                });
    }


    // =====================add item to cart(onClick buy button)=============================

    private void showTheCartAlert(final Product product){
        final AlertDialog.Builder cartAlert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(ProductDescriptionActivity.this);
        edittext.setHint(getString(R.string.quantity));
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        cartAlert.setTitle(getString(R.string.quantity_title));
        cartAlert.setView(edittext);
        cartAlert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String quantity_str = edittext.getText().toString();
                if(!mApp.getUtils().isStringEmpty(quantity_str)){
                    if(Integer.valueOf(quantity_str) > 0){
                        addProductToCart(product,Long.valueOf(quantity_str));
                    } else {
                        showOkAlert(ProductDescriptionActivity.this,getString(R.string.info),
                                getString(R.string.error_quantity),getString(R.string.ok));
                    }
                }
            }
        });

        cartAlert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        cartAlert.show();
    }

    private void addProductToCart(Product product, long quantity){
        showProgress();
        HashMap<String,Object> orderInfo = new HashMap<>();
        orderInfo.put("productid",product.getProductId());
        orderInfo.put("productname",product.getName());
        orderInfo.put("price", product.getPrice());
        orderInfo.put("quantity", quantity);

        ApiManager.getInstance(ProductDescriptionActivity.this).addOrderToCart(mSupplier.getId(),
                mApp.getFirebaseAuth().getCurrentUser().getUid(), orderInfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        dismissProgress();
                        String response = (String)data;
                        if(response.contains(getString(R.string.status_success))){
                            //String[] str_count = response.split("::");
                            //setTheBadgeCount(Integer.valueOf(str_count[1]));
                            BaseActivity.showOkAlert(ProductDescriptionActivity.this,"MyDukan","Product is added to CartList","OK");

                        } else {
                            showErrorToast(ProductDescriptionActivity.this,response);
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                    }
                });
    }



    //=========================================================
    private void setupData(){
        mNameTextView.setText(mProduct.getName());
        mPriceTextView.setText(mApp.getUtils().getPriceFormat(mProduct.getPrice()));

    }

}

