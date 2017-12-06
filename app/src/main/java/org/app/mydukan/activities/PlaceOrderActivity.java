package org.app.mydukan.activities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.OrderAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.OrderProduct;
import org.app.mydukan.data.OrderProduct_test;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arpithadudi on 7/27/16.
 */
public class PlaceOrderActivity extends BaseActivity implements OrderAdapter.OrderAdapterListener {

    private MyDukan mApp;

    private String mSupplierId;


    //Ui reference
    private RecyclerView mOrderRecyclerView;
    private TextView mOrderEmptyView;
    private TextView mTotalQuantity;
    private TextView mGrandTotal;
    private FloatingActionButton mPlaceorder;
    SupplierBindData mSupplier;
    //Variables
    private OrderAdapter mOrderAdapter;
    private ArrayList<OrderProduct> mOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentorder);
        mApp = (MyDukan) getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

        if (bundle.containsKey(AppContants.SUPPLIER)) {

                mSupplier = (SupplierBindData) bundle.getSerializable(AppContants.SUPPLIER);
                mSupplierId =mSupplier.getId();
            }/*
            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }
*/
        }

        mPlaceorder = (FloatingActionButton) findViewById(R.id.productsSubmit);
        mPlaceorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitBtn();
            }
        });

        setupActionBar();
        setupSupplierCard();
    }

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

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.place_order));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    private void setupSupplierCard() {
        View supplierView = findViewById(R.id.orderactivitylayout);
        mOrderAdapter = new OrderAdapter(PlaceOrderActivity.this, this);
        mOrderAdapter.setViewtype(mOrderAdapter.EDIT);

        //setup the recyclerview
        mOrderRecyclerView = (RecyclerView) supplierView.findViewById(R.id.current_orders_list);
        mOrderEmptyView = (TextView) supplierView.findViewById(R.id.noData);
        mTotalQuantity = (TextView) supplierView.findViewById(R.id.totalQty);
        mGrandTotal = (TextView) supplierView.findViewById(R.id.grandTotal_value);
        mPlaceorder = (FloatingActionButton) supplierView.findViewById(R.id.productsSubmit);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(PlaceOrderActivity.this));
        mOrderRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                PlaceOrderActivity.this.getApplicationContext(), false));
        mOrderRecyclerView.setAdapter(mOrderAdapter);
        getCurrentOrdersInTheCart();
    }

    private void getCurrentOrdersInTheCart() {
        showProgress();
        ApiManager.getInstance(PlaceOrderActivity.this).getOrdersFromCart(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                mSupplierId, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                mOrderList = (ArrayList<OrderProduct>) data;
                if(mOrderList.isEmpty()){
                    mOrderEmptyView.setVisibility(View.VISIBLE);
                    mOrderEmptyView.setText(getString(R.string.cartempty));
                }else {
                    mOrderEmptyView.setVisibility(View.GONE);
                    mOrderAdapter.clearItems();
                    mOrderAdapter.addItems(mOrderList);
                    mOrderAdapter.notifyDataSetChanged();
                    updateTheAmount();
                }
                dismissProgress();
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }

    private void updateTheAmount(){
        long totalAmount  = 0;
        long noOfItems = 0;
        for (OrderProduct orderProduct: mOrderList) {
            totalAmount += (orderProduct.getQuantity() * Long.valueOf(orderProduct.getPrice()));
            noOfItems += orderProduct.getQuantity();
        }
        mTotalQuantity.setText(String.valueOf(noOfItems));
        mGrandTotal.setText(String.valueOf(totalAmount));
    }

    private void checkStatusAndThenOrder(){
        showProgress();

        HashMap<String,Long> productIds = new HashMap<>();
        boolean error = false;
        for (OrderProduct prod: mOrderList) {
            productIds.put(prod.getProductid(),prod.getQuantity());
            if(Long.valueOf(prod.getQuantity()) <= 0){
                error = true;
                break;
            }
        }

        if(error){
            showOkAlert(PlaceOrderActivity.this,getString(R.string.info),getString(R.string.error_quantity),getString(R.string.ok));
            dismissProgress();
            return;
        }

        if(productIds.isEmpty()){
            dismissProgress();
            return;
        }

        ApiManager.getInstance(PlaceOrderActivity.this).checkStock(mSupplierId, productIds, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                String response = (String) data;
                if(response.equalsIgnoreCase(getString(R.string.status_success))){
                    placeTheOrder();
                } else {
                    dismissProgress();
                    showOkAlert(PlaceOrderActivity.this,getString(R.string.info),response,getString(R.string.ok));
                }
            }

            @Override
            public void onFailure(String response) {
                showOkAlert(PlaceOrderActivity.this,getString(R.string.info),response,getString(R.string.ok));
            }
        });
    }

    private void placeTheOrder(){
        HashMap<String, Object> orderList = new HashMap<>();
        for (OrderProduct prod: mOrderList) {
            HashMap<String, Object> orders = new HashMap<>();
            orders.put("quantity", Long.valueOf(prod.getQuantity()));
            orders.put("productname", prod.getProductname());
            orders.put("price", prod.getPrice());
            orderList.put(prod.getProductid(), orders);
        }

        HashMap<String, Object> orderinfo = new HashMap<>();
        orderinfo.put("totalamount", mGrandTotal.getText().toString());
        orderinfo.put("status", "pending");
        orderinfo.put("timestamp", System.currentTimeMillis());

        ApiManager.getInstance(PlaceOrderActivity.this).addOrder(mSupplierId, mApp.getFirebaseAuth().getCurrentUser().getUid(),
                orderList, orderinfo, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        String response = (String) data;
                        if (response.equalsIgnoreCase(getString(R.string.status_success))) {
                            deleteAllOrder();
                        } else {
                            dismissProgress();
                            showErrorToast(PlaceOrderActivity.this, response);
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                    }
                });
    }

    private void onSubmitBtn(){
        checkStatusAndThenOrder();
    }

    private void deleteAllOrder(){
        ApiManager.getInstance(PlaceOrderActivity.this).removeAllOrdersFromCart(
            mApp.getFirebaseAuth().getCurrentUser().getUid(), mSupplierId, new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    dismissProgress();
                    finish();
                }

                @Override
                public void onFailure(String response) {
                    dismissProgress();
                    finish();
                }
            });
    }

    private void deleteTheOrder(final int position){
        String id = mOrderList.get(position).getProductid();
        showProgress();
        ApiManager.getInstance(PlaceOrderActivity.this).removeOrdersFromCart(mSupplierId,
                mApp.getFirebaseAuth().getCurrentUser().getUid(), id, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        mOrderList.remove(position);
                        updateTheAmount();
                        mOrderAdapter.clearItems();
                        mOrderAdapter.addItems(mOrderList);
                        mOrderAdapter.notifyDataSetChanged();
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(String response) {
                        showErrorToast(PlaceOrderActivity.this, response);
                        dismissProgress();
                    }
                });

    }

    @Override
    public void OnDeleteClick(int position) {
        showConfirmationAlert(position);
    }

    @Override
    public void OnEditDone(int position, String quantity) {
        showProgress();
        mOrderList.get(position).setQuantity(Integer.valueOf(quantity));
        updateTheAmount();
        mOrderAdapter.notifyDataSetChanged();
        dismissProgress();
    }

    public void showConfirmationAlert(final int position){
        AlertDialog alertDialog = new AlertDialog.Builder(PlaceOrderActivity.this).create();
        alertDialog.setTitle(getString(R.string.info));
        alertDialog.setMessage(getString(R.string.confirm_orderdelete));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTheOrder(position);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
