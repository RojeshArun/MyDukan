package org.app.mydukan.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.moengage.widgets.NudgeView;

import org.app.mydukan.R;
import org.app.mydukan.activities.Schemes.SchemeListActivity;
import org.app.mydukan.appSubscription.PriceDropSubscription;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppSubscriptionInfo;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.data.User;
import org.app.mydukan.fragments.ProductFragment;
import org.app.mydukan.fragments.ProductPagerFragment;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;



import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

//Implementing the interface OnTabSelectedListener to our MainActivity
//This interface would help in swiping views

public class ProductListActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, ProductFragment.ProductFragmentListener {

    public HashMap<String, ArrayList<Product>> mProductList;
    LayerDrawable icon;
    boolean isProductFetched = false;
    //This is our tablayout
    private TabLayout tabLayout;
    private Toolbar mBottomToolBar;
    //This is our viewPager
    private ViewPager viewPager;
    private TextView mNoDataView;
    //Store products

    private MyDukan mApp;
    private String mCategoryId;
    private SupplierBindData mSupplier;
    private String mSerachText;
    private User userdetails;
    private RelativeLayout mlayout;
    private HashMap<String, Integer> priceRange;
    int pmin, pmax;
    String ptype="";
    SharedPreferences sharedPreferences;
    String lounchedDate_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mApp = (MyDukan) getApplicationContext();

        NudgeView nv = (NudgeView) findViewById(R.id.nudge);
        nv.initialiseNudgeView(this); //pass the activity context

