package org.app.mydukan.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.app.mydukan.BuildConfig;
import org.app.mydukan.R;
import org.app.mydukan.activities.Schemes.SchemeListActivity;
import org.app.mydukan.adapters.CategoryAdapter;
import org.app.mydukan.appSubscription.PriceDropSubscription;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.AppSubscriptionInfo;
import org.app.mydukan.data.Category;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NetworkUtil;
import org.app.mydukan.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CategoryListActivity extends BaseActivity implements CategoryAdapter.ComplaintsAdapterListener, AdapterView.OnItemSelectedListener {


    private MyDukan mApp;

    private User userdetails;
    private String userID;
    private int mViewType = 1;

    private SupplierBindData supplierData;
    private String mSupplierId;


    private FloatingActionButton mWhatsAppBtn_payment;
    ScrollView scroll;

    AlertDialog dialog;

    private RecyclerView mRecyclerView;
    private Toolbar mBottomToolBar;
    private TextView mNoDataView;
    ArrayAdapter<String> categAdapter;

    RelativeLayout mlayout;
    LinearLayout subscribeAleartLayout;
    LayerDrawable icon;
    LayerDrawable iconLayer;
    Button go;
    int index = 0;
    private CategoryAdapter mAdapter;
    private ArrayList<Category> mCategoryList;
    private NetworkUtil networkUtil;

    static String open_PageName = "category";
    static  String price_Type="DP";
    static int minRange=500;
    static int maxRange=AppContants.PRICE_MAX;
    String mCatId;
    boolean notNew_user = true;

    private Button btn_Subscribe, btn_Trial, btn_DaysRemaing, mFilterBtn;
    private TextView daysRemain;


    SharedPreferences sharedPreferences;
    String lounchedDate_User;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    String remoteDisp_Subscription, remoteSearchServiCecenters;
    String enableProductPage = "false";

    private static final String LOADING_PHRASE_CONFIG_KEY3 = "showSubscriptionPage";
    private static final String LOADING_PHRASE_CONFIG_KEY4 = "servicecenters_enable";
    private static final String LOADING_PHRASE_CONFIG_KEY5 = "notificationIcon_enable";
    private static final String REMOTE_MESSAGE_KEY = "showSubscriptionPage";
    private static final String REMOTE_MESSAGE_CAPS_KEY = "showSubscriptionPage";
    private static final String REMOTE_MESSAGE_ENABLE_PROMOCODE = "enable_promocode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.SUPPLIER)) {
                supplierData = (SupplierBindData) bundle.getSerializable(AppContants.SUPPLIER);
                mSupplierId = supplierData.getId();

            }
        }
        mApp = (MyDukan) getApplicationContext();
        userID = mApp.getFirebaseAuth().getCurrentUser().getUid();
        networkUtil = new NetworkUtil();

        mWhatsAppBtn_payment = (FloatingActionButton) findViewById(R.id.whatsAppBtn_payment);
        scroll = (ScrollView) findViewById(R.id.scrollViewList);
        mlayout = (RelativeLayout) findViewById(R.id.mlayout_enable);
        subscribeAleartLayout = (LinearLayout) findViewById(R.id.layout_subscribe);
        btn_Subscribe = (Button) findViewById(R.id.btn_subscription);
        btn_Trial = (Button) findViewById(R.id.btn_trial);
        btn_DaysRemaing = (Button) findViewById(R.id.btn_remaingDays);
        daysRemain = (TextView) findViewById(R.id.tv_message_trialUser);
        //  mlayout.setVisibility(View.GONE);
        go = (Button) findViewById(R.id.findcat);
        mFilterBtn = (Button) findViewById(R.id.FilterBtn);
        mFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCategoryData();
                AddFilters();
            }
        });

        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);
        if (firstTime) {
            editor.putBoolean("first", false);
            //For commit the changes, Use either editor.commit(); or  editor.apply();.
            editor.commit();
            LoginActivity loginActivity = new LoginActivity();
            notNew_user = loginActivity.isNewUserForTrail;
            lounchedDate_User = Utils.getCurrentdate();
            // now the calling the method to clear the cache data or app data
        }

        mWhatsAppBtn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onWhatsAppBtnClick();
            }
        });
        //============================================
        mBottomToolBar = (Toolbar) findViewById(R.id.bottomToolbar);
        mBottomToolBar.findViewById(R.id.schemeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_PageName = "schemes"; // this will tell us which page has to open

                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                    subscribeAleartLayout.setVisibility(View.VISIBLE);
                } else {
                    Answers.getInstance().logCustom(new CustomEvent("Scheme click")
                            .putCustomAttribute("Name", supplierData.getName()));
                    Intent intent = new Intent(CategoryListActivity.this, SchemeListActivity.class);
                    intent.putExtra(AppContants.SUPPLIER_ID, supplierData.getId());
                    intent.putExtra(AppContants.SUPPLIER_NAME, supplierData.getName());
                    startActivity(intent);
                }
            }
        });

        mBottomToolBar.findViewById(R.id.recordsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent record = new Intent(CategoryListActivity.this, RecordsActivity.class);
                startActivity(record);
            }
        });
        mBottomToolBar.findViewById(R.id.complaintBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answers.getInstance().logCustom(new CustomEvent("Service Center")
                        .putCustomAttribute("Categorypage_ServiceCenter_button", "Service Center button clicked"));

                Intent intent = new Intent(CategoryListActivity.this, ServiceProviders.class);
                intent.putExtra(AppContants.SERVICECENTERS_ENABLE, remoteSearchServiCecenters);
                startActivity(intent);
            }
        });

        mBottomToolBar.findViewById(R.id.myorderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryListActivity.this, OrderListActivity.class);
                intent.putExtra(AppContants.SUPPLIER_ID, supplierData.getId());
                intent.putExtra(AppContants.SUPPLIER_NAME, supplierData.getName());
                startActivity(intent);
            }
        });

        if (!supplierData.isCartEnabled()) {
            mBottomToolBar.findViewById(R.id.myorderBtn).setVisibility(View.GONE);
        }
        btn_Subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("PaytmButton click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                Intent nIntent = new Intent(CategoryListActivity.this, PaytmGatewayActivity.class);
                nIntent.putExtra(AppContants.FP_USER_DETAILS, userdetails);
                nIntent.putExtra(AppContants.FP_USER_ID, userdetails.getId());
                startActivity(nIntent);
            }
        });

        btn_Trial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Answers.getInstance().logCustom(new CustomEvent("PriceDropTrial click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                mlayout.setVisibility(View.VISIBLE);
                subscribeAleartLayout.setVisibility(View.GONE);
                Utils.getCurrentdate();// TODO: 25-02-2017  PASS THE CURRENT DATE TO SERVER IF THE USER IS START USING TRIAL FEATURE
                btn_Trial.setVisibility(View.GONE);
                btn_DaysRemaing.setVisibility(View.VISIBLE);
                AppSubscriptionInfo appSubscriptionInfo = new AppSubscriptionInfo();
                appSubscriptionInfo.setSubcription_EXTRAINFO("MYDUKAN_UserID:" + userID + "||" + "User_MobileNO:" + userdetails.getUserinfo().getNumber() + "||" + "User_EmailId:" + userdetails.getUserinfo().getEmailid());
                appSubscriptionInfo.setSubscription_TRIALDAYS("7");
                appSubscriptionInfo.setSubscription_TRIALSTARTDATE(Utils.getCurrentdate());
                updateSubscriptionInfo(CategoryListActivity.this, appSubscriptionInfo);
                notNew_user = true;
            }
        });

        btn_DaysRemaing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (open_PageName != null) {
                    //open_PageName="schemes";
                    switch (open_PageName) {
                        case "category":
                            Answers.getInstance().logCustom(new CustomEvent("FreeUseBTN")
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);

                            break;

                        case "category_filter":
                            Intent intent1 = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent1.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent1.putExtra(AppContants.SUPPLIER, supplierData);
                            intent1.putExtra(AppContants.USER_DETAILS, userdetails);
                            HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                            minMax.put("Min", minRange);
                             minMax.put("Max", maxRange);
                            Log.e("Maxxx", (String) ""+maxRange);
                            intent1.putExtra(AppContants.PRICE_RANGE, minMax);
                            intent1.putExtra(AppContants.PRICE_TYPE, price_Type);
                            startActivity(intent1);
                            break;
                        case "schemes":
                            Answers.getInstance().logCustom(new CustomEvent("Scheme click")
                                    .putCustomAttribute("Name", supplierData.getName()));
                            Intent schemeIntent = new Intent(CategoryListActivity.this, SchemeListActivity.class);
                            schemeIntent.putExtra(AppContants.SUPPLIER_ID, supplierData.getId());
                            schemeIntent.putExtra(AppContants.SUPPLIER_NAME, supplierData.getName());
                            startActivity(schemeIntent);
                            break;
                        default:
                            Answers.getInstance().logCustom(new CustomEvent("FreeUseBTN_Default")
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent catintent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            catintent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            catintent.putExtra(AppContants.SUPPLIER, supplierData);
                            catintent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(catintent);
                            break;
                    }
                    mlayout.setVisibility(View.VISIBLE);
                    subscribeAleartLayout.setVisibility(View.GONE);
                    btn_Trial.setVisibility(View.GONE);
                } else {
                    mlayout.setVisibility(View.VISIBLE);
                    subscribeAleartLayout.setVisibility(View.GONE);
                    btn_Trial.setVisibility(View.GONE);
                }

            }
        });


        //setup actionbar
        setupActionBar();
        setupListView();
        getUserProfile();
        fetchCategoryData();
        AddFilters();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        fetchWelcome();

    }

    private void updateSubscriptionInfo(final Context mContext, final AppSubscriptionInfo appSubscriptionInfo) {
        userdetails.setAppSubscriptionInfo(appSubscriptionInfo);
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(AppContants.SUBSCRIPTION_TRIALDAYS, appSubscriptionInfo.getSubscription_TRIALDAYS());
        userInfo.put(AppContants.SUBSCRIPTION_TRIALSTARTDATE, appSubscriptionInfo.getSubscription_TRIALSTARTDATE());
        userInfo.put(AppContants.SUBSCRIPTION_EXTRAINFO, appSubscriptionInfo.getSubcription_EXTRAINFO());
        userInfo.put(AppContants.SUBSCRIPTION_USERID, userID);
        //Initialize AppDukan
        ApiManager.getInstance(mContext).updateUserSubscription(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                userInfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i(MyDukan.LOGTAG, "User updated successfully");
                        if (userdetails != null) {
                            if (userdetails.getAppSubscriptionInfo() != null) {
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        Log.i(MyDukan.LOGTAG, "Failed to update user profile");
                    }
                });
    }


    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    private void fetchWelcome() {
        remoteDisp_Subscription = mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY3);
        remoteSearchServiCecenters = mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY4);
        //  displayWelcomeMessage();LOADING_PHRASE_CONFIG_KEY3

        long cacheExpiration = 1500; // 1 hour in seconds.
        //long cacheExpiration = 3; // 1 hour in seconds.

        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();
                    displayWelcomeMessage();
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();
                } else {
                    Toast.makeText(CategoryListActivity.this, "Unable to dowload data,Please check the Internet Connection.", Toast.LENGTH_SHORT).show();
                }
                // displayWelcomeMessage();
            }
        });
        // [END fetch_config_with_callback]
    }

    /**
     * Display a welcome message in all caps if welcome_message_caps is set to true. Otherwise,
     * display a welcome message as fetched from welcome_message.
     */
    // [START display_welcome_message]
    private void displayWelcomeMessage() {
        // [START get_config_values]
        String welcomeMessage = mFirebaseRemoteConfig.getString(REMOTE_MESSAGE_KEY);
        // [END get_config_values]LOADING_PHRASE_CONFIG_KEY5
        if (mFirebaseRemoteConfig.getBoolean(LOADING_PHRASE_CONFIG_KEY4)) {
            mBottomToolBar.findViewById(R.id.complaintBtn).setVisibility(View.VISIBLE);
        } else {
            mBottomToolBar.findViewById(R.id.complaintBtn).setVisibility(View.GONE);//SET AS GONE AFTER TESTING
        }

        if (mFirebaseRemoteConfig.getBoolean(LOADING_PHRASE_CONFIG_KEY5)) {
            //setTheBadgeCount(1);
            //btn_notification.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_notification_badge, 0, 0);
            // SetNotificationTimer();
        } else {
            //  setTheBadgeCount(0);
            //    btn_notification.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_action_notifications, 0, 0);
        }

        if (mFirebaseRemoteConfig.getBoolean(REMOTE_MESSAGE_CAPS_KEY)) {
            enableProductPage = "true";
        }
        if (mFirebaseRemoteConfig.getBoolean(REMOTE_MESSAGE_ENABLE_PROMOCODE)) {
            enableProductPage = "true";
        }

    }

    private void getUserProfile() {

        if (mViewType == AppContants.SIGN_UP) {
            mApp.getPreference().setAppState(CategoryListActivity.this, new AppStateContants().HOME_SCREEN);
        }
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(CategoryListActivity.this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {

                if (data != null) {
                    userdetails = (User) data;
                    String mId = mApp.getFirebaseAuth().getCurrentUser().getUid();
                    //validateSubScriptionUtils(CategoryListActivity.this, userID, userdetails);
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


    private void setupListView() {
        mAdapter = new CategoryAdapter(CategoryListActivity.this, supplierData.getId(), this);
        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Data");
        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);

        mRecyclerView.setLayoutManager(new GridLayoutManager(CategoryListActivity.this, 3));
        // mRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryListActivity.this));
        mRecyclerView.setAdapter(mAdapter);

       /* catspinner = (Spinner) findViewById(R.mCatId.categ);
        pricespinner = (Spinner) findViewById(R.mCatId.pricetype);
        catspinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        List<String> pricetype = new ArrayList<String>();
        pricetype.add("DP");
        pricetype.add("MOP");

        // Creating adapter for spinner
        categAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, pricetype);
        // Drop down layout style - list view with radio button   R.layout.spinner_item
        categAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        catspinner.setAdapter(categAdapter);
        pricespinner.setAdapter(priceAdapter);*/
    }


    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.category_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchCategoryData() {
        showProgress();
        ApiManager.getInstance(CategoryListActivity.this).getCategoryList(supplierData.getId(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                mCategoryList = (ArrayList<Category>) data;
                if (mCategoryList.isEmpty()) {
                    mNoDataView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    compareCategoryList();
                    mAdapter.addItems(mCategoryList);
                    mAdapter.notifyDataSetChanged();

                    for (int i = 0; i < ((ArrayList<Category>) data).size(); ++i) {
                        String item = ((ArrayList<Category>) data).get(i).getName();
                        categAdapter.add(item);
//                                if(!item.equals("PRICE DROP") || !item.equals("NEW LAUNCH")) {
//                                    categAdapter.add(item);
//                                }
                    }
                    categAdapter.notifyDataSetChanged();
                }
                ApiManager.getInstance(CategoryListActivity.this).refreshTheSupplierGroups(supplierData.getId(), null);
                Answers.getInstance().logCustom(new CustomEvent(supplierData.getName())
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
            }

            @Override
            public void onFailure(String response) {
                dismissProgress();

            }
        });
    }

    public void AddFilters() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.item_filters, null);
        /*final EditText etUsername = (EditText) alertLayout.findViewById(R.mCatId.et_username);
        final EditText etPassword = (EditText) alertLayout.findViewById(R.mCatId.et_password);
        final CheckBox cbShowPassword = (CheckBox) alertLayout.findViewById(R.mCatId.cb_show_password);*/
        final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) alertLayout.findViewById(R.id.rangeSeekbar);
        final TextView tvMax, tvMin;
        tvMin = (TextView) alertLayout.findViewById(R.id.minVal);
        tvMax = (TextView) alertLayout.findViewById(R.id.maxVal);
        final Spinner catspinner, pricespinner;

        catspinner = (Spinner) alertLayout.findViewById(R.id.categ);
        pricespinner = (Spinner) alertLayout.findViewById(R.id.pricetype);
        catspinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        List<String> pricetype = new ArrayList<String>();
        pricetype.add("DP");
        pricetype.add("MOP");

        // Creating adapter for spinner
        categAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, pricetype);
        // Drop down layout style - list view with radio button   R.layout.spinner_item
        categAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        catspinner.setAdapter(categAdapter);
        pricespinner.setAdapter(priceAdapter);
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                if (maxValue.intValue() > 25000) {
                    tvMax.setText(AppContants.RANGE_MAX);
                } else {
                    tvMax.setText(String.valueOf(maxValue));
                }
            }
        });

