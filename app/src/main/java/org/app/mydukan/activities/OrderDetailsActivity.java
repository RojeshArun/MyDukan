package org.app.mydukan.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.ComplaintsAdapter;
import org.app.mydukan.adapters.MyOrderAdapter;
import org.app.mydukan.adapters.OrderAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Order;
import org.app.mydukan.data.OrderProduct;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arpithadudi on 10/11/16.
 */

public class OrderDetailsActivity extends BaseActivity {

    //UI reference
    private TextView mOrderId;
    private TextView mTotalAmt;
    private TextView mNoOfProducts;
    private TextView mStatus;
    private TextView mExecutionStatus;
    private TextView mComment;
    private TextView mNameView;
    private TextView mDateView;

    private RecyclerView mRecyclerView;
    private TextView mNoDataView;

    //Varaibles
    private MyDukan mApp;
    private Order mOrder;
    private String mSupplierName;
    private OrderAdapter mAdapter;
    private ArrayList<OrderProduct> mProductsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails);
        mApp = (MyDukan) getApplicationContext();

        setupActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.ORDER)) {
                mOrder = (Order) bundle.getSerializable(AppContants.ORDER);
            }

            if (bundle.containsKey(AppContants.SUPPLIER_NAME)) {
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
        }

        if(mOrder == null){
            return;
        }

        //Get all the summary fields.
        mOrderId = (TextView) findViewById(R.id.orderid);
        mNameView = (TextView) findViewById(R.id.name);
        mTotalAmt = (TextView) findViewById(R.id.amount);
        mNoOfProducts = (TextView) findViewById(R.id.quantity);
        mDateView = (TextView) findViewById(R.id.date);
        mStatus = (TextView) findViewById(R.id.status);
        mExecutionStatus = (TextView) findViewById(R.id.exestatus);
        mComment = (TextView) findViewById(R.id.comment);

        setupProductListView();

        setSummaryCard();
        getProductList();
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
            getSupportActionBar().setTitle(getString(R.string.order_details));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupProductListView() {
        mAdapter = new OrderAdapter(OrderDetailsActivity.this ,null);
        mAdapter.setViewtype(mAdapter.VIEW);

        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Products");

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                OrderDetailsActivity.this.getApplicationContext(), false));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setSummaryCard() {
        mOrderId.setText("Orderid:" + mOrder.getOrderId());
        mNameView.setText("Supplier name:" + mSupplierName);
        mNoOfProducts.setText(getString(R.string.quantity) + ":" + mOrder.getProductList().size());
        mTotalAmt.setText("Total Amount:" +mApp.getUtils().getPriceFormat(String.valueOf(mOrder.getOrderInfo().getTotalamount())));
        mDateView.setText("Ordered on:" + String.valueOf(mApp.getUtils().dateFormatter(mOrder.getOrderInfo().getTimestamp(), "dd-MM-yy")));
        mStatus.setText("Status:" + mApp.getUtils().toCamelCase(mOrder.getOrderInfo().getStatus()));

        if(!mApp.getUtils().isStringEmpty(mOrder.getOrderInfo().getExecutionstatus())){
            mExecutionStatus.setText("Execution Status:" + mApp.getUtils().toCamelCase(mOrder.getOrderInfo().getExecutionstatus()));
        } else {
            mExecutionStatus.setVisibility(View.GONE);
        }

        if(!mApp.getUtils().isStringEmpty(mOrder.getOrderInfo().getComment())){
            mComment.setText("Comment:" + mApp.getUtils().toCamelCase(mOrder.getOrderInfo().getComment()));
        } else {
            mComment.setVisibility(View.GONE);
        }

    }

    private void getProductList() {
        HashMap<String,OrderProduct> mapList = mOrder.getProductList();
        if(!mapList.isEmpty()) {
            for (String productid : mapList.keySet()) {
                OrderProduct orderProduct = mapList.get(productid);
                orderProduct.setProductid(productid);
                mProductsList.add(orderProduct);
            }
            mAdapter.addItems(mProductsList);
            mAdapter.notifyDataSetChanged();
        }else {
            mNoDataView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

}
