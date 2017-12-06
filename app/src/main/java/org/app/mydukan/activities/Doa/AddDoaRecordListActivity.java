package org.app.mydukan.activities.Doa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Category;
import org.app.mydukan.data.DoaRecord;
import org.app.mydukan.data.ProductInfo;
import org.app.mydukan.data.SupplierInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.DatePickerFragment;
import org.app.mydukan.utils.DateTextWatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by arpithadudi on 11/24/16.
 */

public class AddDoaRecordListActivity extends BaseActivity {

    String mSupplierId = "WDSLSgxI10eiWVey4RVWY5niElE3";
    String mSupplierName = "Mobile Dp For Dealers";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    // UI references.
    private Spinner mCategorySpinner;
    private Spinner mProductNameSpinner;
    private EditText mImeiView;
    private EditText mDateView;
    private EditText mSettledByView;
    private EditText mVoucherView;
    private Switch mSettledBtn;
    private Button mRegisterBtn;

    //Variables
    private MyDukan mApp;
    private DoaRecord mDoaRecord;
    private HashMap<String,String> mProductMap;
    private DatePickerFragment mDateFragment;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backBtnPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddoarecord);
        mApp = (MyDukan) getApplicationContext();

        setupActionBar();

        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(AppContants.DOARECORD)) {
                mDoaRecord = (DoaRecord) bundle.getSerializable(AppContants.DOARECORD);
            }
        }

        mImeiView = (EditText) findViewById(R.id.imeiID);
        mDateView = (EditText) findViewById(R.id.dateID);
        mSettledByView = (EditText) findViewById(R.id.settledbyID);
        mVoucherView = (EditText) findViewById(R.id.voucherID);
        mSettledBtn = (Switch) findViewById(R.id.settledBtn);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        mProductNameSpinner = (Spinner) findViewById(R.id.nameSpinner);
        mRegisterBtn = (Button) findViewById(R.id.register);

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //String item = adapterView.getItemAtPosition(position).toString();

                 if(!mProductNameSpinner.isEnabled()){
                     if(mCategorySpinner.getSelectedItemPosition() == 0){
                         return;
                     }
                     mProductNameSpinner.setEnabled(true);
                     setupNameSpinner();
                 }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mProductNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mImeiView.setEnabled(true);
                mDateView.setEnabled(true);
                mSettledByView.setEnabled(true);
                mVoucherView.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitBtnClicked();
            }
        });

        mDateView.addTextChangedListener(new DateTextWatcher(AddDoaRecordListActivity.this, mDateView));

        mDateView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePicker();
                }
                return true;
            }
        });
        setTheData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backBtnPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backBtnPressed(){
        Intent intent = new Intent(AddDoaRecordListActivity.this, DoaRecordListActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.doarecord_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setTheData(){
        mImeiView.setEnabled(false);
        mDateView.setEnabled(false);
        mSettledByView.setEnabled(false);
        mVoucherView.setEnabled(false);
        mProductNameSpinner.setEnabled(false);
        setupCategorySpinner();
    }

    private void setupCategorySpinner(){
        showProgress();
        ApiManager.getInstance(AddDoaRecordListActivity.this).getCategoryList(mSupplierId,new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                ArrayList<Category> list = (ArrayList<Category>) data;

                if(list != null && !list.isEmpty()){

                    Collections.sort(list,new CategoryComparator());

                    ArrayList<String> categoryNameList = new ArrayList<String>();
                    categoryNameList.add("Select the Category");
                    for (Category category: list) {
                        categoryNameList.add(category.getName());
                    }


                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddDoaRecordListActivity.this,
                            android.R.layout.simple_spinner_item, categoryNameList);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    mCategorySpinner.setAdapter(adapter);
                }
                dismissProgress();
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }

    private void setupNameSpinner(){
        if(mCategorySpinner.getSelectedItemPosition() == 0){
            return;
        }
        showProgress();
        String category = mCategorySpinner.getSelectedItem().toString().trim();
        ApiManager.getInstance(AddDoaRecordListActivity.this).getProductsForCategory(mSupplierId,category,new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                mProductMap = (HashMap<String,String>) data;
                if(mProductMap != null && !mProductMap.isEmpty()){
                    ArrayList<String> productNameList = new ArrayList<String>();
                    productNameList.addAll(mProductMap.keySet());
                    Collections.sort(productNameList,new NameComparator());
                    productNameList.add(0,"Select the Model");

                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddDoaRecordListActivity.this,
                            android.R.layout.simple_spinner_item, productNameList);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    mProductNameSpinner.setAdapter(adapter);
                }
                dismissProgress();
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }

    private void onSubmitBtnClicked(){
        mImeiView.setError(null);
        mDateView.setError(null);
        mSettledByView.setError(null);
        mVoucherView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if(mCategorySpinner.getSelectedItemPosition() == 0){
            showErrorToast(AddDoaRecordListActivity.this,getString(R.string.error_state_category));
            cancel = true;
            focusView = mCategorySpinner;
        } else if(mProductNameSpinner.getSelectedItemPosition() == 0){
            showErrorToast(AddDoaRecordListActivity.this,getString(R.string.error_state_productname));
            cancel = true;
            focusView = mProductNameSpinner;
        } else if (mApp.getUtils().isStringEmpty(mImeiView.getText().toString())) {
            mImeiView.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = mImeiView;
        } else if (mApp.getUtils().isStringEmpty(mDateView.getText().toString())) {
            mDateView.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = mDateView;
        } else if (mApp.getUtils().isStringEmpty(mVoucherView.getText().toString())) {
            mVoucherView.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = mVoucherView;
        } else if(mSettledBtn.isChecked() && mApp.getUtils().isStringEmpty(mSettledByView.getText().toString())){
            mSettledByView.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = mSettledByView;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            DoaRecord record = new DoaRecord();
            record.setSettledby(mSettledByView.getText().toString().trim());
            record.setVoucherno(mVoucherView.getText().toString().trim());
            record.setSettled(mSettledBtn.isChecked());

            try {
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                Date date = formatter.parse(mDateView.getText().toString().trim());
                record.setDate(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SupplierInfo supplierInfo = new SupplierInfo();
            supplierInfo.setName(mSupplierName);
            supplierInfo.setId(mSupplierId);
            record.setSupplierinfo(supplierInfo);

            ProductInfo productInfo = new ProductInfo();
            productInfo.setImei(mImeiView.getText().toString());
            productInfo.setCategory(mCategorySpinner.getSelectedItem().toString().trim());
            String name = mProductNameSpinner.getSelectedItem().toString();
            productInfo.setName(name);
            if(mProductMap.containsKey(name)){
                productInfo.setId(mProductMap.get(name));
            } else {
                showErrorToast(AddDoaRecordListActivity.this,getString(R.string.error_state_productname));
                return;
            }
            record.setProductinfo(productInfo);

            addDoaRecord(record);
        }
    }

    private void addDoaRecord(DoaRecord record){
        showProgress();

        ApiManager.getInstance(AddDoaRecordListActivity.this).addDoaRecord(record,
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        String result = (String) data;
                        dismissProgress();
                        if(!result.equalsIgnoreCase(getString(R.string.status_success))){
                            showErrorToast(AddDoaRecordListActivity.this, result);
                        }else{
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String response) {

                    }
                });

    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                sdf.setCalendar(calendar);
                mDateView.setText(sdf.format(calendar.getTime()));
            }
        };

        mDateFragment = new DatePickerFragment();
        mDateFragment.setCallBack(ondate);

        if(!mDateFragment.isVisible()){
            mDateFragment.show(getSupportFragmentManager(), "datePicker");

        }
    }

    private class CategoryComparator implements Comparator<Category> {
        public int compare(Category c1, Category c2) {
            return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
        }
    }

    private class NameComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

}
