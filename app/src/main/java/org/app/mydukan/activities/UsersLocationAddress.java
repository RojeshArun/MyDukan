package org.app.mydukan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.ProfileContants;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsersLocationAddress extends BaseActivity implements View.OnClickListener, LocationResult {

    private static final String DEFAULT = "User";
    final static int REQUEST_LOCATION = 199;
    public static final int REQUEST_LOCATION_CODE = 99;

    private TextView userName, privacyPolicyView, termsandconditionView;
    private MyDukan mApp;
    private EditText fullAddress, userArea, userCity, userPincode, userState;
    private Button signUpBtn, BtnChangeLocation;
    private CheckBox terms_conditions;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;
    private static final String TAG = "PlacePickerActivity";
    private int mViewType = 1; //1 - Sign up , 2 - My Profile.

    HashMap<String, Object> addressInfo;

    private MyLocation myLocation = null;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int INITIAL_REQUEST = 13;

    String Full_Address; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
    String House_No;
    String Area_Name;
    String City_Name;
    String PostalCode;
    String State_Name;
    String Country_Name;
    String Latitude;
    String Longitude;
    String LatLng;

    String firstname;

    private String viewType = "";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_location_address);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppContants.VIEW_TYPE)) {
            viewType = bundle.getString(AppContants.VIEW_TYPE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        mApp = (MyDukan) getApplicationContext();
        myLocation = new MyLocation();


        fullAddress = (EditText) findViewById(R.id.fullAddress);
        userArea = (EditText) findViewById(R.id.userArea);
        userCity = (EditText) findViewById(R.id.userCity);
        userPincode = (EditText) findViewById(R.id.userPincode);
        userState = (EditText) findViewById(R.id.userState);

        privacyPolicyView = (TextView) findViewById(R.id.privacyPolicyView);
        termsandconditionView = (TextView) findViewById(R.id.termsandconditionView);

        privacyPolicyView = (TextView) findViewById(R.id.privacyPolicyView);
        privacyPolicyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacypolicy = new Intent(UsersLocationAddress.this, PrivacyPolicyActivity.class);
                startActivity(privacypolicy);
            }
        });

        termsandconditionView = (TextView) findViewById(R.id.termsandconditionView);
        termsandconditionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsandcondition = new Intent(UsersLocationAddress.this, PrivacyPolicyActivity.class);
                startActivity(termsandcondition);
            }
        });

        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        BtnChangeLocation = (Button) findViewById(R.id.BtnChangeLocation);

        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);

        userName = (TextView) findViewById(R.id.userName);

        signUpBtn.setOnClickListener(this);
        BtnChangeLocation.setOnClickListener(this);


