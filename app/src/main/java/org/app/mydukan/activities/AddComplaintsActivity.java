package org.app.mydukan.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ComplaintConstants;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.HashMap;

/**
 * Created by arpithadudi on 9/13/16.
 */
public class AddComplaintsActivity extends BaseActivity {

    //UI reference
    private TextView mSupplierNameView;
    private Spinner mComplaintTypeSpinner;
    private EditText mSubjectView;
    private EditText mMessageView;
    private Button mSubmitBtn;

    //Variables
    private MyDukan mApp;
    private String mSupplierId;
    private String mSupplierName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcomplaint);

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

        mSupplierNameView = (TextView) findViewById(R.id.supplierName);
        mComplaintTypeSpinner = (Spinner) findViewById(R.id.complaintTypeSpinner);
        mSubjectView = (EditText) findViewById(R.id.subjectEditText);
        mMessageView = (EditText) findViewById(R.id.messageEditText);

        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitBtnClicked();
            }
        });

        mSupplierNameView.setText(mSupplierName);
        setupComplaintTypeSpinner();
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
            getSupportActionBar().setTitle(getString(R.string.addcomplaint_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupComplaintTypeSpinner(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.complaint_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // Apply the adapter to the spinner
        mComplaintTypeSpinner.setAdapter(adapter);
    }

    private void onSubmitBtnClicked() {
        mSubjectView.setError(null);
        mMessageView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (mApp.getUtils().isStringEmpty(mSubjectView.getText().toString())) {
            mSubjectView.setError(getString(R.string.error_subject_required));
            cancel = true;
            focusView = mSubjectView;
        } else if (mApp.getUtils().isStringEmpty(mMessageView.getText().toString())) {
            mMessageView.setError(getString(R.string.error_message_required));
            cancel = true;
            focusView = mMessageView;
        } else if(mComplaintTypeSpinner.getSelectedItemPosition() == 0){
            showErrorToast(AddComplaintsActivity.this,getString(R.string.error_field_required));
            cancel = true;
            focusView = mComplaintTypeSpinner;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            addSupplierComplaint(mSubjectView.getText().toString().toLowerCase(),
                    mMessageView.getText().toString().toLowerCase(), mComplaintTypeSpinner.getSelectedItem().toString());
        }
    }

    private void  addSupplierComplaint(String subject, String message, String type){
        showProgress();
        ComplaintConstants constants = new ComplaintConstants();
        HashMap<String,Object> complaintInfo = new HashMap<>();
        complaintInfo.put(constants.SUBJECT, subject.toLowerCase());
        complaintInfo.put(constants.MESSAGE,message.toLowerCase());
        complaintInfo.put(constants.COMPLAINTTYPE, type.toLowerCase());
        complaintInfo.put(constants.STATUS, "pending");
        complaintInfo.put(constants.CREATEDDATE,System.currentTimeMillis());

        ApiManager.getInstance(AddComplaintsActivity.this).addComplaint(mSupplierId, mApp.getFirebaseAuth().getCurrentUser().getUid(), complaintInfo, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                String response = (String)data;
                if(response.equalsIgnoreCase(getString(R.string.status_success))){
                    finish();
                } else {
                    showErrorToast(AddComplaintsActivity.this,response);
                }
            }

            @Override
            public void onFailure(String response) {

            }
        });

    }
}
