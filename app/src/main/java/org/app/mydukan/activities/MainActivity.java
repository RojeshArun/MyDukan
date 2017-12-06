package org.app.mydukan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.viewpagerindicator.CirclePageIndicator;

import org.app.mydukan.BuildConfig;
import org.app.mydukan.R;
import org.app.mydukan.adapters.SlidingImage_Adapter;
import org.app.mydukan.adapters.SupplierAdapter;
import org.app.mydukan.appSubscription.PriceDropSubscription;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.AppSubscriptionInfo;
import org.app.mydukan.data.BannerImages;
import org.app.mydukan.data.ImageModel;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.deanwild.materialshowcaseview.target.Target;

//import org.app.mydukan.utils.PaytmGateway;


/**
 * autoIntegrate
 * Created by arpithadudi on 7/27/16.
 * updated by Shivayogi Hiremath.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        SupplierAdapter.supplierAdapterListener {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;

    private int[] myImageList = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4
            , R.drawable.slide5};

    Target t1, t2, t3;
    public static final String FOLLOW_ROOT = "following";
    private int mNavItemId;
    private Toolbar mToolbar;
    private DrawerLayout drawer;
    private FloatingActionButton addsupplierbtn;
    private FloatingActionButton mWhatsAppBtn;

    private final Handler mDrawerActionHandler = new Handler();
    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    //Ui reference
    private RecyclerView mSupplierRecyclerView;
    private TextView mSupplierEmptyView;

    private LinearLayout subscribeAleartLayout;
    private Button btn_Subscribe, btn_Trial, btn_DaysRemaing, btn_navigation;
    private TextView daysRemain, mDueDays;
    boolean notNew_user = true;
    LoginActivity loginActivity = new LoginActivity();
    SharedPreferences sharedPreferences;
    String lounchedDate_User;

    //Variables
    private SupplierAdapter mSupplierAdapter;
    InterstitialAd mInterstitialAd;//to display interstitial ads
    private ArrayList<Supplier> mSupplerlist = new ArrayList<>();
    private ArrayList<String> mBannerImg = new ArrayList<>();

    private MyDukan mApp;
    private User userdetails;
    private String userID;
    private int mViewType = 1;
    AdView mAdView;
    private HashMap mNotification;

    AppSubscriptionInfo appSubscriptionInfo = new AppSubscriptionInfo();
    private static final String TAG = "MainActivity";

    // Remote Config keys showSubscriptionPage
    private static final String LOADING_PHRASE_CONFIG_KEY1 = "video_url";
    private static final String LOADING_PHRASE_CONFIG_KEY2 = "isVideoToBeDsiplayed";
    private static final String LOADING_PHRASE_CONFIG_KEY3 = "showSubscriptionPage";
    private static final String MAINPAGE_CONFIG__IMGBANNER_1 = "mainpage_img_banner_1";
    private static final String MAINPAGE_CONFIG__IMGBANNER_2 = "showSubscriptionPage";
    private static final String MAINPAGE_CONFIG__IMGBANNER_3 = "showSubscriptionPage";
    private static final String MAINPAGE_CONFIG__IMGBANNER_4 = "showSubscriptionPage";

    private ArrayList<String> list_BannerImg = new ArrayList<>();
    private static final String WELCOME_MESSAGE_KEY = "isVideoToBeDsiplayed";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "isVideoToBeDsiplayed";

    /*
      private static final String INTRO_CARD = "fab_intro";
      private static final String INTRO_SWITCH = "switch_intro";
      private static final String INTRO_RESET = "reset_intro";
      private static final String INTRO_REPEAT = "repeat_intro";
      private static final String INTRO_CHANGE_POSITION = "change_position_intro";
      private static final String INTRO_SEQUENCE = "sequence_intro";
      private boolean isRevealEnabled = true;
      */
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    String remoteUrl, remoteToDisplay, remoteDisp_Subscription, remote_imgBanner_one, remote_imgBanner_two, remote_imgBanner_three;
    LinearLayout whatsapp_layout, records_layout, mynetwork_layout, askRaju_layout, serviceCenter_layout;
    LinearLayout relative_flipper_layout;
    ViewFlipper flipper_promotional;
    int mFlipping = 0; // Initially flipping is off
    ImageView banner1;
    boolean isSubscribed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = (MyDukan) getApplicationContext();

        final FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        if (userid != null) {
            userID = userid.getUid();
        } else {
            userID = mApp.getUserId();
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.NOTIFICATION)) {
                mNotification = (HashMap) bundle.getSerializable(AppContants.NOTIFICATION);
                if (mNotification != null) {
                    Intent intent = new Intent(this, NotificationDescriptionActivity.class);
                    intent.putExtra(AppContants.NOTIFICATION, mNotification);
                    startActivity(intent);
                }
            }
        }
        addsupplierbtn = (FloatingActionButton) findViewById(R.id.add_supplier_button);
        subscribeAleartLayout = (LinearLayout) findViewById(R.id.layout_subscribe);
        btn_Subscribe = (Button) findViewById(R.id.btn_subscription);
        btn_Trial = (Button) findViewById(R.id.btn_trial);
        btn_DaysRemaing = (Button) findViewById(R.id.btn_remaingDays);
        daysRemain = (TextView) findViewById(R.id.tv_message_trialUser);
        btn_navigation = (Button) findViewById(R.id.button_menus);
        mDueDays = (TextView) findViewById(R.id.tv_SubscriptionDueDate);
        setupActionBar();
        setupDrawerLayout();
        //checkUserPresentInDatabase();
        getUserProfile();
     /*   imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList(list_BannerImg);
        initViewPager();*/

