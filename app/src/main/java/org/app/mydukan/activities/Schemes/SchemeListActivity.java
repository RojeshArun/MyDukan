package org.app.mydukan.activities.Schemes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.fragments.SchemesPagerFragment;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.fragments.myschemes.fragmetns.MySelectedSchemesHelper;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arpithadudi on 9/9/16.
 */
public class SchemeListActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    //This is our tablayout
    private TabLayout mTabLayout;
    //This is our viewPager
    private ViewPager mViewPager;
    private TextView mNoDataView;

    //Variables
    private MyDukan mApp;
    //List of Schemes
    private HashMap<String, ArrayList<Scheme>> mSchemesList = new HashMap<>();
    private String mSupplierId;
    private String mSupplierName;
    private RadioButton mMySchemesTab, mSchemesTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemeslist);

        mApp = (MyDukan) getApplicationContext();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/1329319399");
        AdView cALdview = (AdView) findViewById(R.id.adView_scmelist);
        AdRequest adRequest = new AdRequest.Builder().build();
        cALdview.loadAd(adRequest);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }
            if (bundle.containsKey(AppContants.SUPPLIER_NAME)) {
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
        }

        //Adding toolbar to the activity
        setupActionBar();

        //Initializing Tablayout
        setupTabLayout();

        //Initializing viewPager
        setupViewPager();

        mNoDataView = (TextView) findViewById(R.id.nodata_view);

        getSchemesList();
        if (mSupplierId != null) {
            if (mSupplierId.equalsIgnoreCase("WDSLSgxI10eiWVey4RVWY5niElE3")) {
                cALdview.setVisibility(View.GONE);
            }
        }

        // Set My Schemes Tab
        mMySchemesTab = (RadioButton) findViewById(R.id.tab_myschemes);
        mMySchemesTab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gotoMySchemesFragment();
                }
            }
        });
        mSchemesTab = (RadioButton) findViewById(R.id.tab_schemes);
    }

    private void gotoMySchemesFragment() {
        mSchemesTab.setOnCheckedChangeListener(null);
        mSchemesTab.setChecked(true);
        Intent mySchemesIntent = new Intent(this, MySchemesActivity.class);
        startActivity(mySchemesIntent);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.schemes_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupTabLayout() {
        //Initializing the tablayout
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Adding onTabSelectedListener to swipe views
        mTabLayout.setOnTabSelectedListener(SchemeListActivity.this);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void setupViewPager() {
        //Initialization of the ViewPager
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        //Creating a  pager adapter
        SchemesPagerFragment adapter = new SchemesPagerFragment(getSupportFragmentManager(), mSupplierName, mSupplierId,
                new ArrayList<ArrayList<Scheme>>(mSchemesList.values()));
        //Adding adapter to pager
        mViewPager.setAdapter(adapter);
    }

    private void setTabs() {
        mTabLayout.removeAllTabs();
        for (Map.Entry<String, ArrayList<Scheme>> entry : mSchemesList.entrySet()) {
            //Adding the tabs using addTab() method
            mTabLayout.addTab(mTabLayout.newTab().setText(entry.getKey()));
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


        ApiManager.getInstance(this).getSchemeRecordList(new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                MySelectedSchemesHelper.getInstance().setRecordList((ArrayList<SchemeRecord>) data);
                dismissProgress();
            }

            @Override
            public void onFailure(String response) {
                dismissProgress();
            }
        });
    }

    private void getSchemesList() {
        showProgress();
        ApiManager.getInstance(SchemeListActivity.this).getSchemeList(mSupplierId, mApp.getFirebaseAuth().getCurrentUser().getUid(),
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        mSchemesList.clear();
                        mSchemesList.putAll((HashMap<String, ArrayList<Scheme>>) data);

                        if (!mSchemesList.isEmpty()) {
                            mNoDataView.setVisibility(View.GONE);
                            mViewPager.setVisibility(View.VISIBLE);

                            setTabs();
                            ((SchemesPagerFragment) mViewPager.getAdapter()).setData(new ArrayList<>(mSchemesList.values()));
                            ((SchemesPagerFragment) mViewPager.getAdapter()).notifyDataSetChanged();
                        } else {
                            mNoDataView.setVisibility(View.VISIBLE);
                            mViewPager.setVisibility(View.GONE);
                        }
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        mNoDataView.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.GONE);
                    }
                });
    }
}
