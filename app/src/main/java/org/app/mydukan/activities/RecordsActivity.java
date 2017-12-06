package org.app.mydukan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.Schemes.SchemeRecordActivity;
import org.app.mydukan.adapters.RecordsAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Record;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by arpithadudi on 10/13/16.
 */

public class RecordsActivity extends BaseActivity implements RecordsAdapter.RecordsAdapterListener{

    //UI reference
    private RecyclerView mRecyclerView;
    private Toolbar mBottomToolBar;
    private TextView mNoDataView;

    //Variables
    private MyDukan mApp;
    private RecordsAdapter mAdapter;
    private ArrayList<Record> mRecordList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        mApp = (MyDukan) getApplicationContext();

        mBottomToolBar = (Toolbar) findViewById(R.id.bottomToolbar);
        mBottomToolBar.findViewById(R.id.schemeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordsActivity.this, SchemeRecordActivity.class);
                startActivity(intent);
            }
        });
//        mBottomToolBar.findViewById(R.mCatId.doaBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(RecordsActivity.this, DoaRecordListActivity.class);
//                startActivity(intent);
//            }
//        });
        setupActionBar();
        BaseActivity.showOkAlert(this,"Records","All Price Drop Model IMEI entered will be stored in Records for Retailer to keep track.","OK");
        setupRecordsView();
        fetchTheRecords();
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

    /**
     * This function sets up the actionbar for the screen
     */
    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.record_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecordsView() {
        mAdapter = new RecordsAdapter(RecordsActivity.this, RecordsActivity.this);

        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Records");

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                RecordsActivity.this.getApplicationContext(), false));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(RecordsActivity.this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchTheRecords(){
        showProgress();
        ApiManager.getInstance(RecordsActivity.this).getRecordsList(new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                mRecordList = (ArrayList<Record>)data;
                if(mRecordList.isEmpty()){
                    mNoDataView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter.addItems(mRecordList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }

    private void deleteTheRecord(final int position){
        showProgress();
        Record record = mRecordList.get(position);
        ApiManager.getInstance(RecordsActivity.this).deleteRecord(record.getSupplierInfo().getId(), record.getRecordId(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                Object mdata=data;
                mRecordList.remove(position);
                mAdapter.addItems(mRecordList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String response) {
                dismissProgress();
                showErrorToast(RecordsActivity.this,response);
            }
        });
    }


    @Override
    public void OnClick(int position) {
        Intent intent = new Intent(RecordsActivity.this, RecordDetailsActivity.class);
        intent.putExtra(AppContants.RECORD, mRecordList.get(position));
        startActivity(intent);
    }

    @Override
    public void OnDeleteClick(int position) {
        if(position >= 0){
            deleteTheRecord(position);
        }
    }
}
