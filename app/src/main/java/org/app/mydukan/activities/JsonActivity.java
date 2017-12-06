package org.app.mydukan.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppSubscriptionInfo;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.CompositionJso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonActivity extends AppCompatActivity {
    private MyDukan mApp;
    ServiceCenterInfo serviceCenterInfo = null;
    ArrayList<ServiceCenterInfo> serviceCenterInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
/*
                CompositionJso compositionJso = new CompositionJso();
                JSONObject obj;
                obj = compositionJso.makeJSONObject(compoTitle, compoDesc, imgPaths, imageViewPaths);

                try {
                    Writer output;  // getApplicationContext().getPackageName() + "/raw/sound_two"
                    File file = new File(String.valueOf(getAssets().open("eventLists_one.json")));
                    output = new BufferedWriter(new FileWriter(file));
                    output.write(obj.toString());
                    output.close();
                    Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                finish();
            }
            */
            }
        });

        // Reading json file from assets folder
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open("eventLists_one.json")));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ((br != null)) {
                try {
                    br.close(); // stop reading
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            JSONObject jsonObjMain = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObjMain.getJSONArray("events");


            serviceCenterInfos = new ArrayList<ServiceCenterInfo>();
            ArrayList<String> mServiceCenterInfo = new ArrayList<String>();

            for (int i = 0; i < jsonArray.length(); i++) {
                // Creating JSONObject from JSONArray
                serviceCenterInfo = new ServiceCenterInfo();
                JSONObject object = jsonArray.getJSONObject(i);
                serviceCenterInfo.setServicecenter_BRAND(object.getString("Brand"));
                serviceCenterInfo.setServicecenter_CITY(object.getString("City"));
                serviceCenterInfo.setServicecenter_NAME(object.getString("CompanyName"));
                serviceCenterInfo.setServicecenter_ADDRESS(object.getString("CompanyAddress"));
                serviceCenterInfo.setStateservicecenter_STATE(object.getString("State"));
               // serviceCenterInfo.setServicecenter_PINCODE(Long.parseLong(object.getString("PinCode")));
                serviceCenterInfo.setServicecenter_PHONENUMBER(object.getString("PhoneNumber"));
                serviceCenterInfos.add(serviceCenterInfo);
               // String message = object.getString("CompanyName");
               // messages.add(message);
          //      mServiceCenterInfo.add(serviceCenterInfo.getServicecenter_NAME() + "\n" + serviceCenterInfo.getBrand() + "\n" + serviceCenterInfo.getCity());
                if (serviceCenterInfos != null) {
                    // updateServiceCenterInfo(this, serviceCenterInfos);
                }else{
                    Toast.makeText(JsonActivity.this, "list is not added to database", Toast.LENGTH_SHORT).show();
                }
            }
          //  updateServiceCenterInfo(this, serviceCenterInfos);



            ArrayAdapter<String> adapter = new ArrayAdapter<String>(JsonActivity.this, android.R.layout.simple_list_item_1, mServiceCenterInfo);
            ListView list = (ListView) findViewById(R.id.eventList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(JsonActivity.this, "TEST List View", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //=======================================================
    /*
    private void updateServiceCenterInfo(final Context mContext, final ArrayList<ServiceCenterInfo> mServiceCenterInfos) {

        //Initialize AppDukan   appsubcription
        ApiManager.getInstance(mContext).updateServiceCenterList(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                mServiceCenterInfos, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i(MyDukan.LOGTAG, "User updated successfully");
                        Toast.makeText(JsonActivity.this, "list is added to database", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(String response) {
                        Log.i(MyDukan.LOGTAG, "Failed to update user profile");

                    }

                    //==================================================================

                });

    }
    */


}