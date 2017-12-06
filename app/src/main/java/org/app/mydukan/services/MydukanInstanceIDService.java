package org.app.mydukan.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.app.mydukan.application.MyDukan;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

/**
 * Created by arpithadudi on 9/28/16.
 */

public class MydukanInstanceIDService extends FirebaseInstanceIdService {

    private MyDukan mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (MyDukan) getApplicationContext();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Send the Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        if(mApp.getFirebaseAuth().getCurrentUser() != null) {
            ApiManager.getInstance(this).sendRegistrationId(mApp.getFirebaseAuth().getCurrentUser().getUid(), token, new ApiResult() {
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
