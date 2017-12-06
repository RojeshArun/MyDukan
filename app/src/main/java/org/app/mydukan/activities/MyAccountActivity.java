package org.app.mydukan.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

public class MyAccountActivity extends AppCompatActivity {

    private MyDukan mApp;
    private String myDukhan_UserId;
    private User userdetails;
    TextView profileDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //  userdetails =  getIntent().getExtras().getParcelable("UserDetails"); //The key argument here must match that used in the other activity
            myDukhan_UserId = extras.getString("MyDhukhan_UserId");
            userdetails = (User) getIntent().getExtras().getSerializable("UserDetails");
            if (userdetails != null) {
                //Initialise the Activity View.
                initAccountView(myDukhan_UserId,userdetails);

            }
        } else {
            getUserProfile();
        }
        //init and declare the ViewParts

        profileDetails= (TextView) findViewById(R.id.tv_profileDetails);

    }

    private void initAccountView(String myDukhan_userId, User userdetails) {
        String userName=userdetails.getUserinfo().getName();
        String userEmailID=userdetails.getUserinfo().getEmailid();
        String userNunmer=userdetails.getUserinfo().getNumber();
        String userAddress=userdetails.getUserinfo().getAddressinfo().getStreet()+"\n"+userdetails.getUserinfo().getAddressinfo().getCity()+userdetails.getUserinfo().getAddressinfo().getCountry()+userdetails.getUserinfo().getAddressinfo().getPincode();
        String mDetailHTML="";
        profileDetails.setText(userName);

    }

    //get the User Details From the FIreBase.
    private void getUserProfile() {
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    userdetails = (User) data;
                    myDukhan_UserId=mApp.getFirebaseAuth().getCurrentUser().getUid();
                    if (userdetails!=null){
                      //  initView(this, myDukhan_UserId, userdetails);//Initialise the Activity View.
                    }

                    return;
                }
            }
            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase

            }
        });
    }

}
