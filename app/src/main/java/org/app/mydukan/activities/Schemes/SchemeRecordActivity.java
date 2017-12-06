package org.app.mydukan.activities.Schemes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.adapters.SchemeRecordAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by arpithadudi on 11/18/16.
 */

public class SchemeRecordActivity extends BaseActivity {

    //UI reference
    private RecyclerView mRecyclerView;
    private TextView mNoDataView;

    //Variables
    private MyDukan mApp;
    private SchemeRecordAdapter mAdapter;
    private ArrayList<SchemeRecord> mRecordList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_records);

        findViewById(R.id.bottomToolbar).setVisibility(View.GONE);

        mApp = (MyDukan) getApplicationContext();

        setupActionBar();
        setupRecordsView();
        fetchTheRecords();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.schemesrecord_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecordsView() {
        mAdapter = new SchemeRecordAdapter(SchemeRecordActivity.this);

        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Records");

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                SchemeRecordActivity.this.getApplicationContext(), false));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SchemeRecordActivity.this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchTheRecords(){
        showProgress();
        ApiManager.getInstance(SchemeRecordActivity.this).getSchemeRecordList(new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                mRecordList = (ArrayList<SchemeRecord>)data;
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
}

