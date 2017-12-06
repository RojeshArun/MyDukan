package org.app.mydukan.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.app.mydukan.BuildConfig;
import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppSubscriptionInfo;
import org.app.mydukan.data.FonPaisaData;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.ProgressSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PaytmGatewayActivity extends AppCompatActivity {
    Button btn_PayNow, btn_choosePlan, btn_promoCode;
    private ProgressSpinner mProgress;
    TextView tv_Status, tv_selectedPlan;
    EditText editTextPromoCode;
    int FONEPAISAPG_RET_CODE = 1;
    FonPaisaData fonPaisaData = new FonPaisaData();
    String uuid1 = UUID.randomUUID().toString();
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private Button btnDisplay;
    private ImageView btnBack;//imageBack
    private LinearLayout radioButtonGroup, promocodeGroup, layout_Promocode;//linearLayout8 layout_promocode
    private String url = "https://mydukan-firebase.000webhostapp.com/V2/generateChecksum.php";
    private String CHECKSUMHASH;
    String BOUNDARY = "s2retfgsGSRFsERFGHfg";
    //Remote config data for promocode.

    Map<String, String> paramMap1 = new HashMap<String, String>();
    //Remote Configuration.
    String enableProductPage = "false";
    // Remote Config keys showSubscriptionPage

    private static final String LOADING_PHRASE_CONFIG_KEY1 = "video_url";
    private static final String LOADING_PHRASE_CONFIG_KEY2 = "isVideoToBeDsiplayed";
    private static final String LOADING_PHRASE_CONFIG_KEY3 = "showSubscriptionPage";
    private static final String LOADING_PHRASE_CONFIG_KEY4 = "servicecenters_enable";
    private static final String LOADING_PHRASE_CONFIG_KEY5 = "notificationIcon_enable";
    private static final String REMOTE_MESSAGE_KEY = "showSubscriptionPage";
    private static final String REMOTE_MESSAGE_CAPS_KEY = "showSubscriptionPage";
    private static final String REMOTE_ENABLE_PROMOCODE = "enable_promocode";
    //Remote Configuration.
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    String remoteDisp_Subscription, remoteSearchServiCecenters;

    //=======Sending Information=================

    Context mContext;
    User mUserDetail;
    String mUserId, mUserEmailId, mUserPhoneNum, mUserName, mSubscription_PLAN, mSubscription_EXPIREDATE, mSubscription_DAYs;
    static   String REQUEST_TYPE, ORDER_ID, MID, CUST_ID, CHANNEL_ID, CALLBACK_URL, INDUSTRY_TYPE_ID, WEBSITE, THEME, EMAIL, MOBILE_NO, GENERATECHECKSUM_URL;
    private String Subscription_Date;
    static  String TXN_AMOUNT;
    private MyDukan mApp;
    String customerID;
    private int mViewType = 1;
    AppSubscriptionInfo appSubscriptionInfo = new AppSubscriptionInfo();
    String uuid = UUID.randomUUID().toString();
    String version;
    String mValidity = "";
    int verCode;
    Random rand = new Random();
    int randomNum = rand.nextInt(50) + 1;

    private ProgressBar mProgressBar;



    HashMap mPtmCheckSumInfo;
    String JsonURL = "https://mydukan-firebase.000webhostapp.com/V3/generateChecksum.php";
    static String checksumValue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fone_paise_gateway);
        overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        Bundle mybundle = getIntent().getExtras();
        if (mybundle != null) {
            if (mybundle.containsKey(AppContants.FP_USER_DETAILS)) {
                mUserDetail = (User) mybundle.getSerializable(AppContants.FP_USER_DETAILS);

                if (mUserDetail != null) {
                    final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
                    mUserId = auth.getUid();
                } else {
                    Toast.makeText(this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
            }
            if (mUserDetail != null && mUserId != null) {
                startGateWay(this, mUserDetail, mUserId);
            }
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progerssBar);

        radioButtonGroup = (LinearLayout) findViewById(R.id.linearLayout8);
        promocodeGroup = (LinearLayout) findViewById(R.id.layout_promocode);

        btnBack = (ImageView) findViewById(R.id.imageBack);
        btn_PayNow = (Button) findViewById(R.id.btn_PayNow);
        tv_Status = (TextView) findViewById(R.id.tv_status);
        editTextPromoCode = (EditText) findViewById(R.id.tv_PromoCode);
        tv_selectedPlan = (TextView) findViewById(R.id.tv_Selected_Plan);
        btn_promoCode = (Button) findViewById(R.id.btn_promoCode);
        InitPlansView();
        //============================================
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]  DEBUG
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
        getRemoteMessage();
        //============================================

        btn_promoCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPromoCode.setVisibility(View.VISIBLE);
                btn_promoCode.setVisibility(View.GONE);
            }
        });

        btn_PayNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //  startFonePaise_Transaction();

                if (TXN_AMOUNT == null || TXN_AMOUNT == "") {
                    dismissProgress();
                    Toast.makeText(mContext, " Please choose the Subscription Plan", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    showProgress();
                    btn_PayNow.setEnabled(false);
                    btn_PayNow.setBackgroundColor(Color.parseColor("#9B9B9B"));
                    showProgress(true);
                    String promoCode = String.valueOf(editTextPromoCode.getText());
                    int rdmNuM = rand.nextInt(50) + 1;
                    REQUEST_TYPE = "DEFAULT";
                    uuid = uuid.replace("-", "");
                    if(promoCode!=null && !promoCode.isEmpty()){
                        CUST_ID = "PROMO"+"@"+promoCode+customerID+ rdmNuM;
                    }else{
                        CUST_ID = "MDK"+customerID+ rdmNuM;
                    }
                    ORDER_ID =uuid;
                    MID = "RetDig03944840906164";//"MYDUKA26141672397932";//Mydukan MID is "MYDUKA26141672397932"     // ORDER_ID = mPromocode + "-" + uuid;
                    //CUST_ID = mPromocode + "@" + customerID + 1 + "@" + rdmNuM;// mUserName+"-"+mUserPhoneNum+"-"+mUserEmailId;//mUserId;//
                   // mUserName+"-"+mUserPhoneNum+"-"+mUserEmailId;//mUserId;//
                    CHANNEL_ID = "WAP";
                    INDUSTRY_TYPE_ID = "Retail109";
                    WEBSITE = "RetDigWAP";
                    THEME = "merchant";
                    EMAIL = mUserEmailId;
                    MOBILE_NO = mUserPhoneNum;
                    GENERATECHECKSUM_URL = "https://mydukan-firebase.000webhostapp.com/V3/generateChecksum.php";     //"https://mydukan-firebase.000webhostapp.com/generateChecksum.php";   //"https://mydukandev.000webhostapp.com/generateChecksum.php";

                    paramMap1.put("MID", "RetDig03944840906164 ");
                    paramMap1.put("ORDER_ID", ORDER_ID);
                    paramMap1.put("CUST_ID", CUST_ID);
                    //Chskb4546   paramMap1.put("CUST_ID", CUST_ID);
                    paramMap1.put("INDUSTRY_TYPE_ID", "Retail109");
                    paramMap1.put("CHANNEL_ID", "WAP");
                    paramMap1.put("TXN_AMOUNT", TXN_AMOUNT);
                    paramMap1.put("WEBSITE", "RetDigWAP");
                    paramMap1.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+ORDER_ID);
                    paramMap1.put("EMAIL", EMAIL);
                    paramMap1.put("MOBILE_NO",MOBILE_NO);

                    getCheckSumHash(mContext, (HashMap) paramMap1);
                    showProgress(false);
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(false);
                onBackPressed();
            }
        });
    }

    public void showProgress() {
        if (!isFinishing()) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = ProgressSpinner.show(PaytmGatewayActivity.this, "Loading Payment Gateway Page...", "");
            mProgress.setCancelable(false);
            mProgress.setCanceledOnTouchOutside(false);
        }
    }

    public void dismissProgress() {
        try {
            if (!isFinishing() && mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = null;
        } catch (Exception e) {

        }
    }

    private String createPostBody(Map<String, String> params) {
        StringBuilder sbPost = new StringBuilder();
        for (String key : params.keySet()) {
            if (params.get(key) != null) {
                sbPost.append("\r\n" + "--" + BOUNDARY + "\r\n");
                sbPost.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n\r\n");
                sbPost.append(params.get(key));
            }
        }

        return sbPost.toString();
    }
    private void getCheckSumHash(Context context, final Map<String, String> map ) {
        mContext = context;
        mPtmCheckSumInfo = (HashMap) map;
        showProgress(true);
        String Request_type = "https://deducible-stair.000webhostapp.com/V3/generateChecksum.php";
//		String Request_type = "https://mydukan-firebase.000webhostapp.com/V3/generateChecksum.php";
        StringRequest strRequest = new StringRequest(Request.Method.POST, Request_type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);

                            response = response.replaceAll("[()]", "");
                            String[] splitString = response.split("=>");
                            if (splitString.length > 1) {
                                String strings = splitString[splitString.length - 1];
                                strings = strings.replace("\\)", "");
                                checksumValue = strings.trim();
                                onStartTransaction(checksumValue,mPtmCheckSumInfo);
                                showProgress(false);
                            }
                            Log.e("Response: ", response);
                            Log.e("Checksum: ", checksumValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showProgress(false);
                            //Toast.makeText(mContext, "Exception . Please try again later.", Toast.LENGTH_LONG).show();
                            Toast.makeText(PaytmGatewayActivity.this, "Connectivity issue. Please try again later or You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App.\"+\"\\n\"+\"for support contact 9036770772", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG_checkSUM", "Error: " + error);
                        showProgress(false);
                        Toast.makeText(PaytmGatewayActivity.this, "Check the Internet Connection or please try Try after some time." , Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                return headers;
            }

            @Override
            public byte[] getBody() {
                Map<String,String> vparams = new HashMap<>();
                String odr=  String.valueOf(mPtmCheckSumInfo.get("ORDER_ID"));
                vparams.put("MID", "RetDig03944840906164");
                vparams.put("CUST_ID", String.valueOf(mPtmCheckSumInfo.get("CUST_ID")));
                vparams.put("ORDER_ID", String.valueOf(mPtmCheckSumInfo.get("ORDER_ID")));
                vparams.put("INDUSTRY_TYPE_ID", "Retail109");
                vparams.put("CHANNEL_ID", "WAP");
                vparams.put("TXN_AMOUNT", String.valueOf(mPtmCheckSumInfo.get("TXN_AMOUNT")));
                vparams.put("WEBSITE", "RetDigWAP");
                vparams.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+odr);
                vparams.put("EMAIL", String.valueOf(mPtmCheckSumInfo.get("EMAIL")));
                vparams.put("MOBILE_NO", String.valueOf(mPtmCheckSumInfo.get("MOBILE_NO")));
                String postBody = createPostBody(vparams);

                return postBody.getBytes();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaytmGatewayActivity.this);
        requestQueue.add(strRequest);
    }

    public void onStartTransaction(String checkSumHashValue, HashMap param) {
        PaytmPGService Service = PaytmPGService.getProductionService();
        showProgress(true);
        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.


        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("MID", "RetDig03944840906164");
        paramMap.put("ORDER_ID", ORDER_ID);
        paramMap.put("CUST_ID", CUST_ID);
     //   Chskb4546  paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail109");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT",  TXN_AMOUNT);
        paramMap.put("WEBSITE", "RetDigWAP");
        paramMap.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+ORDER_ID);
        paramMap.put("EMAIL", EMAIL);
        paramMap.put("MOBILE_NO",MOBILE_NO);
        paramMap.put("CHECKSUMHASH" , checkSumHashValue);



        //paramMap.put("CHECKSUMHASH" , "w2QDRMgp1/BNdEnJEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");
        Log.e("Paytm Payload: ", String.valueOf(paramMap));
        Log.e("checkSumHashValue", checkSumHashValue);

        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Log.e("UIError: ", inErrorMessage);

                        showProgress(false);
                        Log.i("Failure", "someUIErrorOccurred " + inErrorMessage);
                        Toast.makeText(mContext, "PayTm:" + inErrorMessage, Toast.LENGTH_LONG).show();
                        showOkAlert(mContext, "MyDukan Subscription", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");

                    }
//Bundle[{STATUS=PENDING, CHECKSUMHASH=yayvHuiJGN7N8rdleEPMTeN1C1OG2npkxuR+Wuaid/1IQS5bQWJc30H7KUYmGuoGzzmTmTuJ+ALWOvMd5utzkAE0iQOvdUGJCYhI07N3avc=, BANKNAME=, ORDERID=df77e6672a6f4d99b8764e52db32856a, TXNAMOUNT=3.00, TXNDATE=2017-10-31 11:49:38.0, MID=RetDig03944840906164, TXNID=7302753116, RESPCODE=400, PAYMENTMODE=PPI, BANKTXNID=, CURRENCY=INR, GATEWAYNAME=WALLET, RESPMSG=Transaction status not confirmed yet.}]
//Bundle[{STATUS=TXN_SUCCESS, CHECKSUMHASH=mpR76b6ODnP84yw0uVO8omrUBmq+QSgXU4+7UMnfYfZV6Jc9KoWLzMhMiRCxz6BkzF/D1FAfWi/OSluUad7OZySN7PoeuPD3VEugHlMtxJA=, BANKNAME=, ORDERID=443fe491734c4d7a8fe287fbf3856a68, TXNAMOUNT=3.00, TXNDATE=2017-10-31 12:12:47.0, MID=RetDig03944840906164, TXNID=7302796892, RESPCODE=01, PAYMENTMODE=PPI, BANKTXNID=16153656766, CURRENCY=INR, GATEWAYNAME=WALLET, RESPMSG=Txn Successful.}]
                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.e("LOG", "Payment Transaction : " + inResponse);
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);

                        showProgress(false);
                        // After successful transaction this method gets called.
                        // Response bundle contains the merchant response
                        // parameters.
                        //Bundle[{STATUS=TXN_SUCCESS, BANKNAME=, ORDERID=4c45bb65-13a1-46c9-b6a8-889de6e6ad6a, TXNAMOUNT=1.00, TXNDATE=2017-03-05 11:37:18.0, MID=RetDig03944840906164, TXNID=6699268263, RESPCODE=01, PAYMENTMODE=PPI, BANKTXNID=6281549382, CURRENCY=INR, GATEWAYNAME=WALLET, IS_CHECKSUM_VALID=Y, RESPMSG=Txn Successful.}]
                        if (inResponse != null || !(inResponse.isEmpty())) {

                            String STATUS = (String) inResponse.get("STATUS");
                            if (STATUS.equalsIgnoreCase("TXN_SUCCESS")) {
                                appSubscriptionInfo=new AppSubscriptionInfo();
                                String TXNDATE = (String) inResponse.get("TXNDATE");
                                String TXNAMOUNT = (String) inResponse.get("TXNAMOUNT");
                                String MID = (String) inResponse.get("MID");
                                String TXNID = (String) inResponse.get("TXNID");
                                String CURRENCY = (String) inResponse.get("CURRENCY");
                                appSubscriptionInfo.setSubscription_USERID(mUserId);
                                appSubscriptionInfo.setSubscription_PLAN(mSubscription_PLAN);
                                appSubscriptionInfo.setSubscription_DATE(TXNDATE);
                                appSubscriptionInfo.setSubcription_ISVALID("true");
                                appSubscriptionInfo.setSubcription_AMOUNT(TXNAMOUNT);
                                appSubscriptionInfo.setSubcription_TXNID(TXNID);
                                appSubscriptionInfo.setSubcription_CURRENCY(CURRENCY);
                                appSubscriptionInfo.setSubcription_ORDERID(ORDER_ID);
                                appSubscriptionInfo.setSubscription_MID(MID);
                                appSubscriptionInfo.setSubscription_EXPIREDATE(mSubscription_EXPIREDATE);
                                appSubscriptionInfo.setSubscription_DAYS(mSubscription_DAYs);
                                appSubscriptionInfo.setSubcription_EXTRAINFO("MYDUKAN_UserID:" + mUserId + "||" + "User_MobileNO:" + mUserPhoneNum + "||" + "User_EmailId:" + mUserEmailId);
                                updateSubscriptionInfo(mContext, appSubscriptionInfo);
                            }else if (STATUS.equalsIgnoreCase("PENDING")) {

                                showOkAlert(mContext, "Transaction Pending", "An Error has Occured in payment Gateway with Paytm. Your Payment status is PENDING.You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");
                            }
                            else if (STATUS == "TXN_FAILURE") {
                                showOkAlert(mContext, "Transaction Canceled ", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");
                            }
                        }

                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                        showProgress(false);
                        Log.i("Failure", "networkNotAvailable");
                        Toast.makeText(mContext, "Network Connection Error," + "Please check internet connection.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        showProgress(false);
                        Log.i("Failure", "clientAuthenticationFailed " + inErrorMessage);
                        Toast.makeText(mContext, inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.e("onErrorLoadingWebPage: ", inErrorMessage);
                        Log.i("Failure", "someUIErrorOccurred " + inErrorMessage);
                        Toast.makeText(mContext, "PayTm:" + inErrorMessage, Toast.LENGTH_SHORT).show();
                        showOkAlert(mContext, "MyDukan Subscription", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                        Answers.getInstance().logCustom(new CustomEvent("PaytmBack click")
                                .putCustomAttribute("USER_ID/ USER_Email:", mUserDetail.getId() + "/" + mUserDetail.getUserinfo().getEmailid() + "@" + "V" + verCode));
                        // TODO Auto-generated method stub
                        Log.i("Canceled subscription"+TXN_AMOUNT+mSubscription_PLAN , "Canceled the App subscription ");
                        Toast.makeText(mContext, "You have canceled the transaction", Toast.LENGTH_LONG).show();
                        dismissProgress();
                        Intent nIntent = new Intent(PaytmGatewayActivity.this, PaytmGatewayActivity.class);
                        nIntent.putExtra(AppContants.FP_USER_DETAILS, mUserDetail);
                        nIntent.putExtra(AppContants.FP_USER_ID, mUserId);
//                        nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(nIntent);
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        showProgress(false);
                        Log.e("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();

                    }

                });
    }


/*
    private void startTransaction(String CHECKSUMHASH) {

        if (CHECKSUMHASH != null) {
            PaytmPGService Service = PaytmPGService.getProductionService();
           // paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            paramMap.put("CHECKSUMHASH", "CzY3Y7U9ZW+8Nfn7MxECbGjsKYYnWL5kMZTeR3BvBcLd8P3/NeKq4VwAU5kY7fYYo14BH0g0F21TLRSeSIelIuDfsKmz8HwBBjUuEnnqE=");
            // 3TTA5lT02Li2R1gsWyoZxvF+PCUKi933Z9gF4jwgKkxi+vkNVwkEA7a4OAX/K5XqLpA7SB6033IZDpdIR7192bYgC8zoIk9DBqwj/MSzE9I=
            System.out.println("Paytm Payload: " + paramMap);

            Log.d("LOG: ", "REQUEST_TYPE" + "DEFAULT" + "ORDER_ID" + ORDER_ID + "MID" + MID + "CUST_ID" + CUST_ID + "CHANNEL_ID" + CHANNEL_ID + "INDUSTRY_TYPE_ID" + INDUSTRY_TYPE_ID);
            Log.d("LOG", "WEBSITE" + WEBSITE + "TXN_AMOUNT" + TXN_AMOUNT + "EMAIL" + EMAIL + "MOBILE_NO" + MOBILE_NO + "CALLBACK_URL" + "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + ORDER_ID);

            PaytmOrder Order = new PaytmOrder(paramMap);
            Service.initialize(Order, null);

            Service.startPaymentTransaction(PaytmGatewayActivity.this, true, true,
                    new PaytmPaymentTransactionCallback() {

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
                            // Some UI Error Occurred in Payment Gateway Activity.
                            // // This may be due to initialization of views in
                            // Payment Gateway Activity or may be due to //
                            // initialization of webview. // Error Message details
                            // the error occurred.
                            showProgress(false);
                            Log.i("Failure", "someUIErrorOccurred " + inErrorMessage);
                            Toast.makeText(mContext, "PayTm:" + inErrorMessage, Toast.LENGTH_LONG).show();
                            showOkAlert(mContext, "MyDukan Subscription", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");
                        }

                        @Override
                        public void onTransactionResponse(Bundle inResponse) {
                            showProgress(false);
                            // After successful transaction this method gets called.
                            // Response bundle contains the merchant response
                            // parameters.
                            //Bundle[{STATUS=TXN_SUCCESS, BANKNAME=, ORDERID=4c45bb65-13a1-46c9-b6a8-889de6e6ad6a, TXNAMOUNT=1.00, TXNDATE=2017-03-05 11:37:18.0, MID=RetDig03944840906164, TXNID=6699268263, RESPCODE=01, PAYMENTMODE=PPI, BANKTXNID=6281549382, CURRENCY=INR, GATEWAYNAME=WALLET, IS_CHECKSUM_VALID=Y, RESPMSG=Txn Successful.}]
                            if (inResponse != null || !(inResponse.isEmpty())) {

                                String STATUS = (String) inResponse.get("STATUS");
                                if (STATUS == "TXN_SUCCESS") {
                                    String TXNDATE = (String) inResponse.get("TXNDATE");
                                    String TXNAMOUNT = (String) inResponse.get("TXNAMOUNT");
                                    String MID = (String) inResponse.get("MID");
                                    String TXNID = (String) inResponse.get("TXNID");
                                    String CURRENCY = (String) inResponse.get("CURRENCY");
                                    appSubscriptionInfo.setSubscription_USERID(mUserId);
                                    appSubscriptionInfo.setSubscription_PLAN(mSubscription_PLAN);
                                    appSubscriptionInfo.setSubscription_DATE(TXNDATE);
                                    appSubscriptionInfo.setSubcription_ISVALID("true");
                                    appSubscriptionInfo.setSubcription_AMOUNT(TXNAMOUNT);
                                    appSubscriptionInfo.setSubcription_TXNID(TXNID);
                                    appSubscriptionInfo.setSubcription_CURRENCY(CURRENCY);
                                    appSubscriptionInfo.setSubcription_ORDERID(ORDER_ID);
                                    appSubscriptionInfo.setSubscription_MID(MID);
                                    appSubscriptionInfo.setSubscription_EXPIREDATE(mSubscription_EXPIREDATE);
                                    appSubscriptionInfo.setSubscription_DAYS(mSubscription_DAYs);
                                    appSubscriptionInfo.setSubcription_EXTRAINFO("MYDUKAN_UserID:" + mUserId + "||" + "User_MobileNO:" + mUserPhoneNum + "||" + "User_EmailId:" + mUserEmailId);
                                    // verifyTransaction( mContext,appSubscriptionInfo);
                                    updateSubscriptionInfo(mContext, appSubscriptionInfo);
                                } else if (STATUS == "TXN_FAILURE") {
                                    showOkAlert(mContext, "Transaction Canceled ", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");
                                }
                            }
                            Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        }

                        @Override
                        public void networkNotAvailable() {
                            // If network is not
                            // available, then this
                            // method gets called.
                            // available, then this
                            // method gets called.
                            showProgress(false);
                            Log.i("Failure", "networkNotAvailable");
                            Toast.makeText(mContext, "Network Connection Error," + "Please check internet connection.", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                            // This method gets called if client authentication
                            // failed. // Failure may be due to following reasons //
                            // 1. Server error or downtime. // 2. Server unable to
                            // generate checksum or checksum response is not in
                            // proper format. // 3. Server failed to authenticate
                            // that client. That is value of payt_STATUS is 2. //
                            // Error Message describes the reason for failure.
                            showProgress(false);
                            Log.i("Failure", "clientAuthenticationFailed " + inErrorMessage);
                            Toast.makeText(mContext, inErrorMessage, Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                            showProgress(false);
                            // Some UI Error Occurred in Payment Gateway Activity.
                            // // This may be due to initialization of views in
                            // Payment Gateway Activity or may be due to //
                            // initialization of webview. // Error Message details
                            // the error occurred.
                            Log.i("Failure", "someUIErrorOccurred " + inErrorMessage);
                            Toast.makeText(mContext, "PayTm:" + inErrorMessage, Toast.LENGTH_SHORT).show();
                            showOkAlert(mContext, "MyDukan Subscription", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");

                        }

                        // had to be added: NOTE
                        @Override
                        public void onBackPressedCancelTransaction() {
                            Answers.getInstance().logCustom(new CustomEvent("PaytmBack click")
                                    .putCustomAttribute("USER_ID/ USER_Email:", mUserDetail.getId() + "/" + mUserDetail.getUserinfo().getEmailid() + "@" + "V" + verCode));
                            showProgress(false);
                            // TODO Auto-generated method stub
                            Log.i("Canceled subscription", "Canceled the App subscription ");
                            Toast.makeText(mContext, "You have canceled the transaction", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle bundle) {
                            showProgress(false);
                            Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                            Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();

                        }
                    });
        } else {
            showProgress(false);
            Toast.makeText(getBaseContext(), "Payment Transaction checksumhas is null ", Toast.LENGTH_LONG).show();
        }
    }
*/



    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }


    private void InitPlansView() {
        // =================start Radio Button initView =================================

        radioSexGroup = (RadioGroup) findViewById(R.id.radiogp);
        //get string array from source
        String[] websitesArray = getResources().getStringArray(R.array.websites_array);

        //create radio buttons
        for (int i = 0; i < websitesArray.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(websitesArray[i]);
            radioButton.setId(i);
            radioButton.setPadding(24, 24, 24, 24);
            radioButton.setTextSize(18);
            radioButton.setTextColor(Color.BLACK);
            if(i==2){
                radioButton.setTypeface(null, Typeface.BOLD);
            }
            radioSexGroup.addView(radioButton);
        }
        if(radioSexGroup.getChildCount() > 0){
            radioSexGroup.check(radioSexGroup.getChildAt(0).getId());
            //Monthly plan and amount

            TXN_AMOUNT = "51";
            mSubscription_PLAN = "Monthly";
            mValidity = calculateDates(30);
            mSubscription_EXPIREDATE = expriringDate(30);
            mSubscription_DAYs = "30";
            tv_selectedPlan.setText("Selected Plan: " + mSubscription_PLAN + "( ₹ " + TXN_AMOUNT + ")" + "\n" + mValidity);
            btn_PayNow.setVisibility(View.VISIBLE);
        }

        //set listener to radio button group
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = radioSexGroup.getCheckedRadioButtonId();
                RadioButton radioBtn = (RadioButton) findViewById(checkedRadioButtonId);

                Toast.makeText(PaytmGatewayActivity.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
                String mSelection = (String) radioBtn.getText();
               if(mSelection.equalsIgnoreCase("₹ 51/- for One Month")){
                   //Monthly plan and amount

                    TXN_AMOUNT = "51";
                   mSubscription_PLAN = "Monthly";
                   mValidity = calculateDates(30);
                   mSubscription_EXPIREDATE = expriringDate(30);
                   mSubscription_DAYs = "30";
                   tv_selectedPlan.setText("Selected Plan: " + mSubscription_PLAN + "( ₹ " + TXN_AMOUNT + "/- )" + "\n" + mValidity);
                   btn_PayNow.setVisibility(View.VISIBLE);

               }else   if(mSelection.equalsIgnoreCase("₹ 201/- for Six Months")){
                   //HalfYearly plan and amount
                   mSubscription_PLAN = "HalfYearly";

                   TXN_AMOUNT = "201";
                   mValidity = calculateDates(180);
                   mSubscription_EXPIREDATE = expriringDate(180);
                   mSubscription_DAYs = "180";
                   tv_selectedPlan.setText("Selected Plan: " + mSubscription_PLAN + "( ₹ " + TXN_AMOUNT + "/- )" + "\n" + mValidity);
                   btn_PayNow.setVisibility(View.VISIBLE);

               }else  if(mSelection.equalsIgnoreCase("₹ 365/- for One Year"+" "+"\n**(Limited Period Offer)")){
                   //Yearly plan and amount
                   mSubscription_PLAN = "Yearly";
                   TXN_AMOUNT = "365";
                   mValidity = calculateDates(365);
                   mSubscription_EXPIREDATE = expriringDate(365);
                   mSubscription_DAYs = "365";
                   tv_selectedPlan.setText("Selected Plan: " + mSubscription_PLAN + "( ₹ " + TXN_AMOUNT + "/- )" + "\n" + mValidity);
                   btn_PayNow.setVisibility(View.VISIBLE);
                }else{
                   //Default plan and amount
                   TXN_AMOUNT = "51";
                   mSubscription_PLAN = "Monthly";
                   mValidity = calculateDates(30);
                   mSubscription_EXPIREDATE = expriringDate(30);
                   mSubscription_DAYs = "30";
                   tv_selectedPlan.setText("Selected Plan: " + mSubscription_PLAN + "(₹ " + TXN_AMOUNT + "/- )" + "\n" + mValidity);
                   btn_PayNow.setVisibility(View.VISIBLE);
               }
            }
        });

        // =================END Radio Button initView =================================
    }

