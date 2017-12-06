package org.app.mydukan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CategoryAdapter;
import org.app.mydukan.adapters.ComplaintsAdapter;
import org.app.mydukan.adapters.MyOrderAdapter;
import org.app.mydukan.adapters.OrderAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Category;
import org.app.mydukan.data.Complaint;
import org.app.mydukan.data.Order;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class OrderListActivity extends BaseActivity implements MyOrderAdapter.OrderAdapterListener {

    //UI reference
    private RecyclerView mRecyclerView;
    private TextView mNoDataView;

    //Variables
    private MyDukan mApp;
    private String mSupplierId;
    private String mSupplierName;
    private MyOrderAdapter mAdapter;
    private ArrayList<Order> mOrderList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderlist);

        mApp = (MyDukan) getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }

            if (bundle.containsKey(AppContants.SUPPLIER_NAME)) {
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
        }

        //setup actionbar
        setupActionBar();

        setupListView();
        fetchCategoryData();
    }
    public void onBackPressed() {
          finish();
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
            getSupportActionBar().setTitle(getString(R.string.order_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListView() {
        mAdapter = new MyOrderAdapter(OrderListActivity.this,mSupplierName,this);

        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Data");

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                OrderListActivity.this.getApplicationContext(), false));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(OrderListActivity.this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchCategoryData(){
        showProgress();
        ApiManager.getInstance(OrderListActivity.this).getOrderList(mSupplierId,mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                mOrderList = (ArrayList<Order>) data;
                if(mOrderList.isEmpty()){
                    mNoDataView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    Collections.sort(mOrderList,new OrderComparator());

                    mAdapter.addItems(mOrderList);
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
        if(position >= 0){
            Order order = mOrderList.get(position);
            Intent intent = new Intent(OrderListActivity.this, OrderDetailsActivity.class);
            intent.putExtra(AppContants.ORDER, order);
            intent.putExtra(AppContants.SUPPLIER_NAME, mSupplierName);
            startActivity(intent);
        }
    }

    private class OrderComparator implements Comparator<Order> {
        public int compare(Order o1, Order o2) {
            return o1.getOrderInfo().getTimestamp()<o2.getOrderInfo().getTimestamp()?-1:
                    o1.getOrderInfo().getTimestamp()>o2.getOrderInfo().getTimestamp()?1:0;
        }
    }
}