//        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        //        this.setFinishOnTouchOutside(true);
        // Todo Location Already on  ... start
        LocationManager manager = (LocationManager) UsersLocationAddress.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(UsersLocationAddress.this)) {
            boolean networkPresent = myLocation.getLocation(UsersLocationAddress.this, UsersLocationAddress.this);
            if (!networkPresent) {
                // ActivityCompat.requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                checkLocationPermission();
            }

            if (!canAccessLocation() || !canAccessCoreLocation()) {
                checkLocationPermission();

            }
            mClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            showProgress();
//            Toast.makeText(UsersLocationAddress.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(UsersLocationAddress.this)) {
            Toast.makeText(UsersLocationAddress.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (manager != null) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(UsersLocationAddress.this)) {
                Toast.makeText(UsersLocationAddress.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
                dismissProgress();
                enableLoc();
            } else {
                showProgress();
//                Toast.makeText(UsersLocationAddress.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
                //requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }

        SharedPreferences sharedPreferences2 = getSharedPreferences("firstname", Context.MODE_PRIVATE);
        firstname = sharedPreferences2.getString("firstname", DEFAULT);
        userName.setText(firstname);

       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }*/


       /* btnGPSShowLocation = (Button) findViewById(R.id.btnGPSShowLocation);
        btnGPSShowLocation.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View arg0) {

                if (!canAccessLocation() || !canAccessCoreLocation()) {
                    requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);

                } else {
                    boolean networkPresent = myLocation.getLocation(MainActivity.this, MainActivity.this);
                    if (!networkPresent) {
                        showSettingsAlert();
                    }
                }

            }
        });

        btnShowAddress = (Button) findViewById(R.id.btnShowAddress);
        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View arg0) {

                //you can hard-code the lat & long if you have issues with getting it
                //remove the below if-condition and use the following couple of lines
                //double latitude = 37.422005;
                //double longitude = -122.084095
                if (!canAccessLocation() || !canAccessCoreLocation()) {
                    requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                } else {
                    boolean networkPresent = myLocation.getLocation(MainActivity.this, MainActivity.this);
                    if (!networkPresent) {
                        Toast.makeText(MainActivity.this, "please enable your network", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });*/
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }

        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        //

        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(UsersLocationAddress.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onConnected(Bundle bundle) {

                            if (!canAccessLocation() || !canAccessCoreLocation()) {
                                checkLocationPermission();
                            }
                            checkLocationPermission();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mClient.connect();
                            Toast.makeText(UsersLocationAddress.this, "please enable your GPS1 and Try again", Toast.LENGTH_SHORT).show();
                            dismissProgress();
                            Log.d("Location error", "Location connection suspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Toast.makeText(UsersLocationAddress.this, "please enable your GPS2 and Try again", Toast.LENGTH_SHORT).show();
                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                            dismissProgress();
                        }
                    })
                    .build();
            mClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);
            showProgress();

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
//                                Toast.makeText(UsersLocationAddress.this, "Done.", Toast.LENGTH_SHORT).show();
//                                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                                status.startResolutionForResult(UsersLocationAddress.this, REQUEST_LOCATION);

//                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.BtnChangeLocation:

                boolean networkPresent = myLocation.getLocation(UsersLocationAddress.this, UsersLocationAddress.this);
                if (!networkPresent) {
                    Toast.makeText(this, "Please turn on the GPS and try again.", Toast.LENGTH_SHORT).show();
                    showSettingsAlert();
                } else if (!canAccessLocation() || !canAccessCoreLocation()) {
                    // TODO: 27-10-2017 add progress bar
//                    requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(UsersLocationAddress.this), PLACE_PICKER_REQUEST);
                        Answers.getInstance().logCustom(new CustomEvent("UserLocation_Page")
                                .putCustomAttribute("Button_location_clicked", "true"));
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(UsersLocationAddress.this), PLACE_PICKER_REQUEST);
                        Answers.getInstance().logCustom(new CustomEvent("UserLocation_Page")
                                .putCustomAttribute("Button_location_clicked", "true"));
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }

                /*write your code Here*/

                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (addressInfo==null) {
                Toast.makeText(this, "Please turn on the GPS and try again.", Toast.LENGTH_SHORT).show();

            }else{
                if (!addressInfo.isEmpty() && addressInfo!=null) {
                    addressInfo.clear();
                }
            }
            if (resultCode == RESULT_OK) {


                Place place = PlacePicker.getPlace(data, this);
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;

                Latitude = String.valueOf(latitude);
                Longitude = String.valueOf(longitude);

                if(Longitude.isEmpty() && Latitude.isEmpty())
                {
                    Toast.makeText(this, "Wait till the GPS turn On", Toast.LENGTH_SHORT).show();
                    return;
                }

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList==null || addressList.size() == 0) {
                        Toast.makeText(this, "Please turn on the GPS and try again.", Toast.LENGTH_SHORT).show();
                        showSettingsAlert();
                        return;
                    }

                    Full_Address = addressList.get(0).getAddressLine(0);
                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    House_No = addressList.get(0).getFeatureName();
                    Area_Name = addressList.get(0).getSubLocality();
                    City_Name = addressList.get(0).getLocality();
                    PostalCode = addressList.get(0).getPostalCode();
                    State_Name = addressList.get(0).getAdminArea();
                    Country_Name = addressList.get(0).getCountryName();

                    fullAddress.setText(Full_Address);
                    userArea.setText(Area_Name);
                    userCity.setText(City_Name);
                    userPincode.setText(PostalCode);
