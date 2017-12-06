package org.app.mydukan.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.ProfileContants;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CompanyDetails extends BaseActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 224;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String myDhukhan_UserId;
    private String userID;

    private String userChoosenTask;
    private Bitmap mSelectedBitmap;

    private CharSequence[] BrandsList = {"APPLE", "SAMSUNG", "GIONEE", "LAVA", "HTC", "ASUS", "VIVO", "ITEL", "ZIOX", "OPPO",
            "JIO LYF", "ZEN", "INTEX", "CELKON", "XIAOMI", "TECNO", "LENOVO", "ZOPO", "M-TECH", "EDGE MOBILE", "GOOGLE PIXEL",
            "MIROMAX FEATURE PHONES", "PANASONIC", "ACER", "INFOCUS", "HUAWEI", "LG", "XCESS MOBILE", "NOKIA", "MOTOROLA", "LEPHONE",
            "YU", "VIDEOCON", "SONY", "IBALL", "KARBONN", "COMIO", "EXMART", "LEMON", "MAFE", "MOZOMAXX", "SAMSUNG ACCESSORIES",
            "SPICE", "XILLION","OTHERS"};

    private ArrayList<CharSequence> selectedValues = new ArrayList<>();

    private CharSequence[] ProfessionList = {"Retailer", "Distributor", "Brand Promoters", "Company Professional",
            "Shop Executive", "Service Centres", "Wholesaler", "Manufacturers / Brands", "Distributor Sales Executive"};

    private ArrayList<CharSequence> selectedProfession = new ArrayList<>();

    HashMap<String, Object> companyInfo;
    //Company Info
    HashMap<String, Object> otherInfo;


    private int mViewType = 1; //1 - Sign up , 2 - My Profile.

    private MyDukan mApp;
    private ChattUser chattUser;
    private User userdetails;
    private String userType="Retailer";
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

    private Button user_Business_Card, user_Profile_Submit ;

    private ImageView back_button_2;

    private EditText user_Company_Name, user_My_Profession, user_Brand_Selection, user_GST_Nmber;
    private EditText user_office_Mail, user_Shop_Nmber, user_Distributor_Nmber;

    private LinearLayout linear_Brand, linear_GST, linear_officeEmail, linear_ShopNumber, linear_DistributorNumber;

    String Company_Name, My_Profession, Brand_Selection, GST_Number, office_Mail, Shop_Nmber, Distributor_Nmber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
