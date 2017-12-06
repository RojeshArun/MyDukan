package org.app.mydukan.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.app.mydukan.R;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.User;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * This is the sample app which will make use of the PG SDK. This activity will
 * show the usage of Paytm PG SDK API's.
 **/

public class MerchantActivity extends Activity {
    String BOUNDARY = "s2retfgsGSRFsERFGHfg";
    HashMap mPtmCheckSumInfo;
    static String checksumValue = null;
    RadioGroup radioGrp;
    Button paynowBTN;
    static  String orderId="",custID="",emailID="",mobNUM="",amount="50";

    Context mContext;
    User mUserDetail;
    String mUserId, mUserEmailId, mUserPhoneNum, mUserName, mSubscription_PLAN, mSubscription_EXPIREDATE, mSubscription_DAYs;
    String REQUEST_TYPE, ORDER_ID, MID, CUST_ID, CHANNEL_ID, TXN_AMOUNT, CALLBACK_URL, INDUSTRY_TYPE_ID, WEBSITE, THEME, EMAIL, MOBILE_NO, GENERATECHECKSUM_URL;

    private List<String> mList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

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
                //	startGateWay(this, mUserDetail, mUserId);
            }
        }





        // initOrderId();
        String uuid = UUID.randomUUID().toString();


        emailID="shivayogih92@gmail.com";
        mobNUM="9886162309";
        amount="60";
        custID="Chskb4546"+amount;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        paynowBTN = (Button) findViewById(R.id.start_transaction);
        paynowBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCheckSumHash();
            }
        });

        GetsubscriptionPlans();

    }

    private void GetsubscriptionPlans() {

            //showProgress(true);
            mList = new ArrayList<>();
            DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("subscription_plans");
            feedReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null){

                        InitPlansView();
                    }
                    // showProgress(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }



    private void InitPlansView() {
        // =================start Radio Button initView =================================
        radioGrp = (RadioGroup) findViewById(R.id.radioGroup);
        //get string array from source
        String[] websitesArray = getResources().getStringArray(R.array.websites_array);

        //create radio buttons
        for (int i = 0; i < websitesArray.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(websitesArray[i]);
            radioButton.setId(i);
            radioButton.setPadding(24, 12, 24, 12);
            radioButton.setTextSize(18);
            radioButton.setTextColor(Color.BLACK);
            radioGrp.addView(radioButton);
        }
        if(radioGrp.getChildCount() > 0){
            radioGrp.check(radioGrp.getChildAt(0).getId());
        }

        //set listener to radio button group
        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = radioGrp.getCheckedRadioButtonId();
                RadioButton radioBtn = (RadioButton) findViewById(checkedRadioButtonId);
                amount= (String) radioBtn.getText();
                Toast.makeText(MerchantActivity.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // =================END Radio Button initView =================================
    }



    //This is to refresh the order id: Only for the Sample App's purpose.
    @Override
    protected void onStart(){
        super.onStart();
        initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


     private void initOrderId() {
            Random r = new Random(System.currentTimeMillis());
            orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                    + r.nextInt(10000);

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







    private void getCheckSumHash( ) {

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
                                onStartTransaction(checksumValue);
                            }
                            Log.e("Response: ", response);
                            Log.e("Checksum: ", checksumValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(mContext, "Exception . Please try again later.", Toast.LENGTH_LONG).show();
                            Toast.makeText(MerchantActivity.this, "Connectivity issue. Please try again later or You can pay Directly by Paytm to 9036770772 & Share your MyDukan QR Code by What's App.\"+\"\\n\"+\"for support contact 9036770772", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG_checkSUM", "Error: " + error);
                        Toast.makeText(MerchantActivity.this, "" + error, Toast.LENGTH_LONG).show();
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
                vparams.put("MID", "RetDig03944840906164");
                vparams.put("ORDER_ID", orderId);
                vparams.put("CUST_ID", custID);
                vparams.put("INDUSTRY_TYPE_ID", "Retail109");
                vparams.put("CHANNEL_ID", "WAP");
                vparams.put("TXN_AMOUNT", amount);
                vparams.put("WEBSITE", "RetDigWAP");
                vparams.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderId);
                vparams.put("EMAIL", emailID);
                vparams.put("MOBILE_NO",mobNUM);
                String postBody = createPostBody(vparams);
                return postBody.getBytes();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MerchantActivity.this);
        requestQueue.add(strRequest);
    }

    public void onStartTransaction(String checkSumHashValue) {
        PaytmPGService Service = PaytmPGService.getProductionService();

        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.

        Map<String, String> paramMap = new HashMap<String, String>();

//		paramMap.put("MID" , "RetDig03944840906164");
//		paramMap.put("ORDER_ID" , "odr444");
//		paramMap.put("CUST_ID" , "CUST555");
//		paramMap.put("INDUSTRY_TYPE_ID" , "Retail109");
//		paramMap.put("CHANNEL_ID" , "WAP");
//		paramMap.put("TXN_AMOUNT" , "1");
//		paramMap.put("WEBSITE" , "RetDigWAP");
//		paramMap.put("CALLBACK_URL" , "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=odr444");
//		paramMap.put("CHECKSUMHASH" , checkSumHashValue);
//		paramMap.put("EMAIL" , "shivayogih72@gmail.com");
//		paramMap.put("MOBILE_NO" , "7760902997");

        paramMap.put("MID", "RetDig03944840906164");
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CUST_ID", custID);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail109");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("WEBSITE", "RetDigWAP");
        paramMap.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderId);
        paramMap.put("EMAIL", emailID);
        paramMap.put("MOBILE_NO",mobNUM);
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
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.e("LOG", "Payment Transaction : " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
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
                        Log.e("clientAuthentication: ", inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.e("onErrorLoadingWebPage: ", inErrorMessage);
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.e("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }
}