//                    userState.setText(State_Name);
                    if (State_Name != null && !State_Name.isEmpty()) {
                        State_Name = State_Name.toLowerCase();
                    }
                    userState.setText(State_Name);

                    ProfileContants contants = new ProfileContants();

                    //AddressInfo Info
                    addressInfo.put(contants.STREET, Full_Address);
                    addressInfo.put(contants.CITY, City_Name);
                    addressInfo.put(contants.STATE, State_Name);
                    addressInfo.put(contants.COUNTRY, "india");
                    addressInfo.put(contants.PIN_CODE, PostalCode);


                    if (!(LatLng == null)) {
                        addressInfo.put(contants.LOCATION_VERIFIED, false);
                        addressInfo.put(contants.LAT_LNG, LatLng);
                        addressInfo.put(contants.LATITUDE, Latitude);
                        addressInfo.put(contants.LONGITUDE, Longitude);
                    } else {
                        addressInfo.put(contants.LOCATION_VERIFIED, false);
                        addressInfo.put(contants.LAT_LNG, "");
                        addressInfo.put(contants.LATITUDE, "");
                        addressInfo.put(contants.LONGITUDE, "");
                    }
                    result = latitude + "\n" + longitude + "\n" + Full_Address + "\n" + House_No + "\n" + Area_Name + "\n" + City_Name + "\n" + PostalCode + "\n" + State_Name + "\n" + Country_Name;

                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                }
               /* Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);
                placeDescription.setText(stBuilder.toString());*/
            }
        }
    }


    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        String getfullAddress = fullAddress.getText().toString();
        String getuserArea = userArea.getText().toString();
        String getuserCity = userCity.getText().toString();
        String getuserPincode = userPincode.getText().toString();
        String getuserState = userState.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getfullAddress);

        // Check if all strings are null or not
        if (getfullAddress.equals("") || getfullAddress.length() == 0
                /*|| getuserArea.equals("") || getuserArea.length() == 0*/
                || getuserCity.equals("") || getuserCity.length() == 0
                || getuserPincode.equals("") || getuserPincode.length() == 0
                || getuserState.equals("") || getuserState.length() == 0) {

            new CustomToast().Show_Toast(this, fullAddress, "Fields are Empty, Click Edit Location Button and choose your Location.");
        }

        // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked()) {
            new CustomToast().Show_Toast(this, terms_conditions,
                    "Please select Terms and Conditions.");
        }

        // Else do signup or do your stuff

        // Else do signup or do your stuff
        else {
            if (mViewType == AppContants.SIGN_UP) {
                mApp.getPreference().setAppState(UsersLocationAddress.this, new AppStateContants().HOME_SCREEN);
            }

            String UserId = mApp.getFirebaseAuth().getCurrentUser().getUid();

            DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("users/" + UserId + "/userinfo/addressinfo");
            feedReference.setValue(addressInfo);
            DatabaseReference flagVerify_location = FirebaseDatabase.getInstance().getReference("users/" + UserId + "/verified_location");
            flagVerify_location.setValue("true");
            Toast.makeText(this, "Done.", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(UsersLocationAddress.this, SignupActivity.class);
            if (viewType != null) {
                if (viewType.equalsIgnoreCase("user_profile")) {
                                       /* Intent i = new Intent(getContext(), UserProfile.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);*/
                    onBackPressed();
                }
            } else {
                Intent i = new Intent(UsersLocationAddress.this, MainActivity.class);
                i.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

                Answers.getInstance().logCustom(new CustomEvent("UserLocation_Page")
                        .putCustomAttribute("User_location_verified", UserId));
            }

        }

    }

    @Override
    public void onBackPressed() {

           Intent i = new Intent(getApplicationContext(), UserProfile.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);

     /*   // Find the tag of signup and forgot password fragment
        Fragment DemoPage_Fragment = fragmentManager.findFragmentByTag(Utils.DemoPage_Fragment);
        Fragment GmailVerificationFragment = fragmentManager.findFragmentByTag(Utils.SignUp_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if ( GmailVerificationFragment!= null)
            replaceMobileVerificationFragment();
        else if (DemoPage_Fragment != null)
            replaceMobileVerificationFragment();
        else*/
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case INITIAL_REQUEST:
                if (canAccessLocation() && canAccessCoreLocation()) {
                    boolean networkPresent = myLocation.getLocation(UsersLocationAddress.this, this);
                    if (!networkPresent) {
                        showSettingsAlert();
                    }
                }

                break;
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permissin granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mClient != null) {
                            showProgress();
                            mClient = new GoogleApiClient
                                    .Builder(this)
                                    .addApi(Places.GEO_DATA_API)
                                    .addApi(Places.PLACE_DETECTION_API)
                                    .build();
                            if (canAccessLocation() && canAccessCoreLocation()) {
                                boolean networkPresent = myLocation.getLocation(UsersLocationAddress.this, this);
                                if (!networkPresent) {
                                    showSettingsAlert();
                                }
                            }
                        }

                    }
                } else { //Permission is denaided
                    dismissProgress();
                    Toast.makeText(this, "Permission is Denaied !", Toast.LENGTH_SHORT).show();
                }
                return;


        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCoreLocation() {
        return (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean hasPermission(String perm) {

        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(UsersLocationAddress.this, perm));
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                UsersLocationAddress.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Please Enable your GPS, your GPS is NOT Enabled");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Intent i = new Intent(UsersLocationAddress.this, UsersLocationAddress.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        dialog.dismiss();

//                        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
//                        showProgress();
                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    @Override
    public void gotLocation(Location location) {

        if (location != null) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            final String result = "Latitude: " + location.getLatitude() +
                    " Longitude: " + location.getLongitude();

            UsersLocationAddress.this.runOnUiThread(new Runnable() {
                public void run() {
//                tvAddress.setText(result);
                    LocationAddress locationAddress = new LocationAddress();
                    LocationAddress.getAddressFromLocation(latitude, longitude,
                            UsersLocationAddress.this, new GeocoderHandler());
                }
            });
        } else {
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(UsersLocationAddress.this, "Please turn on the GPS and Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            dismissProgress();
            // TODO: 27-10-2017 add progress bar stop
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    Latitude = bundle.getString("latitude");
                    Longitude = bundle.getString("longitude");
                    Full_Address = bundle.getString("fulladdress");
                    Area_Name = bundle.getString("area");
                    City_Name = bundle.getString("city");
                    PostalCode = bundle.getString("pincode");
                    State_Name = bundle.getString("state");

                    if (State_Name != null && !State_Name.isEmpty()) {
                        State_Name = State_Name.toLowerCase();
                    }

                    LatLng = "lat/lng:(" + Latitude + "," + Longitude + ")";


                    ProfileContants contants = new ProfileContants();

                    //AddressInfo Info
                    addressInfo = new HashMap<>();
                    addressInfo.put(contants.STREET, Full_Address);
                    addressInfo.put(contants.CITY, City_Name);
                    addressInfo.put(contants.STATE, State_Name);
                    addressInfo.put(contants.COUNTRY, "india");
                    addressInfo.put(contants.PIN_CODE, PostalCode);


                    if (!(LatLng == null)) {
                        addressInfo.put(contants.LOCATION_VERIFIED, false);
                        addressInfo.put(contants.LAT_LNG, LatLng);
                        addressInfo.put(contants.LATITUDE, Latitude);
                        addressInfo.put(contants.LONGITUDE, Longitude);
                    } else {
                        addressInfo.put(contants.LOCATION_VERIFIED, false);
                        addressInfo.put(contants.LAT_LNG, "");
                        addressInfo.put(contants.LATITUDE, "");
                        addressInfo.put(contants.LONGITUDE, "");
                    }
                    break;
                default:
                    fullAddress = null;
            }
            fullAddress.setText(Full_Address);
            userArea.setText(Area_Name);
            userCity.setText(City_Name);
            userPincode.setText(PostalCode);
            if (State_Name != null && !State_Name.isEmpty()) {
                State_Name = State_Name.toLowerCase();
            }
            userState.setText(State_Name);
        }
    }
}