//=============================================================

        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]
        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        // mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // [END set_default_values]

        fetchWelcome();
        setupSupplierCard();
        showPermissions();

        //=====================================================

      /*  flipper_promotional = (ViewFlipper) findViewById(R.mCatId.flipper_promotional);

        if (mFlipping == 0) {
            *//* Start Flipping *//*
            flipper_promotional.startFlipping();
            mFlipping = 1;
        }
       */
        relative_flipper_layout = (LinearLayout) findViewById(R.id.banner);
        relative_flipper_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            /*    Answers.getInstance().logCustom(new CustomEvent("PaytmButton click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                PaytmGateway paytmGateway = new PaytmGateway();
                paytmGateway.startGateWay(MainActivity.this, userdetails, userdetails.getId());
               *//* Intent nIntent = new Intent(MainActivity.this, PaytmGatewayActivity.class);
                nIntent.putExtra(AppContants.FP_USER_DETAILS, userdetails);
                nIntent.putExtra(AppContants.FP_USER_ID,userdetails.getId());
                startActivity(nIntent);*/

            }
        });

        whatsapp_layout = (LinearLayout) findViewById(R.id.whatsapp_layout);
        whatsapp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWhatsAppBtnClick();
            }
        });

        records_layout = (LinearLayout) findViewById(R.id.records_layout);
        records_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent record = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(record);
            }
        });

        mynetwork_layout = (LinearLayout) findViewById(R.id.mynetwork_layout);
        mynetwork_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nIntent = new Intent(MainActivity.this, MyNetworksActivity.class);
                startActivity(nIntent);

              /*  Intent nIntent = new Intent(MainActivity.this, PaytmGatewayActivity.class);
                startActivity(nIntent);*/
            }
        });

        askRaju_layout = (LinearLayout) findViewById(R.id.askRaju_layout);
        askRaju_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("ASK_RAJU click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));

                Intent nIntent = new Intent(MainActivity.this, ChatActivity.class);
                nIntent.putExtra("IS_SUBSCRIBED", isSubscribed);

                startActivity(nIntent);
            }
        });
        serviceCenter_layout = (LinearLayout) findViewById(R.id.servicecenter_layout);
        serviceCenter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nIntent = new Intent(MainActivity.this, ServiceProviders.class);
                startActivity(nIntent);
            }
        });
        addsupplierbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent supplier = new Intent(MainActivity.this, SupplierListActivity.class);
                //Intent supplier = new Intent(MainActivity.this,VideoActivity.class);
                startActivity(supplier);
            }
        });

        mWhatsAppBtn = (FloatingActionButton) findViewById(R.id.whatsAppBtn);
        mWhatsAppBtn.setVisibility(View.VISIBLE);
        mWhatsAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onWhatsAppBtnClick();
            }
        });

        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);
        if (firstTime) {
            editor.putBoolean("first", false);
            //For commit the changes, Use either editor.commit(); or  editor.apply();.
            editor.commit();
            notNew_user = loginActivity.isNewUserForTrail;
            lounchedDate_User = Utils.getCurrentdate();
            // now the calling the method to clear the cache data or app data
        }


      /*  //checkUserSubscription();
        subscribeAleartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("PaytmButton click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                PaytmGateway paytmGateway = new PaytmGateway();
                paytmGateway.startGateWay(MainActivity.this, userdetails, userdetails.getId());
            }
        });

        btn_Subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("PaytmButton click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                PaytmGateway paytmGateway = new PaytmGateway();
                paytmGateway.startGateWay(MainActivity.this, userdetails, userdetails.getId());
            }
        });*/


        //initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //for Interstitial ads

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1640690939729824/3580285398");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //Write some logic on ad loaded
            }

            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });


        //========Showcase View using SpotlightSequence======================

    /*    SpotlightSequence.getInstance(this, null)
                .addSpotlight(btn_Subscribe, "MyDukan Subscription", "Subscription information is shown here", INTRO_CHANGE_POSITION)
                .addSpotlight(mWhatsAppBtn, "Whatsapp Us", "Having trouble? Reach us on Whatsapp ", INTRO_SWITCH)
                .addSpotlight(addsupplierbtn, "Add Supplier ", "Click here to add a supplier", INTRO_CARD)
                .startSequence();

*/
        //====================================================================

       /* ShowcaseConfig config = new ShowcaseConfig();
          config.setDelay(200);
        //config.setContentTextColor(Color.parseColor("#400000"));
        config.setDismissTextColor(Color.parseColor("#64DD17"));
        config.setMaskColor(Color.parseColor("#dc4b4b4b"));

        // half second between each showcase view  Mask colors #EA80FC   #D81B60  #009688  #E0E0E0

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1010_id");
        sequence.setConfig(config);
        sequence.addSequenceItem(mWhatsAppBtn, "Having trouble reach us on whatsapp", "NEXT");
        sequence.addSequenceItem(addsupplierbtn, "Click here to add a supplier", "NEXT");
        //sequence.addSequenceItem(btn_navigation, "This is the navigation bar", "GOT IT");
        sequence.addSequenceItem(btn_Subscribe, "MyDukan Subscription", "NEXT");
        sequence.start();*/


          /*new SpotlightView.Builder(this)
                .introAnimationDuration(100)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(100)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Chat with us at Whatsapp")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Have trouble using MyDukan?\nLet us know through whatsapp.")
                .maskColor(Color.parseColor("#dc000060"))
                .target(btn_Subscribe)
                .lineAnimDuration(100)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("1010_id") //UNIQUE ID
                .show();*/