//        overridePendingTransition(R.anim.right_enter, R.anim.left_out);

        mApp = (MyDukan) getApplicationContext();
        myDhukhan_UserId = mApp.getFirebaseAuth().getCurrentUser().getUid();

        back_button_2 = (ImageView) findViewById(R.id.back_button_2);

        user_Business_Card = (Button) findViewById(R.id.user_Business_Card);
        user_Profile_Submit = (Button) findViewById(R.id.user_Profile_Submit);

        user_Company_Name = (EditText) findViewById(R.id.user_Company_Name);
        user_My_Profession = (EditText) findViewById(R.id.user_My_Profession);
        user_Brand_Selection = (EditText) findViewById(R.id.user_Brand_Selection);
        user_GST_Nmber = (EditText) findViewById(R.id.user_GST_Nmber);
        user_office_Mail = (EditText) findViewById(R.id.user_office_Mail);
        user_Shop_Nmber = (EditText) findViewById(R.id.user_Shop_Nmber);
        user_Distributor_Nmber = (EditText) findViewById(R.id.user_Distributor_Nmber);

        linear_Brand = (LinearLayout) findViewById(R.id.linear_Brand);
        linear_GST = (LinearLayout) findViewById(R.id.linear_GST);
        linear_officeEmail = (LinearLayout) findViewById(R.id.linear_officeEmail);
        linear_ShopNumber = (LinearLayout) findViewById(R.id.linear_ShopNumber);
        linear_DistributorNumber = (LinearLayout) findViewById(R.id.linear_DistributorNumber);


        back_button_2.setOnClickListener(this);

        user_Business_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        user_Profile_Submit.setOnClickListener(this);

        user_My_Profession.setOnClickListener(this);
        user_Brand_Selection.setOnClickListener(this);


        getUserProfile();

    }

    private void getUserProfile() {
        showProgress();
        if (mViewType == AppContants.SIGN_UP) {
            mApp.getPreference().setAppState(CompanyDetails.this, new AppStateContants().PROFILE_SCREEN);
        }
        if (mApp.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }
        ApiManager.getInstance(CompanyDetails.this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {

                userdetails = (User) data;
                if (userdetails == null) {
                    dismissProgress();
                    return;
                }

                Answers.getInstance().logCustom(new CustomEvent("CompanyDetails_Page")
                        .putCustomAttribute("CompanyDetails_Opened",  myDhukhan_UserId));

                if (userdetails.getCompanyinfo() != null) {
                    user_Company_Name.setText(mApp.getUtils().toCamelCase(userdetails.getCompanyinfo().getName()));
                    user_GST_Nmber.setText(userdetails.getCompanyinfo().getVatno());
                    user_office_Mail.setText(mApp.getUtils().toCamelCase(userdetails.getCompanyinfo().getEmailid()));


                   /*
                   if (!mApp.getUtils().isStringEmpty(userdetails.getCompanyinfo().getIndustry())) {
                        String userType = userdetails.getCompanyinfo().getIndustry().trim().toLowerCase();
                        for (int i = 0; i < mIndustryTypeList.size(); i++) {
                            String value = mIndustryTypeList.get(i).trim().toLowerCase();
                            if (userType.equalsIgnoreCase(value)) {
                                mIndustryTypeSpinner.setSelection(i++);
                                break;
                            }
                        }
                    }
                   */
                    dismissProgress();//remove it
                    if (!mApp.getUtils().isStringEmpty(userdetails.getCompanyinfo().getCardurl())) {
                        user_Business_Card.setText(getString(R.string.change));
                    }
                }

                    if (userdetails.getOtherinfo() != null && !mApp.getUtils().isStringEmpty(userdetails.getOtherinfo().getRole()))
                    {
                        String userType = userdetails.getOtherinfo().getRole().trim().toLowerCase();
                        user_My_Profession.setText(mApp.getUtils().toCamelCase(userdetails.getOtherinfo().getRole()));
                        String terms = userdetails.getOtherinfo().getAcceptedprivacypolicy().trim().toLowerCase();


                        if (userType.equalsIgnoreCase("Distributor") )
                        {
                            user_Brand_Selection.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getBrands()));
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);
                        }
                        if(userType.equalsIgnoreCase("Retailer"))
                        {
//                            linear_officeEmail.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);

                        }

                        if (userType.equalsIgnoreCase("Brand Promoters"))
                        {
                            user_Brand_Selection.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getBrands()));
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_officeEmail.setVisibility(View.VISIBLE);
                        }

                        if(userType.equalsIgnoreCase("Company Professional"))
                        {
                            user_Brand_Selection.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getBrands()));
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_officeEmail.setVisibility(View.VISIBLE);

                        }

                        if(userType.equalsIgnoreCase("Shop Executive"))
                        {
                            user_Shop_Nmber.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getShopnumber()));
                            linear_ShopNumber.setVisibility(View.VISIBLE);
