package org.app.mydukan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moe.pushlibrary.MoEHelper;

import org.app.mydukan.R;
import org.app.mydukan.adapters.ViewPagerAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NetworkUtil;

import java.util.Date;

/**
 * Created by Shivayogi Hiremath on 7/27/16.
 */
public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, ViewPager.OnPageChangeListener ,View.OnClickListener {

    private static final int INITIAL_REQUEST = 13;

    private ImageButton btnNext, btnFinish;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private User user;
    private MyLocation myLocation = null;


    private int[] mImageResources = {
            R.drawable.promo_1,
            R.drawable.promo_2,
            R.drawable.promo_3,
            R.drawable.promo_4
    };


    //Variables related to Google Sign-In
    private static final String USER_ROOT = "chat_USER";
    private static final String KEY_LOGIN = "key_login";
    private static final int GOOGLE_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth;
    protected MoEHelper helper = null;
    //Variables
    private MyDukan mApp;
    private NetworkUtil networkUtil;
    //UI reference
    private RelativeLayout mGoogleLayout;
    private RelativeLayout mLoginLayout;
    public boolean isNewUserForTrail;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (MyDukan) getApplicationContext();
        networkUtil =new NetworkUtil();
        myLocation = new MyLocation();
        setContentView(R.layout.activity_login);
        helper = MoEHelper.getInstance(this);

        setupGoogleClient();
        setupFirebaseAuthListener();

        intro_images = (ViewPager) findViewById(R.id.pager_introduction);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnFinish = (ImageButton) findViewById(R.id.btn_finish);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        mAdapter = new ViewPagerAdapter(this, mImageResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();

        mGoogleLayout = (RelativeLayout) findViewById(R.id.googleBtn);
        mGoogleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(networkUtil.isConnectingToInternet(LoginActivity.this)){
                    showProgress();
                    signInToGoogle();
                }else{
                    Toast.makeText(LoginActivity.this, "Please check network connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mLoginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        mLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                signInToGoogle();
            }
        });

        showPermissions();
    }


    private void setupGoogleClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void setupFirebaseAuthListener() {
        mFirebaseAuth = mApp.getFirebaseAuth();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    String PREF_NAME=user.getUid();
                    String UNIQUE_ID=user.getUid()+"@"+user.getDisplayName()+"@"+user.getEmail();

                    Log.d(MyDukan.LOGTAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    MoEHelper.getInstance(getApplicationContext()).setUniqueId(UNIQUE_ID); //UNIQUE_ID is used to uniquely identify a user.
                    SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    // app-id:  "B2JUXC91LG9VLJDHNHLWQ1G2"
                    if( pref.contains("B2JUXC91LG9VLJDHNHLWQ1G2")){
                        helper.setExistingUser(true);
                    }else{
                        helper.setExistingUser(false);
                    }
                    Log.d(MyDukan.LOGTAG, "onAuthStateChanged:signed_in:" + user.getDisplayName());
                } else {
                    // User is signed out
                    Log.d(MyDukan.LOGTAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void showPermissions(){
        if (Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)  == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CALL_PHONE)  == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)  == PackageManager.PERMISSION_GRANTED ) {
                Log.e("testing", "Permission is granted");
            } else {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage("Please do not deny any permissions, accept all permissions to enter myDukan")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue

                                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//                                showSettingsAlert();
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

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LoginActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LoginActivity.this.startActivity(intent);
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    private void signInToGoogle() {
        googleLogout();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(MyDukan.LOGTAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgress();
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(MyDukan.LOGTAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            dismissProgress();
                            checkIfUserExist();
                            acct.getDisplayName();
                            acct.getEmail();
                            String fullname = acct.getDisplayName();
                            String email = acct.getEmail();
                            String[] parts = fullname.split("\\s+");
                            Log.d("Length-->",""+parts.length);
                            if(parts.length==2) {
                                String firstname = parts[0];
                                String lastname = parts[1];
                                Log.d("First-->", "" + firstname);
                                Log.d("Last-->", "" + lastname);

                                SharedPreferences sharedPreferences = getSharedPreferences("firstname", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("firstname", firstname);
                                editor.putString("email", email);
                                editor.commit();
                            }
                        } else if (task.isSuccessful()) {
                            dismissProgress();
                            Log.i(MyDukan.LOGTAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // dismissProgress();
                    }
                });
    }

    private void checkIfUserExist() {
    /*Check current user is new user or not .
      if current user is new user display the verification page.
      else show the signup page.
     */

        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            // new user And setting the signed in date in MoEngage.
            isNewUserForTrail = true;
            MoEHelper.getInstance(LoginActivity.this).setUserAttribute("signedUpOn", new Date());
            showProgress();
            ApiManager.getInstance(LoginActivity.this).checkAndSignUpUser(mApp.getFirebaseAuth().getCurrentUser().getUid(), mApp.getFirebaseAuth().getCurrentUser().getEmail(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    //Already this user has account.
                    if(data.toString().equalsIgnoreCase(getString(R.string.status_success))) {
                        dismissProgress();
                        mApp.checkAndSendToken();
                        openActivity();  //Testing code.
                    } else {
                        Log.i(MyDukan.LOGTAG, data.toString());
                        dismissProgress();
                        showErrorToast(LoginActivity.this,data.toString());
                    }
                }

                @Override
                public void onFailure(String response) {
                    //Do when the user type is different.
                    logoutUser();
                    showOkAlert(LoginActivity.this, getResources().getString(R.string.info), response,getResources().getString(R.string.ok));
                    dismissProgress();
                }
            });
        }else{
            isNewUserForTrail = false;
            showProgress();

            ApiManager.getInstance(LoginActivity.this).checkAndSignUpUser(mApp.getFirebaseAuth().getCurrentUser().getUid(), mApp.getFirebaseAuth().getCurrentUser().getEmail(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    //Already this user has account.
                    if(data.toString().equalsIgnoreCase(getString(R.string.status_success))) {
                        dismissProgress();
                        mApp.checkAndSendToken();
                        sendUserFirebase();  // very Importent, this will create the chatt Account for user
                        openActivity();
                    } else {
                        Log.i(MyDukan.LOGTAG, data.toString());
                        dismissProgress();
                        showErrorToast(LoginActivity.this,data.toString());
                    }
                }

                @Override
                public void onFailure(String response) {
                    //Do when the user type is different.
                    logoutUser();
                    showOkAlert(LoginActivity.this, getResources().getString(R.string.info),
                            response,getResources().getString(R.string.ok));
                    dismissProgress();
                }
            });

        }
    }

    private void openActivity() {

        String mUserid=mApp.getFirebaseAuth().getCurrentUser().getUid();
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference().child("users/"+mUserid);
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    user=dataSnapshot.getValue(User.class);
                    String mob_vrify= user.getVrified_mobilenum();
                    String location_verfy=user.getVerified_location();
                    String cmpnyInfo_vrify= user.getVerified_CompanyInfo();

                    if(mob_vrify.equals("false")){
                        Intent intent = new Intent(LoginActivity.this, NewSignUpActivity.class);
                        intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                        startActivity(intent);
                        finish();
                    }else if(location_verfy.equals("false")){
                        Intent intent = new Intent(LoginActivity.this, UsersLocationAddress.class);
                        intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                        startActivity(intent);
                        finish();
                    }
                      /* else if(cmpnyInfo_vrify.equals("false")){
                           Intent intent = new Intent(LoginActivity.this, UserProfile.class);
                           intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                           startActivity(intent);
                           finish();

                       }*/ else{
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Send user Firebase
     */
    private void sendUserFirebase(){
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference().child(USER_ROOT);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            ChattUser chattUser = new ChattUser();
            chattUser.setName( firebaseUser.getDisplayName());
            chattUser.setEmail( firebaseUser.getEmail() );
            chattUser.setPhotoUrl( firebaseUser.getPhotoUrl() == null ? "default_uri" : firebaseUser.getPhotoUrl().toString() );
            chattUser.setuId( firebaseUser.getUid() );
            chattUser.setUserType("");

            referenceUser.child( firebaseUser.getUid()).setValue(chattUser);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showErrorToast(LoginActivity.this,"Authentication failed.");
        dismissProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            if (data!=null){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    // Google Sign In failed, update UI appropriately
                    Log.i(MyDukan.LOGTAG, "Google Sign In failed");
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    dismissProgress();
                }
            }
        }else{
            // Google Sign In failed, update UI appropriately
            Log.i(MyDukan.LOGTAG, "Google Sign In failed");
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            dismissProgress();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("permission","granted");
        } else {
            Log.i("permission","revoked");

        }

    }
    private void logoutUser(){
        mApp.getFirebaseAuth().signOut();
        googleLogout();
        MoEHelper.getInstance(getApplicationContext()).logoutUser();
    }
    private void googleLogout() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_next:
                intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount)
                        ? intro_images.getCurrentItem() + 1 : 0);
                break;

            case R.id.btn_finish:
                finish();
                break;


           /* case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;*/

        }

    }
}