// set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Filters");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Apply", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (index >= 0) {
                    if (networkUtil.isConnectingToInternet(CategoryListActivity.this)) {
                        //checkPrimeCategory();
                        open_PageName = "category_filter"; // this will tell us which page has to open
                        mCatId = mCategoryList.get(index).getId();

                        switch (mCatId) {
                            case "-KX41ilBK4hjaSDIV419":
                                //PRICE DROP
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();

                                } else {

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);

                                }
                                break;

                            case "-KTTV8BvY0PpRUOEyKOo":
                                //SAMSUNG
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();
                                } else {
                                  Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KTmmfHzpn5Nn-ZZ0N1A":
                                // GIONEE=  -KTmmfHzpn5Nn-ZZ0N1A
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KTwazqP9cmccZNbt9hb":
                                // LAVA = -KTwazqP9cmccZNbt9hb
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KU2hE65nFcTF0eYa0u_":
                                // VIVO= -KU2hE65nFcTF0eYa0u_
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();



                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KTwlJGRrjcnqumXCzGV":
                                // LENOVO =-KTwlJGRrjcnqumXCzGV
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KUIMuoS_jziNt_deTkv":
                                // ITEL = -KUIMuoS_jziNt_deTkv
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KTlvtThlXhnokm3hux3":
                                //INTEX= -KTlvtThlXhnokm3hux3
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();

                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;

                            case "-KTxmDYOCC_MxyRtu83V":
                                //OPPO= -KTxmDYOCC_MxyRtu83V
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KU1r_NUU5DdFPJLS1C2":
                                //XIAOMI= -KU1r_NUU5DdFPJLS1C2
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;
                            case "-KZkiZ785mct3R2ROHlf":
                                //NEW LAUNCH= -KZkiZ785mct3R2ROHlf
                                if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                    subscribeAleartLayout.setVisibility(View.VISIBLE);

                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    price_Type= pricespinner.getSelectedItem().toString();


                                } else {
                                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                    HashMap<String, Integer> minMax = new HashMap<String, Integer>();

                                    minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                    minRange=Integer.parseInt((String) tvMin.getText());
                                    if (tvMax.getText().equals(AppContants.RANGE_MAX)) {

                                        maxRange=AppContants.PRICE_MAX;
                                        minMax.put("Max", AppContants.PRICE_MAX);
                                    } else {
                                        maxRange=Integer.parseInt((String) tvMax.getText());
                                        minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                    }
                                    Log.e("Maxxx", (String) tvMax.getText());
                                    intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                    price_Type= pricespinner.getSelectedItem().toString();
                                    intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                    startActivity(intent);
                                }
                                break;

                            default:
                                Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                                Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                intent.putExtra(AppContants.SUPPLIER, supplierData);
                                intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                if (tvMax.getText().equals(AppContants.RANGE_MAX)) {
                                    minMax.put("Max", AppContants.PRICE_MAX);
                                } else {
                                    minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                }
                                Log.e("Maxxx", (String) tvMax.getText());
                                intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                startActivity(intent);
                                break;
                        }

                  /*      if (mCatId.equals("-KX41ilBK4hjaSDIV419")) {
                            if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                                subscribeAleartLayout.setVisibility(View.VISIBLE);
                            } else {
                                Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                                Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                                intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                                intent.putExtra(AppContants.SUPPLIER, supplierData);
                                intent.putExtra(AppContants.USER_DETAILS, userdetails);
                                HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                                minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                                if (tvMax.getText().equals(AppContants.RANGE_MAX)) {
                                    minMax.put("Max", AppContants.PRICE_MAX);
                                } else {
                                    minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                                }
                                Log.e("Maxxx", (String) tvMax.getText());
                                intent.putExtra(AppContants.PRICE_RANGE, minMax);
                                intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                                startActivity(intent);
                            }
                        } else {

                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(index).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            HashMap<String, Integer> minMax = new HashMap<String, Integer>();
                            minMax.put("Min", Integer.parseInt((String) tvMin.getText()));
                            if (tvMax.getText().equals(AppContants.RANGE_MAX)) {
                                minMax.put("Max", AppContants.PRICE_MAX);
                            } else {
                                minMax.put("Max", Integer.parseInt((String) tvMax.getText()));
                            }
                            Log.e("Maxxx", (String) tvMax.getText());
                            intent.putExtra(AppContants.PRICE_RANGE, minMax);
                            intent.putExtra(AppContants.PRICE_TYPE, pricespinner.getSelectedItem().toString());
                            startActivity(intent);
                        }
*/
                    } else {
                        Toast.makeText(CategoryListActivity.this, "Please check Internet Connection.", Toast.LENGTH_LONG).show();
                    }

                }
            }
