package org.app.mydukan.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.wooplr.spotlight.SpotlightView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.ComplaintsAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Complaint;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by arpithadudi on 9/13/16.
 */
public class ComplaintsListActivity extends BaseActivity implements ComplaintsAdapter.ComplaintsAdapterListener {

    //UI reference
    private RecyclerView mRecyclerView;
    private TextView mNoDataView;
    private FloatingActionButton mAddComplaintBtn;

    //Variables
    private MyDukan mApp;
    private String mSupplierId;
    private String mSupplierName;
    private ComplaintsAdapter mAdapter;
    private ArrayList<Complaint> mComplaintsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaintslist);

        mApp = (MyDukan) getApplicationContext();
        //====================adds view start=======================================
        //initialization of adview in this activity//


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/3964207396");
        AdView cALdview = (AdView) findViewById(R.id.adView_cmplst);
        AdRequest adRequest = new AdRequest.Builder().build();
        cALdview.loadAd(adRequest);

        //end of adview mobAds//

        //=====================adds view end=====================================

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }
            if (bundle.containsKey(AppContants.SUPPLIER_NAME)) {
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
        }

        //setup actionbar
        setupActionBar();

        mAddComplaintBtn = (FloatingActionButton) findViewById(R.id.addComplaintBtn);
        mAddComplaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComplaintsListActivity.this, AddComplaintsActivity.class);
                intent.putExtra(AppContants.SUPPLIER_ID, mSupplierId);
                intent.putExtra(AppContants.SUPPLIER_NAME, mSupplierName);
                startActivity(intent);
                finish();
            }
        });



/*

        ShowcaseConfig config = new ShowcaseConfig();
        ShowcaseConfig showcaseConfig=new ShowcaseConfig();
        config.setDelay(200);
        //config.setContentTextColor(R.string.content_text_color);
        config.setDismissTextColor(Color.parseColor("#64DD17"));
        config.setMaskColor(Color.parseColor("#dc4b4b4b"));
        // half second between each showcase view   #EA80FC   #D81B60  #009688
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(ComplaintsListActivity.this, "1013_id");
        sequence.setConfig(config);
        sequence.addSequenceItem(mAddComplaintBtn, "Click here to add your Complaint", "NEXT");
        sequence.start();*/


        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Add Supplier")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Add Complaints, Click here to add your Complaint")
                .maskColor(Color.parseColor("#dc000060"))
                .target(mAddComplaintBtn)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("1013") //UNIQUE ID
                .show();

        //==============================================

        setupComplaintsListView();
        fetchComplaintsData();

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

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.complaints_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupComplaintsListView() {
        mAdapter = new ComplaintsAdapter(ComplaintsListActivity.this, mSupplierName, this);

        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Complaints");

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                ComplaintsListActivity.this.getApplicationContext(), true));


        mRecyclerView.setLayoutManager(new LinearLayoutManager(ComplaintsListActivity.this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchComplaintsData() {
        showProgress();
        ApiManager.getInstance(ComplaintsListActivity.this).getComplaintList(mSupplierId, mApp.getFirebaseAuth().getCurrentUser().getUid(),
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        dismissProgress();
                        mComplaintsList = (ArrayList<Complaint>) data;
                        if (mComplaintsList.isEmpty()) {
                            mNoDataView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        } else {
                            mNoDataView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mAdapter.addItems(mComplaintsList);
                            mAdapter.notifyDataSetChanged();
                        }
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();

                    }
                });
    }

    @Override
    public void OnClick(int position) {

    }
}
