package org.app.mydukan.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.LoginFragment;

import org.app.mydukan.R;
import org.app.mydukan.fragments.MobileVerificationFragment;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.Utils;

public class NewSignUpActivity extends AppCompatActivity {

    private static FragmentManager fragmentManager;
    public static String viewType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sign_up);

        Bundle bundle =getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppContants.VIEW_TYPE)) {
            viewType = bundle.getString(AppContants.VIEW_TYPE);
        }


        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new MobileVerificationFragment(),
                            Utils.Login_Fragment).commit();
        }

        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });

    }


    // Replace Login Fragment with animation
    public void replaceMobileVerificationFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new MobileVerificationFragment(),
                        Utils.Login_Fragment).commit();
    }

    @Override
    public void onBackPressed() {

     /*   // Find the tag of signup and forgot password fragment
        Fragment DemoPage_Fragment = fragmentManager.findFragmentByTag(Utils.DemoPage_Fragment);
        Fragment GmailVerificationFragment = fragmentManager.findFragmentByTag(Utils.SignUp_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if ( GmailVerificationFragment!= null)
            replaceMobileVerificationFragment();
        else if (DemoPage_Fragment != null)
            replaceMobileVerificationFragment();
        else*/
        super.onBackPressed();
    }
}