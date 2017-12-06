package org.app.mydukan.utils;

import android.content.Context;
import android.util.Log;

import org.app.mydukan.application.MyDukan;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

/**
 * Created by arpithadudi on 10/3/16.
 */

public class NotificationUtils {

    public final String COMMAND = "COMMAND";
    public final String MESSAGE = "MESSAGE";
    public final String SUPPLIERID = "SUPPLIERID";

    public final String REFRESH = "REFRESH";
    public final String RECORD = "RECORD";

    private Context mContext;

    public NotificationUtils(Context context){
        mContext = context;
    }

    public void refreshTheSupplier(String supplierId, String userId){
        ApiManager.getInstance(mContext).refreshTheSupplierGroups(supplierId,
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.d(MyDukan.LOGTAG, "refreshTheSupplier: " + data);
                    }

                    @Override
                    public void onFailure(String response) {
                        Log.d(MyDukan.LOGTAG, "refreshTheSupplier onFailure: " + response);
                    }
         });
    }

    public void saveNotification(String userId, String message){
        //Add it to the database.
//        ApiManager.getInstance(mContext).addNotifications(userId, message,
//                new ApiResult() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        Log.d(MyDukan.LOGTAG, "addNotifications: " + data);
//                    }
//
//                    @Override
//                    public void onFailure(String response) {
//                        Log.d(MyDukan.LOGTAG, "addNotifications onFailure: " + response);
//                    }
//         });
    }
}
