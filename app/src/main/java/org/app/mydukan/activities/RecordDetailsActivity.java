package org.app.mydukan.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.RecordInfoAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

public class RecordDetailsActivity extends BaseActivity implements RecordInfoAdapter.RecordsInfoAdapterListener {

    //UI reference
    private RecyclerView mRecyclerView;
    private TextView mNoDataView;
    private TextView mSupplierView;
    private TextView mProductView;
    private TextView mAmountView;
    private TextView mQuantityView;

    //Variables
    private MyDukan mApp;
    private RecordInfoAdapter mAdapter;
    private Record mRecordData;
    private Record Filtered_RecordInfoList = new Record();
    private Spinner mProductListSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recorddetails);
        mApp = (MyDukan) getApplicationContext();

        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(AppContants.RECORD)) {
                mRecordData = (Record) bundle.getSerializable(AppContants.RECORD);
            }
        }

        if(mRecordData == null){
            finish();
            return;
        }

        mProductListSpinner = (Spinner) findViewById(R.id.productListSpinner);
        setupActionBar();
        setSummaryView();
        setupRecordListView();
        setupProfessionTypeSpinner();
    }

    private void setupProfessionTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.productlist_filter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.brand_state_spinner);
        // Apply the adapter to the spinner
        mProductListSpinner.setAdapter(adapter);
/*       mProductListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String valSelected = adapterView.getItemAtPosition(i).toString();
                if  (valSelected.equalsIgnoreCase("All")) {
                    //write your answer
                    Filtered_RecordInfoList = mRecordData ;
                    mAdapter.addItems(Filtered_RecordInfoList.getRecordList());
                    mAdapter.notifyDataSetChanged();
                }if (valSelected.equalsIgnoreCase("Settled")) {
                    //write your answer
                    Filtered_RecordInfoList = mRecordData ;
                    ArrayList<RecordInfo> list = getSettledList(Filtered_RecordInfoList);
                    if (list.isEmpty()) {
                    } else {
                        mAdapter.addItems(list);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                 if (valSelected.equalsIgnoreCase("Unsettled")) {

                     ArrayList<RecordInfo> list = getUnSettledList(Filtered_RecordInfoList);
                     if (list.isEmpty()) {
                     } else {
                         mAdapter.addItems(list);
                         mAdapter.notifyDataSetChanged();
                     }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }

    private ArrayList<RecordInfo> getSettledList(Record filtered_RecordInfoList) {
        ArrayList<RecordInfo> result = new ArrayList<RecordInfo>();
        for (RecordInfo product : filtered_RecordInfoList.getRecordList()) {
            if (product.getStatus().equalsIgnoreCase("claim") || product.getStatus().equalsIgnoreCase("Settled by Distributor")) {
                result.add(product);
            }
        }
        return result;
    }

    private ArrayList<RecordInfo> getUnSettledList(Record filtered_RecordInfoList) {
        ArrayList<RecordInfo> result = new ArrayList<RecordInfo>();
        for (RecordInfo product : filtered_RecordInfoList.getRecordList()) {
            if (product.getStatus().equalsIgnoreCase("claim") || product.getStatus().equalsIgnoreCase("Settled by Distributor")) {

            }else{
                result.add(product);
            }
        }
        return result;
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
            getSupportActionBar().setTitle(getString(R.string.recorddetails_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecordListView() {
        mAdapter = new RecordInfoAdapter(RecordDetailsActivity.this ,this);
        mAdapter.addItems(mRecordData.getRecordList());

        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Data");

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                RecordDetailsActivity.this.getApplicationContext(), false));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(RecordDetailsActivity.this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setSummaryView() {
        mSupplierView = (TextView) findViewById(R.id.supplierName);
        mProductView = (TextView) findViewById(R.id.productName);
        mAmountView = (TextView) findViewById(R.id.amount);
        mQuantityView = (TextView) findViewById(R.id.quantity);

        mSupplierView.setText("Supplier Name:" + mRecordData.getSupplierInfo().getName());
        mProductView.setText("Product Name:" +  mRecordData.getProductname());
        mQuantityView.setText("Quantity:" + mRecordData.getRecordList().size());

        long amount = 0l;
        try {
            for (RecordInfo recordInfo: mRecordData.getRecordList()) {
                amount += Long.valueOf(recordInfo.getPrice());
            }
        }catch (Exception e){

        }
        mAmountView.setText("Total Amount: " + mApp.getUtils().getPriceFormat(String.valueOf(amount)));
    }

    @Override
    public void OnUpdateClick(final int position, final String status) {
        RecordInfo info = mRecordData.getRecordList().get(position);
        showProgress();
        ApiManager.getInstance(RecordDetailsActivity.this).updateRecordInfoStatus(mRecordData.getSupplierInfo().getId(),
                mRecordData.getRecordId(), info.getId(), status, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        dismissProgress();
                        mRecordData.getRecordList().get(position).setStatus(status.toLowerCase());
                        mAdapter.addItems(mRecordData.getRecordList());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        showErrorToast(RecordDetailsActivity.this,response);
                    }
                });

    }

    @Override
    public void OnDeleteClick(final int position) {
        RecordInfo info = mRecordData.getRecordList().get(position);
        showProgress();

        ApiManager.getInstance(RecordDetailsActivity.this).deleteRecordInfo(mRecordData.getSupplierInfo().getId(),
            mRecordData.getRecordId(), info.getId(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    dismissProgress();
                    Object mdata= data;
                    mRecordData.getRecordList().remove(position);
                    mAdapter.addItems(mRecordData.getRecordList());
                    mAdapter.notifyDataSetChanged();
                    setSummaryView(); // TODO: 04-03-2017  test for upadate the total amount and quantity.
                }

                @Override
                public void onFailure(String response) {
                    dismissProgress();
                    showErrorToast(RecordDetailsActivity.this,response);
                }
            });

    }
}