        mlayout = (RelativeLayout) findViewById(R.id.layout_enable);
        //Adding toolbar to the activity
        setupActionBar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.SUPPLIER)) {
                mSupplier = (SupplierBindData) bundle.getSerializable(AppContants.SUPPLIER);
            }
            if (bundle.containsKey(AppContants.CATEGORY_ID)) {
                mCategoryId = bundle.getString(AppContants.CATEGORY_ID);
                Log.e("Category", mCategoryId);
            }
            if (bundle.containsKey(AppContants.USER_DETAILS)) {
                userdetails = (User) bundle.getSerializable(AppContants.USER_DETAILS);
            }
            if (bundle.containsKey(AppContants.PRICE_TYPE)) {
                ptype = bundle.getString(AppContants.PRICE_TYPE);
                Log.e("Ptype", ptype);

            }
            if (bundle.containsKey(AppContants.PRICE_RANGE)){
                Log.d("PRICERANGE", "Found");
                priceRange = (HashMap<String, Integer>) bundle.getSerializable(AppContants.PRICE_RANGE);
                pmin = priceRange.get("Min");
                pmax = priceRange.get("Max");
            }
        }


        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        mNoDataView = (TextView) findViewById(R.id.nodata_view);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mBottomToolBar = (Toolbar) findViewById(R.id.bottomToolbar);
        mBottomToolBar.findViewById(R.id.schemeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductListActivity.this, SchemeListActivity.class);
                intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
                intent.putExtra(AppContants.SUPPLIER_NAME, mSupplier.getName());
                startActivity(intent);
            }
        });

        mBottomToolBar.findViewById(R.id.complaintBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductListActivity.this, ServiceProviders.class);
                //intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
                //intent.putExtra(AppContants.SUPPLIER_NAME, mSupplier.getName());
                startActivity(intent);
            }
        });

        mBottomToolBar.findViewById(R.id.myorderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductListActivity.this, OrderListActivity.class);
                intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
                intent.putExtra(AppContants.SUPPLIER_NAME, mSupplier.getName());
                startActivity(intent);
            }
        });
        mBottomToolBar.findViewById(R.id.recordsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent record = new Intent(ProductListActivity.this, RecordsActivity.class);
                startActivity(record);
            }
        });


        if (!mSupplier.isCartEnabled()) {
            mBottomToolBar.findViewById(R.id.myorderBtn).setVisibility(View.GONE);
        }
        mBottomToolBar.setVisibility(View.GONE);

        if (!mApp.getUtils().isStringEmpty(mSupplier.getId())) {
            getProductList();
        } else {
            return;
        }
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.Products));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getProductList() {
        showProgress();
        tabLayout.removeAllTabs();
        viewPager.removeAllViews();
        viewPager.setAdapter(null);
        String uid = mApp.getFirebaseAuth().getCurrentUser().getUid();
        try {
            ApiManager.getInstance(getApplicationContext()).getSupplierProductList(mSupplier, mCategoryId,
                    mSerachText, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isProductFetched) {
                                mProductList = (HashMap<String, ArrayList<Product>>) data;
                                ArrayList<Product> list = new ArrayList<Product>();

                                if (!mProductList.isEmpty()) {
                                    mNoDataView.setVisibility(View.GONE);
                                    viewPager.setVisibility(View.VISIBLE);
                                    if(!ptype.equals("")){
                                        for(Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()){
                                            ArrayList<Product> prods = entry.getValue();
                                            for(Iterator<Product> iterator = prods.iterator(); iterator.hasNext();){
                                                Product p = iterator.next();
                                                if(ptype.equals("DP")) {
                                                    if (p.getDpInt() > pmax || p.getDpInt() < pmin) {
                                                        iterator.remove();
                                                    }
                                                }
                                                else if(ptype.equals("MOP")){
                                                    if (p.getMopInt() > pmax || p.getMopInt() < pmin) {
                                                        iterator.remove();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    HashMap<String, ArrayList<Product>> filteredList = new HashMap<String,ArrayList<Product>>();
                                    for (Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()) {
                                        if(entry.getValue().size() == 0){
                                            continue;
                                        }
                                        filteredList.put(entry.getKey(), entry.getValue());
                                    }

                                    if (mCategoryId.equals("-KX41ilBK4hjaSDIV419")) {  // mCatId = "-KX41ilBK4hjaSDIV419" name = "PRICE DROP"
                                        try{
                                            for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {

                                                Collections.sort(entry.getValue(), new DateComparator());
                                                Collections.reverse(entry.getValue());
                                                Log.i("error", "error at: " + entry.getKey());
                                                //mList.put(entry.getKey(), entry.getValue());
                                                //Adding the tabs using addTab() method
                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                            }
                                            //Collections.sort(list,new DateComparator());
                                       /*     //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(mProductList.values()), mSupplier.isCartEnabled(), mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);*/


                                            //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);


                                        }catch (Exception e){
                                            e.printStackTrace();
                                            for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {
                                                //Adding the tabs using addTab() method
                                                if(entry.getValue().size() == 0){
                                                    continue;
                                                }
                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                            }
                                            //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);
                                        }

                                    } else {
                                        for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {
                                            //Adding the tabs using addTab() method
                                            if(entry.getValue().size() == 0){
                                                continue;
                                            }
                                            tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                        }
                                        //Creating our pager adapter
                                        ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                        //Adding adapter to pager
                                        viewPager.setAdapter(adapter);
                                    }
                                } else {
                                    mNoDataView.setText("There is no model in this Price Range, Swipe to check other categories");
                                    mNoDataView.setVisibility(View.VISIBLE);
                                    viewPager.setVisibility(View.GONE);
                                }
                                dismissProgress();
                                isProductFetched = true;
                            }
                        }

                        @Override
                        public void onFailure(String response) {
                            dismissProgress();
                        }
                    });
        } catch (Exception e) {
            dismissProgress();
        }
    }


    private void addProductToCart(Product product, long quantity) {
        showProgress();
        HashMap<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("productid", product.getProductId());
        orderInfo.put("productname", product.getName());
        orderInfo.put("price", product.getPrice());
        orderInfo.put("quantity", quantity);

        ApiManager.getInstance(ProductListActivity.this).addOrderToCart(mSupplier.getId(),
                mApp.getFirebaseAuth().getCurrentUser().getUid(), orderInfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        dismissProgress();
                        String response = (String) data;
                        if (response.contains(getString(R.string.status_success))) {
                            String[] str_count = response.split("::");
                            setTheBadgeCount(Integer.valueOf(str_count[1]));
                        } else {
                            showErrorToast(ProductListActivity.this, response);
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                    }
                });
    }

    private void addProductToClaimList(Product product, String imeiNo) {
        showProgress();
        ApiManager.getInstance(ProductListActivity.this).addProductToClaim(mSupplier.getId(), mSupplier.getName(),
                product, imeiNo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        String response = (String) data;
                        dismissProgress();
                        if (response.equalsIgnoreCase(getString(R.string.status_success))) {
                            if (!isFinishing())
                                showRecordDialog();
                        } else {
                            showErrorToast(ProductListActivity.this, response);
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        showErrorToast(ProductListActivity.this, response);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_productactivity, menu);

        final MenuItem mSearchItem = menu.findItem(R.id.menu_search);
        mSearchItem.setVisible(true);

        final MenuItem mCart = menu.findItem(R.id.menu_cart);
        if (mSupplier.isCartEnabled()) {
            mCart.setVisible(true);
        } else {
            mCart.setVisible(false);
        }
        icon = (LayerDrawable) mCart.getIcon();

        //This search is used only for the Contact List.
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        try {
            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        } catch (Exception e) {
            Log.e("myDukan", "Exception in SeachManger " + e.getMessage());
        }

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setIconified(true);
                searchView.setQuery("", false);
                return false;
            }
        });

        //This is called when there is any text changed in the searchview.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String newText) {
                // TODO Auto-generated method stub
                searchView.clearFocus();

                //Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                if (mApp.getUtils().isStringEmpty(newText)) {
                    searchView.setIconified(true);
                    searchView.setQuery("", false);
                    mSerachText = null;
                    //reset the view.
                    isProductFetched = false;
                    getProductList();
                } else {
                    mSerachText = !TextUtils.isEmpty(newText) ? newText : null;
                    if (mSerachText != null) {
                        isProductFetched = false;
                        getProductList();
                        //mSearchItem.collapseActionView();
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSerachText = null;
                //reset the view.
                isProductFetched = false;
                getProductList();
                return true;
            }
        });

        return true;
    }

    public void setTheBadgeCount(double qty) {
        new BadgeDrawable(this, icon, String.valueOf((int) qty));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_cart:
                Intent intent = new Intent(ProductListActivity.this, PlaceOrderActivity.class);
                intent.putExtra(AppContants.SUPPLIER, mSupplier);
                startActivity(intent);
                return true;

            case R.id.product_notification:

                Answers.getInstance().logCustom(new CustomEvent("Notification Menubar")
                        .putCustomAttribute("Name", mSupplier.getName()));
//                ShortcutBadger.removeCount(CategoryListActivity.this); //for 1.1.4+
                Intent notification_intent = new Intent(ProductListActivity.this, NotificationListActivity.class);
                notification_intent.putExtra(AppContants.SUPPLIER, mSupplier);
                notification_intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
                notification_intent.putExtra(AppContants.SUPPLIER_NAME, mSupplier.getName());
                startActivity(notification_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //=====================add the imei number here...
    private void showTheClaimAlert(final Product product) {
        final AlertDialog.Builder claimAlert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(ProductListActivity.this);
        edittext.setHint(getString(R.string.hint_imei));
        claimAlert.setTitle(getString(R.string.imei_title));

        claimAlert.setView(edittext);

        claimAlert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String imei_str = edittext.getText().toString();
                addProductToClaimList(product, imei_str);

            }
        });

        claimAlert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        claimAlert.show();
    }

    // =====================add  imei ends=============================

    // =====================add item to cart(onClick buy button)=============================

    private void showTheCartAlert(final Product product) {
        final AlertDialog.Builder cartAlert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(ProductListActivity.this);
        edittext.setHint(getString(R.string.quantity));
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        cartAlert.setTitle(getString(R.string.quantity_title));
        cartAlert.setView(edittext);
        cartAlert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String quantity_str = edittext.getText().toString();
                if (!mApp.getUtils().isStringEmpty(quantity_str)) {
                    if (Integer.valueOf(quantity_str) > 0) {
                        addProductToCart(product, Long.valueOf(quantity_str));
                    } else {
                        showOkAlert(ProductListActivity.this, getString(R.string.info),
                                getString(R.string.error_quantity), getString(R.string.ok));
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

    //=========================================================
    @Override
    public void addProductToCart(Product product) {
        showTheCartAlert(product);
    }

    @Override
    public void addProductToClaim(Product product) {
        // Show a dialog to take the key.
        // showTheClaimAlert(product);
        Intent intent = new Intent(ProductListActivity.this, AddIMEIActivity.class);
        intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
        intent.putExtra(AppContants.PRODUCT, product);
        intent.putExtra(AppContants.SUPPLIER, mSupplier);
        startActivity(intent);

    }

    private void showRecordDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductListActivity.this);

        // set title
        alertDialogBuilder.setTitle("Added Successfully");
        alertDialogBuilder.setIcon(R.drawable.ic_action_about);

        // set dialog message
        alertDialogBuilder
                .setMessage("Price drop product added to your list")
                .setCancelable(true)
                .setPositiveButton(R.string.gotomyrecords, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent record = new Intent(ProductListActivity.this, RecordsActivity.class);
                        startActivity(record);
                        finish();
                    }
                })
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private class DateComparator implements Comparator<Product> {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     /*   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");*/

        public int compare(Product p1, Product p2) {
            //descending getPriceDrop().getStartdate();
            int result = 0;
            try {
                if ((p1.getPriceDrop() != null) && (p1.getPriceDrop() != null)) {
                    long mdate1 = p1.getPriceDrop().getStartdate();
                    long mdate2 = p2.getPriceDrop().getStartdate();
                    Date d1 = null;
                    Date d2 = null;
                    d1 = new Date(p1.getPriceDrop().getStartdate());
                    d2 = new Date(p2.getPriceDrop().getStartdate());

                    if (d1 == null || d2 == null)
                        return result;
                    result = d1.compareTo(d2);
                    //result = d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
             /*
            public String convertTime(long time){
            Date date = new Date(time);
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            return format.format(date);
        }
               d1 = new Date(mdate1);
               d2 = new Date(mdate2);
               if ( d1 == null) {
                   result=-1;
                   return result ;
               }
               if (d2 == null) {
                   result=1;
                   return result ;
               }
               result= d1.compareTo(d2) * -1;
               return  compare(mdate1, mdate2);
               if (result != 0) {
                   return result;
               }else{
                   return 0;
               }

// if nothing is returned from the method this is returned by default
            return 0;*/
        }
    }
}
