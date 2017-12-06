package org.app.mydukan.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

public class UserProfile extends BaseActivity implements View.OnClickListener {

    private int mViewType = 1; //1 - Sign up , 2 - My Profile.
    private static final String DEFAULT = "User";

    private MyDukan mApp;
    private ChattUser chattUser;
    private User userdetails;
    private String userType="Retailer";
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();


    private static FragmentManager fragmentManager;

    private Button user_Profile_Next;

    private ImageView back_button_1;
    private ImageView user_profile_pic;

    private TextView user_Profile_Name, user_Email_Id, edit_PhoneNo, edit_Address;

    private EditText user_Phone_Number, user_Profile_Location, user_Profile_Pincode, user_Profile_State;

    String firstname, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        overridePendingTransition(R.anim.right_enter, R.anim.left_out);


        mApp = (MyDukan) getApplicationContext();

        user_Profile_Next = (Button) findViewById(R.id.user_Profile_Next);

        back_button_1 = (ImageView) findViewById(R.id.back_button_1);
        user_profile_pic = (ImageView) findViewById(R.id.user_profile_pic);

        user_Profile_Name = (TextView) findViewById(R.id.user_Profile_Name);
        user_Email_Id = (TextView) findViewById(R.id.user_Email_Id);

        edit_PhoneNo = (TextView) findViewById(R.id.edit_PhoneNo);
        edit_Address = (TextView) findViewById(R.id.edit_Address);

        user_Phone_Number = (EditText) findViewById(R.id.user_Phone_Number);
        user_Profile_Location = (EditText) findViewById(R.id.user_Profile_Location);
        user_Profile_Pincode = (EditText) findViewById(R.id.user_Profile_Pincode);
        user_Profile_State = (EditText) findViewById(R.id.user_Profile_State);

        user_Profile_Next.setOnClickListener(this);
        back_button_1.setOnClickListener(this);

        edit_Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent address = new Intent(UserProfile.this, UsersLocationAddress.class);
                address.putExtra(AppContants.VIEW_TYPE, AppContants.USER_PROFILE);
                overridePendingTransition(R.anim.left_enter, R.anim.right_out);
                startActivity(address);

                Answers.getInstance().logCustom(new CustomEvent("UserProfile_Page")
                        .putCustomAttribute("Edit_btn_Address", mApp.getFirebaseAuth().getCurrentUser().getUid()));

            }
        });
        edit_PhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent phone = new Intent(UserProfile.this, NewSignUpActivity.class);
                phone.putExtra(AppContants.VIEW_TYPE, AppContants.USER_PROFILE);
                overridePendingTransition(R.anim.left_enter, R.anim.right_out);
                startActivity(phone);

                Answers.getInstance().logCustom(new CustomEvent("UserProfile_Page")
                        .putCustomAttribute("Edit_btn_PhoneNumber", mApp.getFirebaseAuth().getCurrentUser().getUid()));



           /*     fragmentManager = getSupportFragmentManager();

                MobileVerificationFragment mobileVerificationFragment =new MobileVerificationFragment();
                Bundle bundle=new Bundle();
                bundle.putString(AppContants.VIEW_TYPE, AppContants.USER_PROFILE);
                mobileVerificationFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container_profile,mobileVerificationFragment, Utils.Login_Fragment).commit();

*/
            }
        });

//        InitProfileView(chattUser);
        SharedPreferences sharedPreferences2 = getSharedPreferences("firstname", Context.MODE_PRIVATE);
        firstname = sharedPreferences2.getString("firstname", DEFAULT);
        user_Profile_Name.setText(firstname);
        getCurrentUserData(auth);
        getUserProfile();
    }

    private void getCurrentUserData(FirebaseUser auth) {
        //showProgress(true);
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("chat_USER/" + auth.getUid());
        feedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    chattUser = dataSnapshot.getValue(ChattUser.class);
                    InitProfileView(chattUser);
                }
                // showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void InitProfileView(ChattUser chattUser) {

//        user_Profile_Name.setText(chattUser.getName());
        user_Email_Id.setText(chattUser.getEmail());
        if( chattUser.getPhotoUrl()!=null)
        {
            Glide.with(getApplicationContext())
                    .load( chattUser.getPhotoUrl() )
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50,50)
                    .into(user_profile_pic);
        }else{
            Glide.with(getApplicationContext())
                    .load( R.mipmap.ic_launcher )
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50,50)
                    .into(user_profile_pic);
        }
    }

    private void getUserProfile() {
        showProgress();
        if (mViewType == AppContants.SIGN_UP) {
            mApp.getPreference().setAppState(UserProfile.this, new AppStateContants().PROFILE_SCREEN);
        }
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(UserProfile.this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                userdetails = (User) data;
                if (userdetails == null) {
                    dismissProgress();
                    return;
                }

                Answers.getInstance().logCustom(new CustomEvent("UserProfile_Page")
                        .putCustomAttribute("UserProfile_PageOpened", mApp.getFirebaseAuth().getCurrentUser().getUid() ));

                if (userdetails.getUserinfo() != null) {
//                    user_Profile_Name.setText(mApp.getUtils().toCamelCase(userdetails.getUserinfo().getName()));
//                    user_Email_Id.setText(mApp.getUtils().toSameCase(userdetails.getUserinfo().getEmailid()).toLowerCase());
                    String phoneNum = userdetails.getUserinfo().getNumber();
                    if(phoneNum!=null){
                        user_Phone_Number.setText(phoneNum);

                    }else{
                        user_Phone_Number.setText(userdetails.getUserinfo().getNumber());

                    }
                    // mNumberView.setText(userdetails.getUserinfo().getNumber());
                    // digitsButton.setVisibility(View.GONE);
                    // mValidatedbtn.setVisibility(View.GONE);
                    // mNumberView.setEnabled(true);


                    if (userdetails.getUserinfo().getAddressinfo() != null) {
                        user_Profile_Pincode.setText(userdetails.getUserinfo().getAddressinfo().getPincode());
                        user_Profile_Location.setText(mApp.getUtils().toSameCase(userdetails.getUserinfo().getAddressinfo().getStreet()));
//                        mCityView.setText(mApp.getUtils().toCamelCase(userdetails.getUserinfo().getAddressinfo().getCity()));
                        user_Profile_State.setText(mApp.getUtils().toCamelCase(userdetails.getUserinfo().getAddressinfo().getState()));

                    }


                    //Initialize AppDukan
                }
                dismissProgress();
            }

            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase
                dismissProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            Intent intent = new Intent(UserProfile.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {

            case R.id.user_Profile_Next:

                Intent i = new Intent(UserProfile.this, CompanyDetails.class);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

                Answers.getInstance().logCustom(new CustomEvent("UserProfile_Page")
                        .putCustomAttribute("UserProfile_Confirmed", mApp.getFirebaseAuth().getCurrentUser().getUid()));

                break;

            case R.id.back_button_1:

                Intent back = new Intent(UserProfile.this, MainActivity.class);
                overridePendingTransition(R.anim.left_enter, R.anim.right_out);
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(back);

                break;

            }
    }
}
