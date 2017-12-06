package org.app.mydukan.server;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.app.mydukan.data.PaytmOrderInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.tag;

/**
 * Created by Shivu on 25-03-2017.
 */

public class VolleyApiManager {

    private static String TAG;
    private static RequestQueue rq;
     private static String url;// = "https://pguat.paytm.com/oltp/HANDLER_INTERNAL/TXNSTATUS?JsonData={%22MID%22:%22klbGlV59135347348753%22,%22ORDERID%22:%22ORDER48886809916%22}";
    private static PaytmOrderInfo paytmOrderInfo;
    Context mContext;

    public void verifyTransaction( Context context,Object mid, Object orderid){
        mContext=context;
        // Tag used to cancel the request
        rq = Volley.newRequestQueue(mContext);// rq != null
        final String mMID= String.valueOf(mid);
        final String ORDERID= String.valueOf(orderid);

        //  String tag_json_obj = "json_obj_req";  3f9a7956-47f5-4291-a86a-349286296bf7"

        String url = "https://pguat.paytm.com/oltp/HANDLER_INTERNAL/TXNSTATUS?JsonData={%22MID%22:%22"+mMID+"%22,%22ORDERID%22:%22"+ORDERID+"%22}";

        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response!=null){
                            JSONObject jsonObject=response;
                            try {
                                jsonObject = new JSONObject(response.toString());
                                paytmOrderInfo = new PaytmOrderInfo();

                                String paytm_TXNID = jsonObject.getString("TXNID");
                                String paytm_BANKTXNID = jsonObject.getString("BANKTXNID");
                                String paytm_ORDERID = jsonObject.getString("ORDERID");
                                String paytm_TXNAMOUNT = jsonObject.getString("TXNAMOUNT");
                                String paytm_STATUS = jsonObject.getString("STATUS");
                                String paytm_TXNTYPE = jsonObject.getString("TXNTYPE");
                                String paytm_GATEWAYNAME = jsonObject.getString("GATEWAYNAME");
                                String paytm_RESPCODE = jsonObject.getString("RESPCODE");
                                String paytm_RESPMSG = jsonObject.getString("RESPMSG");
                                String paytm_BANKNAME = jsonObject.getString("BANKNAME");
                                String paytm_MID = jsonObject.getString("MID");
                                String paytm_PAYMENTMODE = jsonObject.getString("PAYMENTMODE");
                                String paytm_REFUNDAMT = jsonObject.getString("REFUNDAMT");
                                String paytm_TXNDATE = jsonObject.getString("TXNDATE");

                                paytmOrderInfo.setPaytm_TXNID(paytm_TXNID);
                                paytmOrderInfo.setPaytm_BANKTXNID(paytm_BANKTXNID);
                                paytmOrderInfo.setPaytm_ORDERID(paytm_ORDERID);
                                paytmOrderInfo.setPaytm_TXNAMOUNT(paytm_TXNAMOUNT);
                                paytmOrderInfo.setPaytm_STATUS(paytm_STATUS);
                                paytmOrderInfo.setPaytm_TXNTYPE(paytm_TXNTYPE);
                                paytmOrderInfo.setPaytm_GATEWAYNAME(paytm_GATEWAYNAME);
                                paytmOrderInfo.setPaytm_RESPCODE(paytm_RESPCODE);
                                paytmOrderInfo.setPaytm_RESPMSG(paytm_RESPMSG);
                                paytmOrderInfo.setPaytm_BANKNAME(paytm_BANKNAME);
                                paytmOrderInfo.setPaytm_MID(paytm_MID);
                                paytmOrderInfo.setPaytm_PAYMENTMODE(paytm_PAYMENTMODE);
                                paytmOrderInfo.setPaytm_REFUNDAMT(paytm_REFUNDAMT);
                                paytmOrderInfo.setPaytm_TXNDATE(paytm_TXNDATE);

                                if(paytm_MID == mMID && paytm_ORDERID==ORDERID && paytm_STATUS=="TXN_SUCCESS"){
                                    // updateSubscriptionInfo(mContext, appSubscriptionInfo);
                                }else{
                                    Toast.makeText(mContext,  "Payment Transaction not successful", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        Log.d("Paytm", response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Paytm", "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        rq.add(jsonObjReq);
    }

}
