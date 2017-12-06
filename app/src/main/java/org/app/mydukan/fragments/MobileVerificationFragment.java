package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.app.mydukan.R;
import org.app.mydukan.activities.CustomToast;
import org.app.mydukan.activities.LoginActivity;
import org.app.mydukan.activities.NewSignUpActivity;
import org.app.mydukan.activities.UsersLocationAddress;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.utils.NetworkUtil;
import org.app.mydukan.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class MobileVerificationFragment extends Fragment implements View.OnClickListener, OTPListener {

    private View view;
    private MyDukan mApp;
    private int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_ID = 100;

    public static final String KEY_X_Auth_Key = "X-Auth-Key";
    public static final String KEY_X_Api_Method = "X-Api-Method";
    public static final String KEY_X_Api_Format = "X-Api-Format";

    public static final String KEY_X_Auth_Key_1 = "x-auth-key";
    public static final String KEY_X_Api_Method_1 = "x-api-method";
    public static final String KEY_X_Api_Format_1 = "x-api_format";

    public static final String KEY_OTP = "otp";

    //    String X_Auth_Key = "A02779ac7e86c8807324948db7eabfb9e";
    String X_Auth_Key = "A61dc8aa4ae06f68b7eecc08cbb757b35";
    String X_Api_Method = "otp";
    String X_Api_Format = "json";

    //    String x_auth_key = "A02779ac7e86c8807324948db7eabfb9e";
    String x_auth_key = "A61dc8aa4ae06f68b7eecc08cbb757b35";
    String x_api_method = "otp.verify";
    String x_api_format = "json";

    String status;
    String message;
    String token_id;
    String OtpCode;

    String status_1;
    String message_1;
    String token_id_1;
    String OtpCode_1;

    TextView nextText, mobileNumberText, verified_icon_text;
    private EditText mobile_field, otp_field;
    private Button BtnVerify, BtnResend, BtnSend, BtnNext;
    private LinearLayout loginLayout, otpLayout;
    private RelativeLayout mainlayout;
    private ImageView verified_icon;
    private Animation shakeAnimation;
    private FragmentManager fragmentManager;
    private NetworkUtil networkUtil;
    private ImageView goBackBtn;

    private static final String TAG = "MobileVerificationFragment";

    // json object response url


    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private ProgressBar progressBar;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    public MobileVerificationFragment() {
        // Required empty public constructor
    }

    // Progress dialog
    private ProgressDialog pDialog;
    private String viewType = "";

    private TextView txtResponse;

    // temporary string to show the parsed response
    private String jsonResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_mobile_verification, container, false);

       // viewType = getArguments().getString(AppContants.VIEW_TYPE);

        mApp = (MyDukan) getActivity().getApplicationContext();
        networkUtil = new NetworkUtil();

       // viewType = getArguments().getString(AppContants.VIEW_TYPE);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        OtpReader.bind(this, "MYDUKN");


        initViews();
        setListeners();

        requestRuntimePermissions(android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS);


        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        mainlayout = (RelativeLayout) view.findViewById(R.id.mainlayout);
        verified_icon = (ImageView) view.findViewById(R.id.verified_icon);
        goBackBtn = (ImageView) view.findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(this);


        return view;
    }

    private void requestRuntimePermissions(String... permissions) {
        for (String perm : permissions) {

            if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{perm}, PERMISSION_REQUEST_ID);

            }
        }
    }


    // Initiate Views
    private void initViews() {

        fragmentManager = getActivity().getSupportFragmentManager();

        mobile_field = (EditText) view.findViewById(R.id.mobile_field);
        otp_field = (EditText) view.findViewById(R.id.otp_field);
        BtnSend = (Button) view.findViewById(R.id.BtnSend);
        BtnVerify = (Button) view.findViewById(R.id.BtnVerify);
        BtnResend = (Button) view.findViewById(R.id.BtnResend);
        BtnNext = (Button) view.findViewById(R.id.BtnNext);

       /* mobile_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mobile_field.isClickable()) {

                    intro_images.setVisibility(View.GONE);
                    pager_indicator.setVisibility(View.GONE);
                }
                else{
                    intro_images.setVisibility(View.VISIBLE);
                    pager_indicator.setVisibility(View.VISIBLE);
                }
            }
        });*/

        nextText = (TextView) view.findViewById(R.id.createAccount);
        nextText.setOnClickListener(this);

        mobileNumberText = (TextView) view.findViewById(R.id.mobileNumberText);
        verified_icon_text = (TextView) view.findViewById(R.id.verified_icon_text);

        otpLayout = (LinearLayout) view.findViewById(R.id.otpLayout);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews

        // Assign click listeners
        BtnSend.setOnClickListener(this);
        BtnResend.setOnClickListener(this);
        BtnVerify.setOnClickListener(this);
        BtnNext.setOnClickListener(this);


    }


    // Set Listeners
    private void setListeners() {
        BtnSend.setOnClickListener(this);

       /* // Set check listener over checkbox for showing and hiding password
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });*/
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

       /* // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mobile_field.getText().toString());
        }*/
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]


    private boolean validatePhoneNumber() {
        String phoneNumber = mobile_field.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mobile_field.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.BtnNext:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.BtnSend:

                if (networkUtil.isConnectingToInternet(getActivity())) {
                    checkValidation();
                } else {
                    Toast.makeText(getContext(), "Please check Internet Connection.", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.goBackBtn:
                loginLayout.setVisibility(View.VISIBLE);
                otpLayout.setVisibility(View.GONE);
                break;


            case R.id.BtnVerify:
                OtpCode = otp_field.getText().toString();
                if (TextUtils.isEmpty(OtpCode)) {
                    otp_field.setError("Cannot be empty.");
                    return;
                }
                makeJsonObjectVerifyRequest();


                break;
            case R.id.BtnResend:
                if (networkUtil.isConnectingToInternet(getActivity())) {
                    checkValidation();
                    Toast.makeText(getActivity(), "Resending Code", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getContext(), "Please check Internet Connection.", Toast.LENGTH_LONG).show();
                }
                break;

           /*
           case R.id.forgot_password:
                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
           */
            case R.id.createAccount:

                /*// Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new GmailVerificationFragment(),
                                Utils.SignUp_Fragment).commit();
                break;*/
        }

    }


    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getMobile = mobile_field.getText().toString();
//        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getMobile);

        // Check for both field is empty or not
        if (getMobile.length() != 10) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    "Enter 10 digit Mobile Number.");

            loginLayout.setVisibility(View.VISIBLE);
            otpLayout.setVisibility(View.GONE);

        }
      /*  // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Mobile Number is Invalid.");
            // Else do login and do your stuff*/
        else {
            /*Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getActivity(), "Welcome", Toast.LENGTH_SHORT)
                    .show();*/

            mobileNumberText.setText(getMobile);

//            progressBar.setVisibility(View.VISIBLE);
            pDialog.show();
          /*  loginLayout.setVisibility(View.GONE);
            otpLayout.setVisibility(View.VISIBLE);*/
            makeJsonObjectOTPRequest();
            Answers.getInstance().logCustom(new CustomEvent("MobileNumber_Page")
                    .putCustomAttribute("Mobile_page_Open",getMobile));


        }
    }

    private void makeJsonObjectOTPRequest() {
        pDialog.show();

        String getMobile = mobile_field.getText().toString();
        if (!getMobile.isEmpty()) {
            getMobile = "+91" + getMobile;

        }

        String Request_type = "https://api-valify.solutionsinfini.com/v1/?mobile=" + getMobile;
        StringRequest strRequest = new StringRequest(Request.Method.POST, Request_type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            // Parsing json object response
                            // response will be a json object
                            status = jsonObject.getString("status");
                            message = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");
                            token_id = data.getString("valify_id");

                            if (status.equals("OK")) {
                                loginLayout.setVisibility(View.GONE);
                                otpLayout.setVisibility(View.VISIBLE);
                                pDialog.dismiss();
                                Answers.getInstance().logCustom(new CustomEvent("MobileNumber_Page")
                                        .putCustomAttribute("Mobile_OTP_Request",mobile_field.getText().toString()));

                                new CountDownTimer(60000, 1000) {

                                    public void onTick(long millisUntilFinished) {

                                        BtnResend.setEnabled(false);
                                        BtnResend.setText("Resend After: " + millisUntilFinished / 1000);
//                                            BtnResend.setText("Resend After: " + millisUntilFinished / 1000);

                                        //here you can have your logic to set text to edittext
                                    }

                                    public void onFinish() {
                                       /* if(otp_field!=null)
                                        {
                                            BtnResend.setText("Done!");
                                        }
                                        else {
                                            BtnResend.setText("Resend OTP");
                                        }*/
                                        BtnResend.setText("Resend OTP");
                                        BtnResend.setEnabled(true);
                                    }

                                }.start();
//                                pDialog.dismiss();
                            }

                            if (status.equals("E600")) {
                                validatePhoneNumber();
                                pDialog.dismiss();
                                loginLayout.setVisibility(View.VISIBLE);
                                otpLayout.setVisibility(View.GONE);
                            }

                            if (status.equals("A401B")) {
                                pDialog.dismiss();
                                validatePhoneNumber();
                                loginLayout.setVisibility(View.VISIBLE);
                                otpLayout.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getActivity(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_X_Auth_Key, X_Auth_Key);
                map.put(KEY_X_Api_Method, X_Api_Method);
                map.put(KEY_X_Api_Format, X_Api_Format);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(strRequest);
    }


    private void makeJsonObjectVerifyRequest() {
        pDialog.show();

        String getMobile = mobile_field.getText().toString();
        if (!getMobile.isEmpty()) {
            getMobile = "+91" + getMobile;

        }
        String OtpCode = otp_field.getText().toString();

        String Request_type = "https://api-valify.solutionsinfini.com/v1/?otp=" + OtpCode + "&valify_id=" + token_id;
//                "https://api-valify.solutionsinfini.com/v1/?otp="+OtpCode+"&valify_id="+token_id;
//        "https://api-valify.solutionsinfini.com/v1/?otp="+OtpCode+"&valify_id="+token_id;
        final String finalGetMobile = getMobile;
        StringRequest strRequest = new StringRequest(Request.Method.POST, Request_type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            // Parsing json object response
                            // response will be a json object
                            status_1 = jsonObject.getString("status");
                            message_1 = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");
                            token_id_1 = data.getString("otp");

                            if (status.equals("OK")) {
                                pDialog.dismiss();
                                mainlayout.setVisibility(View.GONE);
                                BtnNext.setVisibility(View.GONE);
                                verified_icon.setVisibility(View.VISIBLE);
                                verified_icon_text.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Verified", Toast.LENGTH_SHORT)
                                        .show();
                                Answers.getInstance().logCustom(new CustomEvent("MobileNumber_Page")
                                        .putCustomAttribute("Mobile_OTP_Verified",mobile_field.getText().toString()));

                                String UserId = mApp.getFirebaseAuth().getCurrentUser().getUid();
                                DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("users/" + UserId + "/userinfo/number");
                                feedReference.setValue(finalGetMobile);
                                DatabaseReference flagVerify_mob = FirebaseDatabase.getInstance().getReference("users/" + UserId + "/vrified_mobilenum");
                                flagVerify_mob.setValue("true");

                                //  updateUserInfo(email,name,Phonum);
                                //https://mydukan-1024.firebaseio.com/users/kVihGQVdNPgwUoHkaLiw6X1czN43/userinfo/number
                                NewSignUpActivity newSignUpActivity =new NewSignUpActivity();
                                if (newSignUpActivity.viewType !=null) {
                                    if (newSignUpActivity.viewType.equalsIgnoreCase("user_profile")){
                                       /* Intent i = new Intent(getContext(), UserProfile.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);*/
                                       getActivity().onBackPressed();
                                    }
                                } else {
                                    Intent i = new Intent(getContext(), UsersLocationAddress.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }

                            }

                            if (status_1.equals("E600")) {
                                validatePhoneNumber();
                                pDialog.dismiss();
                                Toast.makeText(getActivity(), "E600 Error", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            if (status_1.equals("A401B")) {
                                Toast.makeText(getActivity(), "A401B Error", Toast.LENGTH_SHORT)
                                        .show();
                                pDialog.dismiss();

                            }
                            if (status_1.equals("E603")) {
                                BtnVerify.setEnabled(true);
                                BtnVerify.setTextColor(Color.parseColor("#4285F4"));
                                pDialog.dismiss();
                                Toast.makeText(getActivity(), "E603 Error", Toast.LENGTH_SHORT)
                                        .show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + message_1);
                        Toast.makeText(getActivity(),
                                message_1, Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_X_Auth_Key_1, x_auth_key);
                map.put(KEY_X_Api_Method_1, x_api_method);
                map.put(KEY_X_Api_Format_1, x_api_format);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(strRequest);
    }

   /* private void updateUserInfo() {


        ApiManager.getInstance(getActivity()).checkAndSignUpUser(mApp.getFirebaseAuth().getCurrentUser().getUid(), mApp.getFirebaseAuth().getCurrentUser().getEmail(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                //Already this user has account.

            }

            @Override
            public void onFailure(String response) {
                //Do when the user type is different.
               // logoutUser();
                showOkAlert(getActivity(), getResources().getString(R.string.info),
                        response,getResources().getString(R.string.ok));
            }
        });
    }
*/

    @Override
    public void otpReceived(String smsText) {
        //Do whatever you want to do with the text
        if (smsText != null && !(smsText.isEmpty())) {
            if (smsText.length() > 4) {
                String o = smsText.substring(29, 35);
                System.out.println(o);
                String x = smsText.substring(5, smsText.length());
                otp_field.setText(o);
//                Toast.makeText(getContext(),"Got "+smsText,Toast.LENGTH_LONG).show();
                Log.d("Otp", smsText);
            } else {
                System.out.println(smsText);
                Log.d("Otp", smsText);
                BtnResend.setText("Resend OTP");
                pDialog.dismiss();
            }
        }


    }
}
