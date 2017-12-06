package org.app.mydukan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightSequence;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class AddIMEIActivity extends BaseActivity {

    private MyDukan mApp;
    private String mSupplierId;
    private Product product;
    private SupplierBindData mSupplier;

    private Button btnYes, btnNO;
    private ImageButton btnScanner;
    private EditText textIMEInum;
    private static final String INTRO_CARD = "fab_intro";
    private static final String INTRO_SWITCH = "switch_intro";
    private static final String INTRO_RESET = "reset_intro";
    private static final String INTRO_REPEAT = "repeat_intro";
    private static final String INTRO_CHANGE_POSITION = "change_position_intro";
    private static final String INTRO_SEQUENCE = "sequence_intro";
    private boolean isRevealEnabled = true;
    private SpotlightView spotLight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_imei);

        mApp = (MyDukan) getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }
            if (bundle.containsKey(AppContants.SUPPLIER)) {
                mSupplier = (SupplierBindData) bundle.getSerializable(AppContants.SUPPLIER);
            }
            if (bundle.containsKey(AppContants.SUPPLIER)) {
                product = (Product) bundle.getSerializable(AppContants.PRODUCT);
            }
        }

        textIMEInum = (EditText) findViewById(R.id.tv_IMEINum);
        btnYes = (Button) findViewById(R.id.btn_Yes);
        btnNO = (Button) findViewById(R.id.btn_No);

        btnScanner = (ImageButton) findViewById(R.id.btn_scannerIMEI);
     /*  new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Chat with us at Whatsapp")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Have trouble using MyDukan?\nLet us know through whatsapp.")
                .maskColor(Color.parseColor("#dc000060"))
                .target(btnScanner)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("1111") //UNIQUE ID
                .show();


        SpotlightSequence.getInstance(AddIMEIActivity.this,null)
                // .addSpotlight(btnScanner, "Whatsapp Us", "having trouble reach us on whatsapp ", INTRO_SWITCH)
                .addSpotlight(btnScanner, " Scan BarCode ", "Click here to scan a IMEI number", INTRO_SWITCH)
                // .addSpotlight(btn_Subscribe, "MyDukan Subscription", "Subscription information is shown here", INTRO_CARD)
                .startSequence();

*/
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200);
        // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1019_id");
        sequence.setConfig(config);
        config.setDismissTextColor(Color.parseColor("#64DD17"));
        config.setMaskColor(Color.parseColor("#dc4b4b4b"));
        sequence.addSequenceItem(btnScanner,
                "Click here to scan a Barcode of IMEI number", "OK");

//        sequence.addSequenceItem(addsupplierbtn, "Click here to add a supplier", "GOT IT");
       // sequence.addSequenceItem(mToolbar, "This is the navigation bar", "GOT IT");

        sequence.start();


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What ever you want to do with the value
                String imei_str = textIMEInum.getText().toString();
                if(!(imei_str.trim().isEmpty()) && (imei_str!=null) ){
                    addProductToClaimList(product,imei_str);
                    Answers.getInstance().logCustom(new CustomEvent("barcode scanner click").putCustomAttribute("Name", mSupplier.getName()));

                }else{
                    textIMEInum.setError("Please Enter the IMEI");
                }
            }
        });
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanNow(v);
                Answers.getInstance().logCustom(new CustomEvent("barcode scanner click").putCustomAttribute("Name", mSupplier.getName()));

            }
        });

    }

    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
        super.onBackPressed();

    }
    /**
     * event handler for scan button
     *
     * @param view view of the activity
     */
    public void scanNow(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * function handle scan result
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            // display it on screen
            textIMEInum.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private void addProductToClaimList(Product product, String imeiNo) {
        showProgress();
        ApiManager.getInstance(AddIMEIActivity.this).addProductToClaim(mSupplierId,mSupplier.getName(),product,imeiNo, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                String response = (String) data;
                dismissProgress();
                if(response.equalsIgnoreCase(getString(R.string.status_success))){
                    if(!isFinishing())
                        showRecordDialog();
                    return;
                } else {
                    showErrorToast(AddIMEIActivity.this,response);
                }
            }

            @Override
            public void onFailure(String response) {
                dismissProgress();
                showErrorToast(AddIMEIActivity.this,response);
            }
        });
    }


    private void showRecordDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddIMEIActivity.this);

        // set title
        alertDialogBuilder.setTitle("Added Successfully");
        alertDialogBuilder.setIcon(R.drawable.ic_action_about);

        // set dialog message
        alertDialogBuilder
                .setMessage("Price drop product added to your list")
                .setCancelable(true)
                .setPositiveButton(R.string.gotomyrecords,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent record = new Intent(AddIMEIActivity.this, RecordsActivity.class);
                        startActivity(record);
                        finish();
                    }
                })
                .setNegativeButton(R.string.ok,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        onBackPressed();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }



}