//        CleverTapAPI cleverTap;
//        try {
//            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
//        } catch (CleverTapMetaDataNotFoundException e) {
//            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
//        } catch (CleverTapPermissionsNotSatisfied e) {
//            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
//        }

        //=================================================================
//        new CompanyInfo().execute();
    }


    private ArrayList<ImageModel> populateList(ArrayList<String> list_BannerImg) {

        ArrayList<ImageModel> list = new ArrayList<>();
        if (list_BannerImg.size() > 0) {
            for (int i = 0; i < 4; i++) {
                ImageModel imageModel = new ImageModel();
                imageModel.setImage_drawable(list_BannerImg.get(i));
                list.add(imageModel); // TODO: 11-08-2017 make some correction pending you must do it.
            }
        } else {
          /*  for(int i = 0; i <4; i++){
                ImageModel imageModel = new ImageModel();
                imageModel.setImage_drawable(myImageList[i]);
                list.add(imageModel);
            }
           */
        }
        return list;
    }


    private void initViewPager() {

        // mPager = (ViewPager) findViewById(R.mCatId.pager);
        mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this, imageModelArrayList, userdetails, userdetails.getId()));
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 8000, 8000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }


    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    private void fetchWelcome() {
        // mWelcomeTextView.setText(mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY));
        remoteUrl = mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY1);
        remoteToDisplay = mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY2);
        remoteDisp_Subscription = mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY3);
        remote_imgBanner_one = mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_1);

        //  displayWelcomeMessage();LOADING_PHRASE_CONFIG_KEY3

        long cacheExpiration = 2000; // 1 hour in seconds.
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
                    Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
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
        String welcomeMessage = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY);
        // [END get_config_values]

        if (mFirebaseRemoteConfig.getBoolean(WELCOME_MESSAGE_CAPS_KEY)) {
            Intent intent = new Intent(MainActivity.this, VidPlayer.class);
            //  Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra(AppContants.REMOTECONFIG_VID, remoteUrl);
            startActivity(intent);
        } else {
            // mWelcomeTextView.setAllCaps(false);
        }
        //mWelcomeTextView.setText(welcomeMessage);
    }
    // [END display_welcome_message]

    private void getBannerImages() {

        //showProgress(true);
        mBannerImg = new ArrayList<>();
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("mainpage_Banners");
        feedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    BannerImages bannerImages = new BannerImages();
                    bannerImages = dataSnapshot.getValue(BannerImages.class);
                    if (bannerImages != null) {
                        mBannerImg.add(bannerImages.getBanner1());
                        mBannerImg.add(bannerImages.getBanner2());
                        mBannerImg.add(bannerImages.getBanner3());
                        mBannerImg.add(bannerImages.getBanner4());
                        mBannerImg.add(bannerImages.getBanner5());
                        imageModelArrayList = new ArrayList<>();
                        imageModelArrayList = populateList(mBannerImg);
                        initViewPager();
                        relative_flipper_layout.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                // showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        list_BannerImg.clear();
        if ((mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_1) != null) && !(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_1).isEmpty())) {
            list_BannerImg.add(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_1));


        }
        if ((mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_2) != null) && !(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_2).isEmpty())) {
            list_BannerImg.add(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_2));


        }
        if ((mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_3) != null) && !(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_3).isEmpty())) {
            list_BannerImg.add(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_3));
        }
        if ((mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_4) != null) && !(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_4).isEmpty())) {
            list_BannerImg.add(mFirebaseRemoteConfig.getString(MAINPAGE_CONFIG__IMGBANNER_4));
        }
        if (!list_BannerImg.isEmpty() && list_BannerImg.size() > 0) {

        }

    }

    private boolean isUserSubscribed(User userdetails) {
        PriceDropSubscription priceDropSubscription = new PriceDropSubscription();
        boolean isSubscribed = priceDropSubscription.checkSubscription(this, userdetails);
        return isSubscribed;
    }

    private void checkUserSubscription(User userdetails) {
        PriceDropSubscription priceDropSubscription = new PriceDropSubscription();
        String isSubScried = priceDropSubscription.checkDueDays(this, userdetails);
        String created_Date = String.valueOf(userdetails.getOtherinfo().getCreatedDate());
        if (!isSubScried.isEmpty()) {
            //  Toast.makeText(MainActivity.this, isSubScried, Toast.LENGTH_SHORT).show();
            mDueDays.setText(isSubScried);
            // subscribeAleartLayout.setVisibility(View.GONE);
        } else {
            mDueDays.setVisibility(View.GONE);
            //  subscribeAleartLayout.setVisibility(View.GONE);
        }
    }

    private void updateSubscriptionInfo(final Context mContext, final AppSubscriptionInfo appSubscriptionInfo) {
        userdetails.setAppSubscriptionInfo(appSubscriptionInfo);
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(AppContants.SUBSCRIPTION_TRIALDAYS, appSubscriptionInfo.getSubscription_TRIALDAYS());
        userInfo.put(AppContants.SUBSCRIPTION_TRIALSTARTDATE, appSubscriptionInfo.getSubscription_TRIALSTARTDATE());
        userInfo.put(AppContants.SUBSCRIPTION_EXTRAINFO, appSubscriptionInfo.getSubcription_EXTRAINFO());
        userInfo.put(AppContants.SUBSCRIPTION_USERID, appSubscriptionInfo.getSubscription_USERID());
        //Initialize AppDukan
        ApiManager.getInstance(mContext).updateUserSubscription(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                userInfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i(MyDukan.LOGTAG, "User updated successfully");
                        if (userdetails != null) {
                            if (userdetails.getAppSubscriptionInfo() != null) {
                                subscribeAleartLayout.setVisibility(View.GONE);
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


    //Request interstitial ad from below method

    public void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    //==============================================================================
    private void getUserProfile() {

        if (mViewType == AppContants.SIGN_UP) {
            mApp.getPreference().setAppState(MainActivity.this, new AppStateContants().HOME_SCREEN);
        }
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(MainActivity.this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    userdetails = (User) data;

                    //Store in Preference its very Important to add the suppliers to User.
                    mApp.getPreference().setUser(MainActivity.this, userdetails);

                    checkUserSubscription(userdetails);
                    getUserSubscription();
                    setChatUserData();
                    getBannerImages();
                    isSubscribed = isUserSubscribed(userdetails);
                    if (userdetails != null && userdetails.getCompanyinfo() == null) {
                        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Info")
                                .setMessage("Please fill the Company / Outlet Details to Continue..")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue
                                        Intent intent = new Intent(MainActivity.this, CompanyDetails.class);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                    return;
                }
            }

            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase
                dismissProgress();
                Toast.makeText(MainActivity.this, "Please update your profile details.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setChatUserData() {
        if (userdetails != null) {
            try {
                FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child("chat_USER/" + userID);
                String userROLE = userdetails.getOtherinfo().getRole();
                if (userROLE != null) {
                    referenceFollow.child("userType").setValue(userdetails.getOtherinfo().getRole());
                } else {
//                    Toast.makeText(MainActivity.this, "Please update your profile details to use MyNetwork feature.", Toast.LENGTH_SHORT).show();
                    referenceFollow.child("userType").setValue("");
                }
                String userNUMBER = userdetails.getUserinfo().getNumber();
                if (userNUMBER != null) {
                    referenceFollow.child("phoneNumber").setValue(userdetails.getUserinfo().getNumber());
                } else {
                    referenceFollow.child("phoneNumber").setValue("");
//                    Toast.makeText(MainActivity.this, "Please update your profile details to use MyNetwork feature.", Toast.LENGTH_SHORT).show();
                }
                referenceFollow.child("addressinfo").setValue(userdetails.getUserinfo().getAddressinfo());
                referenceFollow.child("companyinfo").setValue(userdetails.getCompanyinfo());
                referenceFollow.child("name").setValue(userdetails.getCompanyinfo().getName());
                final DatabaseReference addFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
                addFollow.child(userID).child(userID).setValue(true);
            } catch (Exception e) {
                Log.i(MyDukan.LOGTAG, "exception ChattProfile UpdateProfile: " + e.getMessage());
//                Toast.makeText(MainActivity.this, "Please update your profile details to use MyNetwork feature.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getUserSubscription() {

        if (mViewType == AppContants.SIGN_UP) {
            mApp.getPreference().setAppState(MainActivity.this, new AppStateContants().HOME_SCREEN);
        }
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(MainActivity.this).getSubscriptionInfo(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    String subscription_PLAN = String.valueOf(data);
                    appSubscriptionInfo.setSubscription_PLAN(subscription_PLAN);
                    // userdetails.setAppSubscriptionInfo(appSubscriptionInfo);
                    checkUserSubscription(userdetails);
                    isSubscribed = isUserSubscribed(userdetails);
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

    //==============================================================================

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           /*
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
             */
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        drawer.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_share:
                shareTheLink();
                return true;
//            case R.mCatId.menu_notification:
//                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
//                startActivity(intent);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        switch (itemId) {
            case R.id.nav_profile:

                Intent profile = new Intent(MainActivity.this, UserProfile.class);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                profile.putExtra(AppContants.VIEW_TYPE, AppContants.MY_PROFILE);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profile);

                /*Intent profile = new Intent(MainActivity.this, SignupActivity.class);
                profile.putExtra(AppContants.VIEW_TYPE, AppContants.MY_PROFILE);
                startActivity(profile);

                /*Intent mAccIntent = new Intent(MainActivity.this, MyAccountActivity.class);
                mAccIntent.putExtra("MyDhukhan_UserId", userID);
                mAccIntent.putExtra("UserDetails", userdetails);
                startActivity(mAccIntent);*/
                break;

       /*     case R.mCatId.nav_chat:
                Intent chat_Intent = new Intent(MainActivity.this, PostCommentsActivity.class);
                chat_Intent.putExtra("MyDhukhan_UserId", userID);
                chat_Intent.putExtra("UserDetails", userdetails);
                startActivity(chat_Intent);
                break;*/

            case R.id.nav_help:
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder
                        .setMessage(getString(R.string.logout_warning))
                        .setTitle(getString(R.string.Alert))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                logoutUser();
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.nav_record:
                Intent record = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(record);
                break;
            case R.id.nav_mynetwork:
              /*  Intent nIntent = new Intent(MainActivity.this, MyNetworkActivity.class);
                startActivity(nIntent);*/
                Intent nIntent = new Intent(MainActivity.this, MyNetworksActivity.class);
                startActivity(nIntent);
                break;
//            case R.mCatId.nav_appdukan:
//                AppDukan.getInstance(MainActivity.this).launch(MainActivity.this,MainActivity.this);
//                break;]

            case R.id.ic_qrcode:
                Intent generateQRcode = new Intent(MainActivity.this, GenerateQRCodeActivity.class);
                generateQRcode.putExtra("MyDhukhan_UserId", userID);
                generateQRcode.putExtra("UserDetails", userdetails);
                startActivity(generateQRcode);
                break;
            case R.id.nav_service_providers:
                Intent serviceProviders = new Intent(MainActivity.this, ServiceProviders.class);
                startActivity(serviceProviders);
                break;

            case R.id.nav_privecy_policy:
                Intent distributor = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                startActivity(distributor);
                break;

           /*     Intent generateQRcode = new Intent(SignupActivity.this, GenerateQRCodeActivity.class);
                           generateQRcode.putExtra("MyDhukhan_UserId",userID);
                            generateQRcode.putExtra("UserDetails", userdetails);
                        startActivity(generateQRcode);
            */
            default:
                break;
        }
    }

    private void logoutUser() {
        mApp.getFirebaseAuth().signOut();

        Intent signout = new Intent(MainActivity.this, LaunchActivity.class);
        startActivity(signout);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchSupplierdata();
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyDukan");
        // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView headerText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emailID);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        headerText.setText(firebaseUser.getEmail());
       /* User user = mApp.getPreference().getUser(MainActivity.this);
        if (user != null && user.getUserinfo() != null) {
            if (!mApp.getUtils().isStringEmpty(user.getUserinfo().getEmailid())) {
                headerText.setText(user.getUserinfo().getEmailid());
            }
        }*/
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkUserPresentInDatabase() {
        showProgress();
        try {
            ApiManager.getInstance(MainActivity.this).checkUserProfle(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    //do nothing if all things where correct
                    dismissProgress();
                }

                @Override
                public void onFailure(String response) {
                    dismissProgress();
                    /*new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Info")
                            .setMessage("Please fill the Company Details to enter myDukan")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue
                                    Intent intent = new Intent(MainActivity.this, CompanyDetails.class);
                                    startActivity(intent);
                                    finish();
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();*/
//                    logoutUser();
                }
            });
        } catch (Exception e) {
            dismissProgress();
        }

    }

    private void setupSupplierCard() {
        View supplierView = findViewById(R.id.supplierlayout);
        mSupplierAdapter = new SupplierAdapter(MainActivity.this, AppContants.USER_SUPPLIER, this);
        //fetchSupplierdata();
        //setup the recyclerview
        mSupplierRecyclerView = (RecyclerView) supplierView.findViewById(R.id.listview);
       /* mSupplierRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                MainActivity.this.getApplicationContext(), false));*/
        mSupplierEmptyView = (TextView) supplierView.findViewById(R.id.nodata_view);
        mSupplierRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mSupplierRecyclerView.setAdapter(mSupplierAdapter);
        // fetchSupplierdata();
    }

    private void clearTheData() {
        if (!mSupplerlist.isEmpty()) {
            mSupplerlist.clear();
            mSupplierAdapter.clearsupplier();
            mSupplierEmptyView.setVisibility(View.GONE);
            mSupplierRecyclerView.removeAllViews();
            mSupplierAdapter.notifyDataSetChanged();
        }
    }

    private void fetchSupplierdata() {
        clearTheData();
        showProgress();
        try {
            ApiManager.getInstance(MainActivity.this).getUserSupplierlist(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    mSupplerlist = new ArrayList<Supplier>();
                    mSupplerlist = (ArrayList<Supplier>) data;

                    if (mSupplerlist.isEmpty()) {
                        mSupplierEmptyView.setVisibility(View.VISIBLE);
                        mSupplierEmptyView.setText(getString(R.string.my_supplier_empty_text));
                        dismissProgress();
                        return;
                    } else {
                        mSupplierEmptyView.setVisibility(View.GONE);
                    }
                    if (mSupplierAdapter != null) {
                        mSupplierAdapter.clearsupplier();
                    }

                    mSupplierAdapter.addsupplier(mSupplerlist);
                    mSupplierAdapter.notifyDataSetChanged();
                    dismissProgress();
                }

                @Override
                public void onFailure(String response) {
                    dismissProgress();
                    mSupplierEmptyView.setVisibility(View.VISIBLE);
                    mSupplierEmptyView.setText(getString(R.string.my_supplier_empty_text));
                }


            });
        } catch (Exception e) {
            dismissProgress();
        }
    }

    private void onWhatsAppBtnClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                if (contactExists(MainActivity.this, "+919036770772")) {
                    sendwhatsapp();
                }
                if (!contactExists(MainActivity.this, "+919036770772")) {
                    addcontact();
                }
            } else {
                showPermissions();
            }
        } else {
            if (contactExists(MainActivity.this, "+919036770772")) {
                sendwhatsapp();
            }
            if (!contactExists(MainActivity.this, "+919036770772")) {
                addcontact();
            }
        }
    }

    public void openTheSupplier(Supplier supplier) {
        showProgress();
        SupplierBindData supplierData = new SupplierBindData();
        supplierData.setId(supplier.getId());
        supplierData.setName(supplier.getUserinfo().getName());
        supplierData.setCartEnabled(supplier.getOtherinfo().isCartEnabled());
        supplierData.setGroupIds(supplier.getSupplierGroups().getGroupIds());
        // Answers.getInstance().logCustom(new CustomEvent("Supplier click").putCustomAttribute("Name", supplier.getUserinfo().getName()));

        Intent intent = new Intent(MainActivity.this, CategoryListActivity.class);
        intent.putExtra(AppContants.SUPPLIER, supplierData);
        startActivity(intent);
        dismissProgress();
    }

    public void addSupplier(String supplierid) {
        showProgress();
        try {
            ApiManager.getInstance(MainActivity.this).addSupplier(mApp.getFirebaseAuth().getCurrentUser().getUid(), supplierid, new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    Log.i(mApp.LOGTAG, "Success");
                    dismissProgress();
                    fetchSupplierdata();
                }

                @Override
                public void onFailure(String response) {
                    showErrorToast(MainActivity.this, response);
                    dismissProgress();
                }
            });
        } catch (Exception e) {
            dismissProgress();
        }
    }

    @Override
    public void OnSupplierAddClick(int position) {
        addSupplier(mSupplerlist.get(position).getId());
    }

    @Override
    public void OnSupplierOpenClick(int position) {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        openTheSupplier(mSupplerlist.get(position));
    }

    private void showPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
            } else {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage("Please do not deny any permissions, accept all permissions to enter myDukan")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 1);
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

    private void shareTheLink() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_msg_subject));
        String url = "https://play.google.com/store/apps/details?mCatId=org.app.mydukan";
        share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg_text) + "\n " + url);
        startActivity(Intent.createChooser(share, "Share link!"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "granted");
        } else {
            Log.i("permission", "revoked");
        }
    }

    /*private class CompanyInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... params) {

            String mUserid=mApp.getFirebaseAuth().getCurrentUser().getUid();
            DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference().child("users/"+mUserid);
            referenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot!=null){
                        userdetails=dataSnapshot.getValue(User.class);
//                        String mob_vrify= userdetails.getVrified_mobilenum();
//                        String location_verfy=userdetails.getVerified_location();
                         String cmpnyInfo_vrify= userdetails.getVerified_CompanyInfo();

                        if(cmpnyInfo_vrify.equals("true")){
                         Toast.makeText(MainActivity.this, "WelCome", Toast.LENGTH_SHORT).show();
                       }
                       else
                        {
                            new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Info")
                                    .setMessage("Please fill the Company Details to enter myDukan")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue
                                            Intent intent = new Intent(MainActivity.this, CompanyDetails.class);
                                            startActivity(intent);
                                            finish();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }*/
}