//                            linear_officeEmail.setVisibility(View.VISIBLE);
                        }

                        if(userType.equalsIgnoreCase("Service Centres"))
                        {
                            user_Brand_Selection.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getBrands()));
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);
                        }

                        if(userType.equalsIgnoreCase("Wholesaler"))
                        {
                            // wholesalerradio.setText(mApp.getUtils().toSameCase(userdetails.getCompanyinfo().getVatno()));
//                            linear_officeEmail.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);

                        }

                        if(userType.equalsIgnoreCase("Manufacturers / Brands"))
                        {
                            user_Brand_Selection.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getBrands()));
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_officeEmail.setVisibility(View.VISIBLE);

                        }

                        if(userType.equalsIgnoreCase("Distributor Sales Executive"))
                        {
                            user_Distributor_Nmber.setText(mApp.getUtils().toSameCase(userdetails.getOtherinfo().getDistributorphonenumber()));
//                            linear_officeEmail.setVisibility(View.VISIBLE);
                            linear_DistributorNumber.setVisibility(View.VISIBLE);
                        }

                    }

                    //Initialize AppDukan
                dismissProgress();
            }

            @Override
            public void onFailure(String response) {
                //Do when there is no data present in firebase
                dismissProgress();
            }
        });
    }


    private void updateCompanyVCard() {

        if (user_Business_Card.getText().toString().equalsIgnoreCase(getString(R.string.change))) {
            if (mSelectedBitmap != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                mSelectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                byte[] data = bytes.toByteArray();

                ApiManager.getInstance(CompanyDetails.this).uploadVcard(data, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {

                     Uri vcard = (Uri) data;
                        if (vcard != null) {
                            // user.getCompanyinfo().setCardurl(vcard.toString());

                            updateCompanyDetails(vcard.toString());
                        }

                    }

                    @Override
                    public void onFailure(String response) {
//                        Toast.makeText(CompanyDetails.this, "Uploading Business Card Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
            }
         else {
                String vcard=userdetails.getCompanyinfo().getCardurl();
                updateCompanyDetails(vcard);
//            Toast.makeText(CompanyDetails.this, "Uploading Business Card Unsuccessful", Toast.LENGTH_SHORT).show();

        }
        }
        else{

            updateCompanyDetails("");
        }
    }

    private void updateCompanyDetails(String vCard) {

        ProfileContants contants = new ProfileContants();
        //Company Info
        companyInfo = new HashMap<>();
        companyInfo.put(contants.NAME, user_Company_Name.getText().toString().trim());
        companyInfo.put(contants.VATNO, user_GST_Nmber.getText().toString().trim());
        companyInfo.put(contants.CARDURL, vCard);
        companyInfo.put(contants.OFFICIAL_EMAILID, user_office_Mail.getText().toString().trim());

        otherInfo = new HashMap<>();

        otherInfo.put(contants.ROLE, user_My_Profession.getText().toString().trim());
        otherInfo.put(contants.BRANDS, user_Brand_Selection.getText().toString().trim());
        otherInfo.put(contants.SHOPNUMBER, user_Shop_Nmber.getText().toString().trim());
        otherInfo.put(contants.DISTRIBUTOR_NUMBER, user_Distributor_Nmber.getText().toString().trim());
        otherInfo.put(contants.CREATEDDATE,  userdetails.getOtherinfo().getCreatedDate());

//        String UserId= mApp.getFirebaseAuth().getCurrentUser().getUid();
//        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference companyInfoReference = FirebaseDatabase.getInstance().getReference("users/"+myDhukhan_UserId+"/companyinfo");
        companyInfoReference.setValue(companyInfo);
        DatabaseReference otherInfoReference = FirebaseDatabase.getInstance().getReference("users/"+myDhukhan_UserId+"/otherinfo");
        otherInfoReference.setValue(otherInfo);
        DatabaseReference verified_CompanyInfo = FirebaseDatabase.getInstance().getReference("users/"+myDhukhan_UserId+"/verified_CompanyInfo");
        verified_CompanyInfo.setValue("true");
        Toast.makeText(this, "Done.", Toast.LENGTH_SHORT).show();

        Answers.getInstance().logCustom(new CustomEvent("CompanyDetails_Page")
                .putCustomAttribute("CompanyDetails_Added", myDhukhan_UserId));


    }

    private void galleryIntent() {
        try {
            Intent intent;
            int sdkVersion = Build.VERSION.SDK_INT;

            if (sdkVersion < 19) {
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FILE);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(CompanyDetails.this, "Cannot open the gallery on this device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void cameraIntent() {
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(CompanyDetails.this,
                        "Camera is not present on this device",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(CompanyDetails.this,
                    "Camera is not present on this device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyDetails.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = checkPermission();
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void onCaptureImageResult(Intent data) {
        mSelectedBitmap = null;
        mSelectedBitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mSelectedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mSelectedBitmap != null) {
            user_Business_Card.setText(getString(R.string.change));
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        mSelectedBitmap = null;
        if (data != null) {
            try {
                mSelectedBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                        data.getData());
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        if (mSelectedBitmap != null) {
            user_Business_Card.setText(getString(R.string.change));
        }
    }



    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(CompanyDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) CompanyDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CompanyDetails.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CompanyDetails.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(CompanyDetails.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Intent intent = new Intent(CompanyDetails.this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mApp.getUtils().isStringEmpty(userChoosenTask)) {
                        if (userChoosenTask.equals("Take Photo"))
                            cameraIntent();
                        else if (userChoosenTask.equals("Choose from Library"))
                            galleryIntent();
                    }
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA && data != null)
                onCaptureImageResult(data);

        }
    }

    // Check Validation Method
    private void checkValidation() {
        // Get all edittext texts
        Company_Name = user_Company_Name.getText().toString().trim();
        My_Profession = user_My_Profession.getText().toString().trim();
        Brand_Selection = user_Brand_Selection.getText().toString().trim();
        GST_Number = user_GST_Nmber.getText().toString().trim();
        office_Mail = user_office_Mail.getText().toString().trim();
        Shop_Nmber = user_Shop_Nmber.getText().toString().trim();
        Distributor_Nmber = user_Distributor_Nmber.getText().toString().trim();

        // Check if all strings are null or not
        if (Company_Name.equals("") || Company_Name.length() == 0) {

            new CustomToast().Show_Toast(this, user_Company_Name, "Company Name fields is empty.");
        }

        else if (My_Profession.isEmpty() && My_Profession.equals("")) {
            new CustomToast().Show_Toast(this, user_My_Profession,
                    "Please select your Profession.");
        }


        else if (linear_Brand.getVisibility() == View.VISIBLE && (user_Brand_Selection.getText().toString().isEmpty())) {

                new CustomToast().Show_Toast(this, user_Brand_Selection,
                        "Please select your Brand.");
        }

        /*else if (linear_GST.getVisibility() == View.VISIBLE && (user_GST_Nmber.getText().toString().isEmpty())) {

                new CustomToast().Show_Toast(this, user_GST_Nmber,
                        "Please enter your GST Number.");

        }

        else if (linear_officeEmail.getVisibility() == View.VISIBLE && (user_office_Mail.getText().toString().isEmpty())) {

                new CustomToast().Show_Toast(this, user_office_Mail,
                        "Please enter your Office Email.");
        }

        else if (linear_ShopNumber.getVisibility() == View.VISIBLE  &&  (user_Shop_Nmber.getText().toString().isEmpty())) {

                new CustomToast().Show_Toast(this, user_Shop_Nmber,
                        "Please enter your Shop Number.");
        }
        else if (linear_DistributorNumber.getVisibility() == View.VISIBLE && (user_Distributor_Nmber.getText().toString().isEmpty())) {

                new CustomToast().Show_Toast(this, user_Distributor_Nmber,
                        "Please enter your Distributor Number.");
        }*/

        else {
           /* String UserId= mApp.getFirebaseAuth().getCurrentUser().getUid();
            DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("users/"+UserId+"/userinfo/addressinfo");
            feedReference.setValue(addressInfo);
            DatabaseReference flagVerify_location = FirebaseDatabase.getInstance().getReference("users/"+UserId+"/verified_location");
            flagVerify_location.setValue("true");
            Toast.makeText(this, "Done.", Toast.LENGTH_SHORT).show();*/
//            Intent i = new Intent(UsersLocationAddress.this, SignupActivity.class);
            updateCompanyVCard();


            Intent i = new Intent(CompanyDetails.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            startActivity(i);

        }

    }

    protected void onChangeSelected_Brands() {
        StringBuilder stringBuilder = new StringBuilder();

        int l = selectedValues.size();
        for(CharSequence receivers : selectedValues)

            if(selectedValues.size()==1)
            {
                stringBuilder.append(receivers);
                user_Brand_Selection.setText(stringBuilder.toString());
            }
            else if(selectedValues.size()>=1){

                stringBuilder.append(receivers + ",");
                StringBuffer sb = new StringBuffer(stringBuilder.toString());
                sb.replace(stringBuilder.toString().lastIndexOf(","), stringBuilder.toString().lastIndexOf(",") + 1, "");
                user_Brand_Selection.setText(sb);

            }

        /*
         if(selectedReceivers.size()<2){
            selectedValue.setText(selectedReceivers.get(0));
        }else{
            for(CharSequence receivers : selectedReceivers)
                stringBuilder.append(receivers + ", ");
            selectedValue.setText(stringBuilder.toString());
        }
        */
    }

    protected void showMultiSelectedBrandsDialog() {
        boolean[] checkedReceivers = new boolean[BrandsList.length];
        int count = BrandsList.length;

        for(int i = 0; i < count; i++)
            checkedReceivers[i] = selectedValues.contains(BrandsList[i]);

        DialogInterface.OnMultiChoiceClickListener receiversDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked)
                    selectedValues.add(BrandsList[which]);
                else
                    selectedValues.remove(BrandsList[which]);

                onChangeSelected_Brands();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Select Your Brands")
                .setMultiChoiceItems(BrandsList, checkedReceivers, receiversDialogListener)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        user_Brand_Selection.setTextSize(18);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showSelectMyProfessionalDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyDetails.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Select Your Profession");
        builder.setSingleChoiceItems(ProfessionList, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        /*Toast.makeText(getApplicationContext(), brandpromotersList[item],
                                Toast.LENGTH_SHORT).show();*/
                        user_My_Profession.setText(ProfessionList[item]);
                        if(ProfessionList[item].equals("Retailer"))
                        {
//                            linear_officeEmail.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);
                        }

                        else if(ProfessionList[item].equals("Distributor"))
                        {

                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);
                        }

                        else if(ProfessionList[item].equals("Brand Promoters"))
                        {
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_officeEmail.setVisibility(View.VISIBLE);
                        }

                        else if(ProfessionList[item].equals("Company Professional"))
                        {
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_officeEmail.setVisibility(View.VISIBLE);
                        }

                        else if(ProfessionList[item].equals("Shop Executive"))
                        {
                            linear_ShopNumber.setVisibility(View.VISIBLE);
//                            linear_officeEmail.setVisibility(View.VISIBLE);

                        }

                        else if(ProfessionList[item].equals("Service Centres"))
                        {
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);

                        }

                        else if(ProfessionList[item].equals("Wholesaler"))
                        {

//                            linear_officeEmail.setVisibility(View.VISIBLE);
                            linear_GST.setVisibility(View.VISIBLE);
                        }

                        else if(ProfessionList[item].equals("Manufacturers / Brands"))
                        {
                            linear_Brand.setVisibility(View.VISIBLE);
                            linear_officeEmail.setVisibility(View.VISIBLE);
                        }
                        else if(ProfessionList[item].equals("Distributor Sales Executive"))
                        {
//                            linear_officeEmail.setVisibility(View.VISIBLE);
                            linear_DistributorNumber.setVisibility(View.VISIBLE);
                        }
                        user_Brand_Selection.setText("");
                    }
                });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

//                Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void showSingleSelectedBrandsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyDetails.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Select any One Brand");
        builder.setSingleChoiceItems(BrandsList, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        /*Toast.makeText(getApplicationContext(), brandpromotersList[item],
                                Toast.LENGTH_SHORT).show();*/
                        user_Brand_Selection.setText(BrandsList[item]);
                    }
                });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

//                Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_button_2:
                Intent back = new Intent(CompanyDetails.this, UserProfile.class);
                back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                startActivity(back);

                break;

            case R.id.user_Profile_Submit:
                checkValidation();

                break;

            case R.id.user_Brand_Selection:
                My_Profession = user_My_Profession.getText().toString().trim();


                if((My_Profession.equalsIgnoreCase("Brand Promoters")) || (My_Profession.equalsIgnoreCase("Company Professional")))
                {
                    showSingleSelectedBrandsDialog();

                }

                else if((user_My_Profession.equals("Brand Promoters")) || (user_My_Profession.equals("Company Professional")))
                {
                    showSingleSelectedBrandsDialog();

                }

                else if ((My_Profession.equalsIgnoreCase("Distributor")) || (My_Profession.equalsIgnoreCase("Service Centres")) || (My_Profession.equalsIgnoreCase("Manufacturers / Brands")))
                {

                    showMultiSelectedBrandsDialog();

                }

                else if ((user_My_Profession.equals("Distributor")) || (user_My_Profession.equals("Service Centres")) || (user_My_Profession.equals("Manufacturers / Brands")))
                {

                    showMultiSelectedBrandsDialog();

                }

                else
                {
                    Toast.makeText(CompanyDetails.this, "You have not selected your Brand", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.user_My_Profession:
                showSelectMyProfessionalDialog();
//                linear_Brand, linear_GST, linear_officeEmail, linear_ShopNumber, linear_DistributorNumber;
                My_Profession = user_My_Profession.getText().toString().trim();

                linear_Brand.setVisibility(View.GONE);
                linear_officeEmail.setVisibility(View.GONE);
                linear_GST.setVisibility(View.GONE);
                linear_ShopNumber.setVisibility(View.GONE);
                linear_DistributorNumber.setVisibility(View.GONE);

                /*if(My_Profession.equalsIgnoreCase("Retailer"))
                {
                    linear_officeEmail.setVisibility(View.VISIBLE);
                    linear_GST.setVisibility(View.VISIBLE);
                }

                else if(My_Profession.equalsIgnoreCase("Distributor"))
                {

                    linear_Brand.setVisibility(View.VISIBLE);
                    linear_GST.setVisibility(View.VISIBLE);
                }

                else if(My_Profession.equalsIgnoreCase("Brand Promoters"))
                {
                    linear_Brand.setVisibility(View.VISIBLE);
                    linear_officeEmail.setVisibility(View.VISIBLE);
                }

                else if(My_Profession.equalsIgnoreCase("Company Professional"))
                {
                    linear_Brand.setVisibility(View.VISIBLE);
                    linear_officeEmail.setVisibility(View.VISIBLE);
                }

                else if(My_Profession.equalsIgnoreCase("Shop Executive"))
                {
                    linear_ShopNumber.setVisibility(View.VISIBLE);
                    linear_officeEmail.setVisibility(View.VISIBLE);

                }

                else if(My_Profession.equalsIgnoreCase("Service Centres"))
                {
                    linear_Brand.setVisibility(View.VISIBLE);
                    linear_GST.setVisibility(View.VISIBLE);

                }

                else if(My_Profession.equalsIgnoreCase("Wholesaler"))
                {

                    linear_officeEmail.setVisibility(View.VISIBLE);
                    linear_GST.setVisibility(View.VISIBLE);
                }

                else if(My_Profession.equalsIgnoreCase("Manufacturers / Brands"))
                {
                    linear_Brand.setVisibility(View.VISIBLE);
                    linear_officeEmail.setVisibility(View.VISIBLE);
                }
                else if(My_Profession.equalsIgnoreCase("Distributor Sales Executive"))
                {
                    linear_officeEmail.setVisibility(View.VISIBLE);
                    linear_DistributorNumber.setVisibility(View.VISIBLE);


                }
                  else
                {
                    Toast.makeText(CompanyDetails.this, "You have not selected your Profession", Toast.LENGTH_SHORT).show();
                }
                */

                
                break;

        }

    }
}