//                Toast.makeText(getBaseContext(), "Username: " + user + " Password: " + pass, Toast.LENGTH_SHORT).show();
        });
        dialog = alert.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

            }
        });

        dialog.show();
    }

    private boolean showSubscriptionPage(Context context, String userID, User userdetails) {
        boolean subPageOpen = false;
        if (mSupplierId.equals("WDSLSgxI10eiWVey4RVWY5niElE3")) {

            PriceDropSubscription priceDropSubscription = new PriceDropSubscription();
            boolean isSubScried = priceDropSubscription.checkSubscription(this, userdetails);

            if (mFirebaseRemoteConfig.getBoolean(LOADING_PHRASE_CONFIG_KEY3)) {
                //  subscribeAleartLayout.setVisibility(View.GONE);

                if (isSubScried) {
                    //subscribed user,please allow them to use.
                    // subscribeAleartLayout.setVisibility(View.GONE);
                    mlayout.setVisibility(View.VISIBLE);
                    subPageOpen = false;
                } else {
                    subPageOpen = true;
                    if (!notNew_user) {
                        btn_Trial.setVisibility(View.VISIBLE);
                    } else {
                        //btn_Trial.setVisibility(View.VISIBLE);// TODO: 02-03-2017  change visibility after testing(visibility must be gone in this else part.).
                        btn_Trial.setVisibility(View.GONE);
                        if (userdetails.getAppSubscriptionInfo() != null) {
                            String trialStartDate = userdetails.getAppSubscriptionInfo().getSubscription_TRIALSTARTDATE();
                            long daydiff = priceDropSubscription.getDaysRemaining(trialStartDate);
                            if (daydiff >= 7) {
                                daysRemain.setVisibility(View.GONE);
                                btn_DaysRemaing.setVisibility(View.GONE);
                            } else {
                                daysRemain.setText("Your Free trial subscription expires in " + (7 - daydiff) + " days");
                                daysRemain.setVisibility(View.VISIBLE);
                                btn_DaysRemaing.setVisibility(View.VISIBLE);
                            }
                        } else {
                            btn_Trial.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                //  subscribeAleartLayout.setVisibility(View.GONE);
                mlayout.setVisibility(View.VISIBLE);
                subPageOpen = false;
            }
        } else {
            // subscribeAleartLayout.setVisibility(View.GONE);
            mlayout.setVisibility(View.VISIBLE);
            subPageOpen = false;
        }
        return subPageOpen;
    }


    //================================================================

    private void compareCategoryList() {
        ArrayList<Category> orderList = new ArrayList<>();
        ArrayList<Category> nonOrderList = new ArrayList<>();
        for (Category category : mCategoryList) {
            if (category.getOrder().equalsIgnoreCase("-1")) {
                nonOrderList.add(category);
            } else if (category.getOrder().equals("")) {
                nonOrderList.add(category);
            } else {
                orderList.add(category);
            }
        }

        Collections.sort(orderList, new CategoryListActivity.OrderComparator());
        Collections.sort(nonOrderList, new CategoryListActivity.NameComparator());

        mCategoryList.clear();
        mCategoryList.addAll(orderList);
        mCategoryList.addAll(nonOrderList);
    }

    private class NameComparator implements Comparator<Category> {
        public int compare(Category c1, Category c2) {
            return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
        }
    }

    private class OrderComparator implements Comparator<Category> {
        public int compare(Category c1, Category c2) {
            return Integer.valueOf(c1.getOrder()).compareTo(Integer.valueOf(c2.getOrder()));
        }
    }

    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        scroll.smoothScrollTo(0, 0);

    }

    private void onWhatsAppBtnClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                if (contactExists(CategoryListActivity.this, "+919036770772")) {
                    sendwhatsapp();
                }
                if (!contactExists(CategoryListActivity.this, "+919036770772")) {
                    addcontact();
                }
            } else {
                showPermissions();
            }
        } else {
            if (contactExists(CategoryListActivity.this, "+919036770772")) {
                sendwhatsapp();
            }
            if (!contactExists(CategoryListActivity.this, "+919036770772")) {
                addcontact();
            }
        }
    }

    private void showPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage("Please do not deny any permissions, accept all permissions to enter myDukan")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue
                                ActivityCompat.requestPermissions(CategoryListActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 1);
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e("testing", "Permission is already granted");

        }
    }

    public void addcontact() {
        try {
            Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
            contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

            contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, "MyDukan")
                    .putExtra(ContactsContract.Intents.Insert.PHONE, "+919036770772");
            startActivityForResult(contactIntent, 1);

        } catch (Exception e) {
            dismissProgress();
            Log.i("Cannot add", " cannot add");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "granted");
        } else {
            Log.i("permission", "revoked");

        }
    }

    public void sendwhatsapp() {
        Uri uri = Uri.parse("smsto:" + "+919036770772");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    public boolean contactExists(Context context, String number) {
        // number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    return true;
                }
            }
        } finally {
            if (cur != null)
                cur.close();
        }

        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        index = position;
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void OnClick(int position) {
        if (position >= 0) {
            open_PageName = "category"; // this will tell us which page has to open
            mCatId = mCategoryList.get(position).getId();
            if (networkUtil.isConnectingToInternet(CategoryListActivity.this)) {
                switch (mCatId) {
                    case "-KX41ilBK4hjaSDIV419":
                        //PRICE DROP
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;

                    case "-KTTV8BvY0PpRUOEyKOo":
                        //SAMSUNG
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KTmmfHzpn5Nn-ZZ0N1A":
                        // GIONEE=  -KTmmfHzpn5Nn-ZZ0N1A
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KTwazqP9cmccZNbt9hb":
                        // LAVA = -KTwazqP9cmccZNbt9hb
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KU2hE65nFcTF0eYa0u_":
                        // VIVO= -KU2hE65nFcTF0eYa0u_
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KTwlJGRrjcnqumXCzGV":
                        // LENOVO =-KTwlJGRrjcnqumXCzGV
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KUIMuoS_jziNt_deTkv":
                        // ITEL = -KUIMuoS_jziNt_deTkv
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KTlvtThlXhnokm3hux3":
                        //INTEX= -KTlvtThlXhnokm3hux3
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;

                    case "-KTxmDYOCC_MxyRtu83V":
                        //OPPO= -KTxmDYOCC_MxyRtu83V
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KU1r_NUU5DdFPJLS1C2":
                        //XIAOMI= -KU1r_NUU5DdFPJLS1C2
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;
                    case "-KZkiZ785mct3R2ROHlf":
                        //NEW LAUNCH= -KZkiZ785mct3R2ROHlf
                        if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                            subscribeAleartLayout.setVisibility(View.VISIBLE);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                    .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                            intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                            intent.putExtra(AppContants.SUPPLIER, supplierData);
                            intent.putExtra(AppContants.USER_DETAILS, userdetails);
                            startActivity(intent);
                        }
                        break;

                    default:
                        Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                        Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                        intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                        intent.putExtra(AppContants.SUPPLIER, supplierData);
                        intent.putExtra(AppContants.USER_DETAILS, userdetails);
                        startActivity(intent);
                        break;
                }

/*
                if (mCatId.equals("-KX41ilBK4hjaSDIV419")) {
                    if (showSubscriptionPage(CategoryListActivity.this, userID, userdetails)) {
                        subscribeAleartLayout.setVisibility(View.VISIBLE);
                    } else {
                        Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                                .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                        Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                        intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                        intent.putExtra(AppContants.SUPPLIER, supplierData);
                        intent.putExtra(AppContants.USER_DETAILS, userdetails);
                        startActivity(intent);
                    }
                } else {
                    Answers.getInstance().logCustom(new CustomEvent(mCategoryList.get(position).getName())
                            .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                    Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
                    intent.putExtra(AppContants.CATEGORY_ID, mCatId);
                    intent.putExtra(AppContants.SUPPLIER, supplierData);
                    intent.putExtra(AppContants.USER_DETAILS, userdetails);
                    startActivity(intent);
                }*/
            } else {
                Toast.makeText(CategoryListActivity.this, "Please check Internet Connection.", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_productactivity, menu);

        final MenuItem mCart = menu.findItem(R.id.product_notification);
        /*BitmapDrawable iconBitmap = (BitmapDrawable) mCart.getIcon();
        LayerDrawable iconLayer = new LayerDrawable(new Drawable[] { iconBitmap });*/
        icon = (LayerDrawable) mCart.getIcon();

        //  MenuItem  iconButtonMessages = menu.findItem(R.mCatId.product_notification);
        // itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.mCatId.badge_textView);
        // itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden

       /* iconButtonMessages = (IconButton)findViewById(R.mCatId.badge_icon_button);
        iconButtonMessages.setText("{fa-envelope}");
        iconButtonMessages.setTextColor(getResources().getColor(R.color.tw__solid_white));

      //  itemMessagesBadgeTextView.setText("2");
       // itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
        iconButtonMessages.setTextColor(getResources().getColor(R.color.white));*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.product_notification:

                Answers.getInstance().logCustom(new CustomEvent("Notification Menubar")
                        .putCustomAttribute("Name", supplierData.getName()));
//                ShortcutBadger.removeCount(CategoryListActivity.this); //for 1.1.4+
                //setTheBadgeCount(0);

                Intent notification_intent = new Intent(this, NotificationListActivity.class);
                notification_intent.putExtra(AppContants.SUPPLIER, supplierData);
                notification_intent.putExtra(AppContants.SUPPLIER_ID, supplierData.getId());
                notification_intent.putExtra(AppContants.SUPPLIER_NAME, supplierData.getName());
                startActivity(notification_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
