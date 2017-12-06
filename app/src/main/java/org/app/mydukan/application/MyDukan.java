package org.app.mydukan.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.digits.sdk.android.Digits;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.moe.pushlibrary.MoEHelper;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.AppPreference;
import org.app.mydukan.utils.Utils;
import io.fabric.sdk.android.Fabric;

public class MyDukan extends Application {
    public static final String LOGTAG = "MyDukan";

    private Context mContext;
    private Utils mUtils;
    private AppPreference mPreference;
    private AppContants mAppContants;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        MoEHelper.getInstance(getApplicationContext()).autoIntegrate(this);
//        MultiDex.install(getBaseContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(AppContants.TWITTER_KEY, AppContants.TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(),new TwitterCore(authConfig), new Digits.Builder().build(), new Answers());
        getFirebaseAuth();
        checkAndSendToken();

    }

    public Context getApplicationContext(){
        return mContext;
    }

    public Utils getUtils() {
        if (mUtils == null) {
            mUtils = new Utils();
        }
        return mUtils;
    }

    public AppPreference getPreference() {
        if (mPreference == null) {
            mPreference = new AppPreference();
        }
        return mPreference;
    }

    public AppContants getContants() {
        if (mAppContants == null) {
            mAppContants = new AppContants();
        }
        return mAppContants;
    }

    public FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    public String getUserId() {
        String UserId = null;
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            UserId = String.valueOf(mFirebaseAuth.getInstance().getCurrentUser());
        }
        return UserId;
    }

    public void checkAndSendToken(){
        if(mFirebaseAuth.getCurrentUser() != null) {
            ApiManager.getInstance(getApplicationContext()).checkAndSubscribeForTopic();
            String token = FirebaseInstanceId.getInstance().getToken();

            // Send the Instance ID token to your app server.
            ApiManager.getInstance(this).sendRegistrationId(mFirebaseAuth.getCurrentUser().getUid(), token, new ApiResult() {
                @Override
                public void onSuccess(Object data) {

                }

                @Override
                public void onFailure(String response) {

                }
            });
        }
    }







}
