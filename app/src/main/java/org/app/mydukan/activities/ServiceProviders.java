package org.app.mydukan.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.app.mydukan.R;
import org.app.mydukan.adapters.ServiceCenterAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ServiceProviders extends BaseActivity {

    private String selectedBrand = null;
    private String selectedState = null;

    ServiceCenterInfo serviceCenterInfo = null;

    Spinner state_spinner, city_spinner;

    // Declare Variables
    ListView list;
    ServiceCenterAdapter adapter;
    EditText editsearch;
    WebView webView;
    TextView totalServiceCenters,emptyData;
    private MyDukan mApp;
    private ArrayList<ServiceCenterInfo> serviceCenterInfos = new ArrayList<>();
    private ArrayList<ServiceCenterInfo> upServiceCenterPage;
    HashMap<String, List<Object>> state_cityName = new HashMap<>();
    List<String> stateNames;
    List<String> mCity;
    ArrayAdapter<String> City_Adapter;
    ArrayAdapter<String> State_Adapter;
    String stateSelected;
    String citySelacted;
    String mEnable ="true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_providers);

        //fetchServiceCentersData();
        // fetchDropdownData();
        //==================================================================

        //get reference to the brand spinner from the XML layout
        state_spinner = (Spinner) findViewById(R.id.state_spinner);
        emptyData = (TextView) findViewById(R.id.tv_nodata);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if (bundle.containsKey(AppContants.SERVICECENTERS_ENABLE)) {
                 mEnable = bundle.getString(AppContants.SERVICECENTERS_ENABLE);
            }
        }
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(state_spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(1200);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        //attach the listener to the brand spinner

        //get reference to the state spinner from the XML layout
        city_spinner = (Spinner) findViewById(R.id.city_spinner);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(city_spinner);
            // Set popupWindow height to 500px
            popupWindow.setHeight(1200);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        //attach the listener to the state spinner


        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySelacted = mCity.get(position);
                State_Adapter.notifyDataSetChanged();
                fetchServiceCentersData(stateSelected,citySelacted);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateSelected = stateNames.get(position);
                List<Object> listCity =  (state_cityName.get(stateSelected));
                mCity =new ArrayList<>();
                for(Object obj : listCity) {
                    mCity.add(String.valueOf(obj));
                }
                    if (!mCity.isEmpty()) {
                        Collections.sort(mCity, String.CASE_INSENSITIVE_ORDER);
                    }
                //create an ArrayAdaptar for State from the String Array
                City_Adapter = new ArrayAdapter<String>(ServiceProviders.this, R.layout.brand_state_spinner,mCity);
                //set the view for the Drop down list
                City_Adapter.setDropDownViewResource(R.layout.brand_state_spinner);
                //set the ArrayAdapter to the spinner
                city_spinner.setAdapter(City_Adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //==================================================================

        mApp = (MyDukan) getApplicationContext();

        list = (ListView) findViewById(R.id.listview);

        editsearch = (EditText) findViewById(R.id.search);


        // search data here
        fetchDropdownData();
        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                if(text!=null) {
                    if(adapter!=null){
                    adapter.filter(text);
                    }else{
                        Toast.makeText(ServiceProviders.this, "Could't able to find the Data ,Please try after some time.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


     /*
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/index.html");
        */
    }


    private void fetchDropdownData() {
        showProgress();
        try {
            ApiManager.getInstance(this).getState_CityNames(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    state_cityName= (HashMap<String, List<Object>>) data;
                    if (state_cityName.isEmpty()) {
                        dismissProgress();
                        return;

                    } else {
                        setupDropdownList(state_cityName);
                    }

                   dismissProgress();
                }

                @Override
                public void onFailure(String response) {
                    //  mSupplierEmptyView.setVisibility(View.VISIBLE);
                    if (response.equals(getString(R.string.status_failed)))
                        dismissProgress();
                }
            });
        } catch (Exception e) {
            //  dismissProgress();
        }
    }

    private void setupDropdownList(HashMap<String, List<Object>> state_cityName) {
  // stateNames =  state_cityName.keySet();
        stateNames = new ArrayList<>(state_cityName.keySet());
        if (!stateNames.isEmpty()) {
            Collections.sort(stateNames, String.CASE_INSENSITIVE_ORDER);
        }
        //create an ArrayAdaptar for Brand from the String Array
        State_Adapter = new ArrayAdapter<String>(this,R.layout.brand_state_spinner, stateNames);
        //set the view for the Drop down list
        State_Adapter.setDropDownViewResource(R.layout.brand_state_spinner);
        //set the ArrayAdapter to the spinner
        state_spinner.setAdapter(State_Adapter);

    }


   /* private class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {

        super.dismissProgress();
    }


    private void fetchServiceCentersData(String stateSelected, String citySelacted) {

        showProgress();
        try {
            ApiManager.getInstance(this).getServiceCentersList(mApp.getFirebaseAuth().getCurrentUser().getUid(),stateSelected,citySelacted,new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    serviceCenterInfos = (ArrayList<ServiceCenterInfo>) data;

                    if (serviceCenterInfos.isEmpty()) {
                        editsearch.setVisibility(View.GONE);
                        emptyData.setVisibility(View.VISIBLE);
                        dismissProgress();
                        return;
                    } else {
                        emptyData.setVisibility(View.GONE);
                        editsearch.setVisibility(View.VISIBLE);
                        setupServiceCenterPage(serviceCenterInfos);
                        dismissProgress();
                    }
                }

                @Override
                public void onFailure(String response) {
                  //  mSupplierEmptyView.setVisibility(View.VISIBLE);
                    if(response.equals(getString(R.string.status_failed)))
                        dismissProgress();
                }
            });
        } catch (Exception e) {
          //  dismissProgress();
        }
    }

    public void setupServiceCenterPage(ArrayList<ServiceCenterInfo> upServiceCenterPage) {
        this.upServiceCenterPage = upServiceCenterPage;
        //Adapter initialization
        // Pass results to ListViewAdapter Class
        Answers.getInstance().logCustom(new CustomEvent("ServiceCenter")
                .putCustomAttribute("ServiceCenter Info", "Data Received"));

        adapter = new ServiceCenterAdapter(ServiceProviders.this, upServiceCenterPage);
        // Binds the Adapter to the ListView
        list.setAdapter(adapter);
    }
}
