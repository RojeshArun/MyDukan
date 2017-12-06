package org.app.mydukan.server;

import org.app.mydukan.data.PaytmOrderInfo;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shivu on 25-03-2017.
 */

public interface DataCallback {
    PaytmOrderInfo onSuccess(JSONObject result);
}
