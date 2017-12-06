package org.app.mydukan.activities.Schemes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.SchemeInfo;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.data.SupplierInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.DatePickerFragment;
import org.app.mydukan.utils.DateTextWatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by arpithadudi on 11/17/16.
 */

public class AddSchemeRecordActivity extends BaseActivity {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    // UI references.
    private EditText mEarningsView;
    private EditText mDateView;
    private EditText mSettledByView;
    private EditText mVoucherView;
    private Switch mSettledBtn;
    private Button mRegisterBtn;

    //Variables
    private MyDukan mApp;
    private SchemeRecord mSchemeRecord;
    private DatePickerFragment mDateFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addschemerecord);
        mApp = (MyDukan) getApplicationContext();

        setupActionBar();

        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(AppContants.SCHEMERECORD)) {
                mSchemeRecord = (SchemeRecord) bundle.getSerializable(AppContants.SCHEMERECORD);
            }
        }

        mEarningsView = (EditText) findViewById(R.id.earningsID);
        mDateView = (EditText) findViewById(R.id.dateID);
        mSettledByView = (EditText) findViewById(R.id.settledbyID);
        mVoucherView = (EditText) findViewById(R.id.voucherID);
        mSettledBtn = (Switch) findViewById(R.id.settledBtn);
        mRegisterBtn = (Button) findViewById(R.id.register);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitBtnClicked();
            }
        });

        mDateView.addTextChangedListener(new DateTextWatcher(AddSchemeRecordActivity.this, mDateView));

        mDateView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePicker();
                }
                return true;
            }
        });
        getSchemeRecordInfo();
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
            getSupportActionBar().setTitle(getString(R.string.schemes_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setTheData(){
        if(mSchemeRecord != null){
            mEarningsView.setText(mSchemeRecord.getEarnings());
            if(!String.valueOf(mSchemeRecord.getDate()).equalsIgnoreCase("0")) {
                mDateView.setText(String.valueOf(mApp.getUtils().dateFormatter(
                        mSchemeRecord.getDate(), DATE_FORMAT)));
            }
            mSettledByView.setText(mSchemeRecord.getSettledby());
            mVoucherView.setText(mSchemeRecord.getVoucherno());
            mSettledBtn.setChecked(mSchemeRecord.getSettled());
        }
    }

    private void onSubmitBtnClicked(){
        mEarningsView.setError(null);
        mDateView.setError(null);
        mSettledByView.setError(null);
        mVoucherView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (mApp.getUtils().isStringEmpty(mEarningsView.getText().toString())) {
            mEarningsView.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = mEarningsView;
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

            SchemeRecord record = new SchemeRecord();
            record.setEarnings(mEarningsView.getText().toString().trim());
            record.setSettledby(mSettledByView.getText().toString().trim());
            record.setVoucherno(mVoucherView.getText().toString().trim());
            record.setSettled(mSettledBtn.isChecked());
            record.setEnrolled(true);

            try {
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                Date date = formatter.parse(mDateView.getText().toString().trim());
                record.setDate(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            record.setSupplierinfo(mSchemeRecord.getSupplierinfo());
            record.setSchemeinfo(mSchemeRecord.getSchemeinfo());

            addSchemeRecord(record);
        }
    }

    private void getSchemeRecordInfo(){
        ApiManager.getInstance(AddSchemeRecordActivity.this).getSchemeRecord(mSchemeRecord.getSchemeinfo().getId(),
                mSchemeRecord.getSupplierinfo().getId(), new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        dismissProgress();
                        if(data != null){
                            mSchemeRecord = (SchemeRecord) data;
                        }
                        setTheData();
                    }

                    @Override
                    public void onFailure(String response) {

                    }
                });
    }

    private void addSchemeRecord(SchemeRecord record){
        showProgress();

        ApiManager.getInstance(AddSchemeRecordActivity.this).addSchemeRecord(record,
                new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                String result = (String) data;
                dismissProgress();
                if(!result.equalsIgnoreCase(getString(R.string.status_success))){
                    showErrorToast(AddSchemeRecordActivity.this, result);
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
}