/*
    public void addListenerOnButton() {

        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

        RadioButton mRadioButton = (RadioButton) findViewById(R.id.radioMonth);
        if (mRadioButton.isChecked()) {
            //Monthly plan and amount
            TXN_AMOUNT = "51.00";
            mSubscription_PLAN = "Monthly";
            mValidity = calculateDates(30);
            mSubscription_EXPIREDATE = expriringDate(30);
            mSubscription_DAYs = "30";
            tv_selectedPlan.setText("Selected Plan:" + mSubscription_PLAN + "(" + TXN_AMOUNT + ")" + "\n" + mValidity);
            btn_PayNow.setVisibility(View.VISIBLE);
        }
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("chk", "id" + checkedId);
                if (checkedId == R.id.radioMonth) {
                    //Monthly plan and amount
                    TXN_AMOUNT = "51.00";
                    mSubscription_PLAN = "Monthly";
                    mValidity = calculateDates(30);
                    mSubscription_EXPIREDATE = expriringDate(30);
                    mSubscription_DAYs = "30";
                    tv_selectedPlan.setText("Selected Plan:" + mSubscription_PLAN + "(" + TXN_AMOUNT + ")" + "\n" + mValidity);
                    btn_PayNow.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioSixMonth) {
                    //HalfYearly plan and amount
                    mSubscription_PLAN = "HalfYearly";
                    TXN_AMOUNT = "306.00";
                    mValidity = calculateDates(180);
                    mSubscription_EXPIREDATE = expriringDate(180);
                    mSubscription_DAYs = "180";
                    tv_selectedPlan.setText("Selected Plan:" + mSubscription_PLAN + "(" + TXN_AMOUNT + ")" + "\n" + mValidity);
                    btn_PayNow.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioYear) {
                    //Yearly plan and amount
                    mSubscription_PLAN = "Yearly";
                    TXN_AMOUNT = "509.00";
                    mValidity = calculateDates(365);
                    mSubscription_EXPIREDATE = expriringDate(365);
                    mSubscription_DAYs = "365";
                    tv_selectedPlan.setText("Selected Plan:" + mSubscription_PLAN + "(" + TXN_AMOUNT + ")" + "\n" + mValidity);
                    btn_PayNow.setVisibility(View.VISIBLE);
                } else {
                    //Default plan and amount
                    TXN_AMOUNT = "51.00";
                    mSubscription_PLAN = "Monthly";
                    mValidity = calculateDates(30);
                    mSubscription_EXPIREDATE = expriringDate(30);
                    mSubscription_DAYs = "30";
                    tv_selectedPlan.setText("Selected Plan:" + mSubscription_PLAN + "(" + TXN_AMOUNT + ")" + "\n" + mValidity);
                    btn_PayNow.setVisibility(View.VISIBLE);
                }
            }
        });
    }*/
    @Override
    public void onBackPressed() {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        super.onBackPressed();
    }

    public static void showOkAlert(Context context, String title, String message, String btnText) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, btnText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    // [START display_welcome_message]

    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    private void getRemoteMessage() {
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
                    Toast.makeText(PaytmGatewayActivity.this, "Failed to fetch the data, please check the Internet connection or try later", Toast.LENGTH_SHORT).show();
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
    private void displayWelcomeMessage(){
        // [START get_config_values]
        String welcomeMessage = mFirebaseRemoteConfig.getString(REMOTE_MESSAGE_KEY);
        // [END get_config_values]LOADING_PHRASE_CONFIG_KEY5
        if (mFirebaseRemoteConfig.getBoolean(REMOTE_ENABLE_PROMOCODE)) {
            promocodeGroup.setVisibility(View.VISIBLE); //SET AS Visible if REMOTE_ENABLE_PROMOCODE=1
        } else {
            promocodeGroup.setVisibility(View.GONE);//SET AS GONE if REMOTE_ENABLE_PROMOCODE=0
        }
    }
    // [END display_welcome_message]
/*    public void onStartTransaction(String mPromocode) {
        // TXN_AMOUNT=txn_AMOUNT;

        PaytmPGService Service = null;
        Service = PaytmPGService.getProductionService();
        String uuid1 = UUID.randomUUID().toString();
        int rdmNuM = rand.nextInt(50) + 1;
        UniqueOrderid = (mUserPhoneNum + "MYDHUKAN" + uuid).toString();
        REQUEST_TYPE = "DEFAULT";
        ORDER_ID = mPromocode + "-" + uuid;
        MID = "RetDig03944840906164";//"MYDUKA26141672397932";//Mydukan MID is "MYDUKA26141672397932"
        CUST_ID = mPromocode + "@" + customerID + 1 + "@" + rdmNuM;// mUserName+"-"+mUserPhoneNum+"-"+mUserEmailId;//mUserId;//
        CHANNEL_ID = "WAP";
        INDUSTRY_TYPE_ID = "Retail109"; //"Retail";
        WEBSITE = "RetDigWAP"; //  "APP_STAGING"
        THEME = "merchant";
        EMAIL = "";
        MOBILE_NO = mUserPhoneNum;
        CALLBACK_URL = "https://mydukan-firebase.000webhostapp.com/V2/verifyChecksum.php";    // "https://mydukan-firebase.000webhostapp.com/verifyChecksum.php";    //"https://mydukandev.000webhostapp.com/verifyChecksum.php";
        GENERATECHECKSUM_URL = "https://mydukan-firebase.000webhostapp.com/V2/generateChecksum.php";     //"https://mydukan-firebase.000webhostapp.com/generateChecksum.php";   //"https://mydukandev.000webhostapp.com/generateChecksum.php";

        Map<String, String> paramMap = new HashMap<String, String>();
        // these are mandatory parameters
        paramMap.put("REQUEST_TYPE", REQUEST_TYPE);
        paramMap.put("ORDER_ID", uuid1);
        paramMap.put("MID", MID);
        paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("TXN_AMOUNT", TXN_AMOUNT);
        paramMap.put("THEME", THEME);
        paramMap.put("EMAIL", EMAIL);
        paramMap.put("MOBILE_NO", MOBILE_NO);
        paramMap.put("CALLBACK_URL", CALLBACK_URL); // enter verification checksum URL

        Log.d("LOG: ", "REQUEST_TYPE" + "DEFAULT" + "ORDER_ID" + uuid + "MID" + MID + "CUST_ID" + CUST_ID + "CHANNEL_ID" + CHANNEL_ID + "INDUSTRY_TYPE_ID" + INDUSTRY_TYPE_ID);
        Log.d("LOG", "WEBSITE" + WEBSITE + "TXN_AMOUNT" + TXN_AMOUNT + "EMAIL" + EMAIL + "MOBILE_NO" + MOBILE_NO + "CALLBACK_URL" + CALLBACK_URL);
        PaytmOrder Order = new PaytmOrder(paramMap);
        PaytmMerchant Merchant = new PaytmMerchant(GENERATECHECKSUM_URL, CALLBACK_URL);
        // PaytmMerchant Merchant = new PaytmMerchant( "http://hostname/<checksum-gerneration-URL>", " http://hostname/<checksum-verification-URL>");
        Service.initialize(Order, Merchant, null);
        Service.startPaymentTransaction(mContext, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Log.i("Failure", "someUIErrorOccurred " + inErrorMessage);
                        Toast.makeText(mContext, "PayTm:" + inErrorMessage, Toast.LENGTH_LONG).show();
                        showOkAlert(mContext, "MyDukan Subscription", "An Error has Occured in Loading the payment Gateway Page. You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App." + "\n" + "for support contact 9036770772", "OK");
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // Response bundle contains the merchant response
                        // parameters.
                        //Bundle[{STATUS=TXN_SUCCESS, BANKNAME=, ORDERID=4c45bb65-13a1-46c9-b6a8-889de6e6ad6a, TXNAMOUNT=1.00, TXNDATE=2017-03-05 11:37:18.0, MID=RetDig03944840906164, TXNID=6699268263, RESPCODE=01, PAYMENTMODE=PPI, BANKTXNID=6281549382, CURRENCY=INR, GATEWAYNAME=WALLET, IS_CHECKSUM_VALID=Y, RESPMSG=Txn Successful.}]
                        if (inResponse != null || !(inResponse.isEmpty())) {

                            String TXNDATE = (String) inResponse.get("TXNDATE");
                            String TXNAMOUNT = (String) inResponse.get("TXNAMOUNT");
                            String MID = (String) inResponse.get("MID");
                            String TXNID = (String) inResponse.get("TXNID");
                            String CURRENCY = (String) inResponse.get("CURRENCY");

                            appSubscriptionInfo.setSubscription_USERID(mUserId);
                            appSubscriptionInfo.setSubscription_PLAN(mSubscription_PLAN);
                            appSubscriptionInfo.setSubscription_DATE(TXNDATE);
                            appSubscriptionInfo.setSubcription_ISVALID("true");
                            appSubscriptionInfo.setSubcription_AMOUNT(TXNAMOUNT);
                            appSubscriptionInfo.setSubcription_TXNID(TXNID);
                            appSubscriptionInfo.setSubcription_CURRENCY(CURRENCY);
                            appSubscriptionInfo.setSubcription_ORDERID(ORDER_ID);
                            appSubscriptionInfo.setSubscription_MID(MID);
                            appSubscriptionInfo.setSubscription_EXPIREDATE(mSubscription_EXPIREDATE);
                            appSubscriptionInfo.setSubscription_DAYS(mSubscription_DAYs);
                            appSubscriptionInfo.setSubcription_EXTRAINFO("MYDUKAN_UserID:" + mUserId + "||" + "User_MobileNO:" + mUserPhoneNum + "||" + "User_EmailId:" + mUserEmailId);
                            // verifyTransaction( mContext,appSubscriptionInfo);
                            updateSubscriptionInfo(mContext, appSubscriptionInfo);
                        }

                        Log.d("LOG", "Payment Transaction is successful " + inResponse);

                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage, Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(mContext, inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Log.i("Failure", "networkNotAvailable");
                        Toast.makeText(mContext, "Network Connection Error", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Log.i("Failure", "clientAuthenticationFailed " + inErrorMessage);
                        Toast.makeText(mContext, inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Toast.makeText(mContext, inErrorMessage, Toast.LENGTH_LONG).show();
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Answers.getInstance().logCustom(new CustomEvent("PaytmBack click")
                                .putCustomAttribute("USER_ID/ USER_Email:", mUserDetail.getId() + "/" + mUserDetail.getUserinfo().getEmailid() + "@" + "V" + verCode));

                        // TODO Auto-generated method stub
                        Log.i("Canceled subscription", "Canceled the App subscription ");
                        Toast.makeText(mContext, "You have canceled the transaction", Toast.LENGTH_LONG).show();

                    }
                });
    }

    public static void showOkAlert(Context context, String title, String message, String btnText) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, btnText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }*/

    public void startGateWay(Context context, User userdetails, String userID) {
        mContext = context;
        mUserDetail = userdetails;
        mUserId = userID;
        mApp = (MyDukan) mContext.getApplicationContext();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
            int i = 20;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AppSubscriptionInfo appSubscriptionInfo = null;
        if (mUserDetail != null) {
            //mUserId=userID;
            mUserEmailId = mUserDetail.getUserinfo().getEmailid();
            mUserName = mUserDetail.getUserinfo().getName();
            String uPhoneNum = mUserDetail.getUserinfo().getNumber();
            mUserEmailId = mUserDetail.getUserinfo().getEmailid();
            if (uPhoneNum.length() > 10) {
                mUserPhoneNum = uPhoneNum.substring(uPhoneNum.length() - 10, uPhoneNum.length());
            }
            customerID = mUserName + "@" + mUserPhoneNum + "@" + "V" + verCode;
            customerID = customerID.replace(" ", "");
        }

    }

    private String calculateDates(int days) {
        Date current_Date = new Date();
        Date now1 = addDays(current_Date, days);
        DateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = (formatter_date.format(current_Date));
        String date2 = (formatter_date.format(now1));
        //new SimpleDateFormat("yyyy-MM-dd").format(date);
        String ValidityDate = null;

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String xyz = formatter.format(current_Date);
        String abc = formatter.format(now1);
        ValidityDate = "Valid From " + xyz + "  To " + abc;
        return ValidityDate;
    }


    private String expriringDate(int days) {
        Date current_Date = new Date();
        Date now1 = addDays(current_Date, days);
        DateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = (formatter_date.format(current_Date));
        String date2 = (formatter_date.format(now1));
        //new SimpleDateFormat("yyyy-MM-dd").format(date);
        return date2;
    }

    /**
     * add days to date in java
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    //============================
    private void updateSubscriptionInfo(final Context mContext, final AppSubscriptionInfo appSubscriptionInfo) {
        mUserDetail.setAppSubscriptionInfo(appSubscriptionInfo);
        // appSubscriptionInfo.setSubscription_PLAN("Monthly");
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(AppContants.SUBSCRIPTION_DATE, appSubscriptionInfo.getSubscription_DATE());
        userInfo.put(AppContants.SUBSCRIPTION_PLAN, appSubscriptionInfo.getSubscription_PLAN());
        userInfo.put(AppContants.SUBSCRIPTION_ISVALID, appSubscriptionInfo.getSubcription_ISVALID());
        userInfo.put(AppContants.SUBSCRIPTION_CURRENCY, appSubscriptionInfo.getSubcription_CURRENCY());
        userInfo.put(AppContants.SUBSCRIPTION_AMOUNT, appSubscriptionInfo.getSubcription_AMOUNT());
        userInfo.put(AppContants.SUBSCRIPTION_EXTRAINFO, appSubscriptionInfo.getSubcription_EXTRAINFO());
        userInfo.put(AppContants.SUBSCRIPTION_ORDERID, appSubscriptionInfo.getSubcription_ORDERID());
        userInfo.put(AppContants.SUBSCRIPTION_TXNID, appSubscriptionInfo.getSubcription_TXNID());
        userInfo.put(AppContants.SUBSCRIPTION_EXFIREDATE, appSubscriptionInfo.getSubscription_EXPIREDATE());
        userInfo.put(AppContants.SUBSCRIPTION_DAYS, appSubscriptionInfo.getSubscription_DAYS());
        userInfo.put(AppContants.SUBSCRIPTION_MID, "RetDig03944840906164");
        userInfo.put(AppContants.SUBSCRIPTION_TRIALDAYS, "7");
        if (appSubscriptionInfo.getSubscription_TRIALSTARTDATE() == "" || appSubscriptionInfo.getSubscription_TRIALSTARTDATE() == null) {
            userInfo.put(AppContants.SUBSCRIPTION_TRIALSTARTDATE, appSubscriptionInfo.getSubscription_DATE());
        } else {
            userInfo.put(AppContants.SUBSCRIPTION_TRIALSTARTDATE, appSubscriptionInfo.getSubscription_TRIALSTARTDATE());
        }
        userInfo.put(AppContants.SUBSCRIPTION_USERID, mApp.getFirebaseAuth().getCurrentUser().getUid());

        ApiManager.getInstance(mContext).appsubcription(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                userInfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i(MyDukan.LOGTAG, "User updated successfully");

                        if (mUserDetail != null) {
                            if (mUserDetail.getAppSubscriptionInfo() != null) {
                                return;
                            }
                        }
                    }
                    @Override
                    public void onFailure(String response) {
                        Log.i(MyDukan.LOGTAG, "Failed to update user profile");
                    }
                });

        //Initialize AppDukan   appsubcription
        ApiManager.getInstance(mContext).updateUserSubscription(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                userInfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i(MyDukan.LOGTAG, "User updated successfully");

                        if (mUserDetail != null) {
                            if (mUserDetail.getAppSubscriptionInfo() != null) {
                                Answers.getInstance().logCustom(new CustomEvent("PaytmTXN successful")
                                        .putCustomAttribute("USER_ID/ USER_Email:", mUserDetail.getId() + "/" + mUserDetail.getUserinfo().getEmailid()));

                                String orderid = appSubscriptionInfo.getSubcription_ORDERID();
                                String orderAmt = appSubscriptionInfo.getSubcription_AMOUNT();
                                String txnId = appSubscriptionInfo.getSubcription_TXNID();
                                String Subcription_Plan = appSubscriptionInfo.getSubscription_PLAN();
                                String plan = "";
                                if (Subcription_Plan == "HalfYearly") {
                                    plan = "6 months(180 days)";
                                } else if (Subcription_Plan == "Monthly") {
                                    plan = "30 days";
                                } else if (Subcription_Plan == "Yearly") {
                                    plan = "one year(365 days)";
                                }
                                tv_Status.setText("Dear Customer,Payment of " + "\u20B9" + orderAmt + " has been recieved towards MyDukan subscription. Your transaction is successful." + "\n" + "\n Your subscription is valid " + plan + ".\nYour TransactionId:" + txnId + "\n" + "Thank you.");
                                btn_PayNow.setVisibility(View.GONE);
                                radioButtonGroup.setVisibility(View.GONE);
                                promocodeGroup.setVisibility(View.GONE);
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

}
