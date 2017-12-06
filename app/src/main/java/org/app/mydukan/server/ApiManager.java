package org.app.mydukan.server;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Category;
import org.app.mydukan.data.Complaint;
import org.app.mydukan.data.DoaConstants;
import org.app.mydukan.data.DoaRecord;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.HelpVideos;
import org.app.mydukan.data.Notification;
import org.app.mydukan.data.Offer;
import org.app.mydukan.data.Order;
import org.app.mydukan.data.OrderProduct;
import org.app.mydukan.data.PriceDrop;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.ProfileContants;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeConstants;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.data.SubCategory;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.data.SupplierGroups;
import org.app.mydukan.data.User;
import org.app.mydukan.utils.AppContants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.app.mydukan.fragments.OneFragment.FEED_ROOT;

/**
 * Created by Codespeak on 05-07-2016.
 */
public class ApiManager {

    private static ApiManager mApiManger;
    private static Context mctx;
    public final String RETAILER_TYPE = "RETAILER_TYPE";
    public final String SUPPLIER_TYPE = "SUPPLIER_TYPE";
    long supplierCount = 0;
    long grpProductCount = 0;
    long subCategoryCount = 0;
    long grpSchemeCount = 0;
    long grpNotificationCount = 0;
    long productCount;
    //private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private MyDukan mApp;

    private ApiManager(Context context) {
        mctx = context;
        mApp = (MyDukan) mctx.getApplicationContext();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // firebase storage URL to be mentioned below
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydukan-1024.appspot.com");
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (mApiManger == null) {
            mApiManger = new ApiManager(context);
        }
        return mApiManger;
    }

    public void updateUserProfile(final String uid, HashMap<String, Object> userInfo, HashMap<String, Object> companyInfo, HashMap<String, Object> otherInfo, String userType, final ApiResult status) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + uid + "/userinfo/", userInfo);
        childUpdates.put("/users/" + uid + "/credential/username", userInfo.get("emailid"));
        childUpdates.put("/users/" + uid + "/companyinfo/", companyInfo);
        childUpdates.put("/users/" + uid + "/otherinfo/", otherInfo);
        childUpdates.put("/users/" + uid + "/usertype/", "retailer");

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            status.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            status.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }
    //======================================================================================================

    public void updateUserSubscription(final String uid, HashMap<String, Object> appSubscriptionInfo, final ApiResult status) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + uid + "/appSubscriptionInfo/", appSubscriptionInfo);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            status.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            status.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }
/*
    public void updateServiceCenterList(final String uid, ArrayList<ServiceCenterInfo> serviceCenterInfos, final ApiResult status) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/servicecenters", serviceCenterInfos);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            status.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            status.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }

*/

    public void appsubcription(final String uid, HashMap<String, Object> appSubscriptionInfo, final ApiResult result) {
        final String subscription_ORDERID = String.valueOf(appSubscriptionInfo.get(AppContants.SUBSCRIPTION_ORDERID));

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/appsubcription/" + uid + "/" + subscription_ORDERID + "/", appSubscriptionInfo);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }


    public void validateAppSubscriptionInfo(String userId, String status, final ApiResult result) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userId + "/appSubscriptionInfo/subscription_ISVALID/", status);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });

    }

    public void setQRCodeURL(String userId, String qrCodeURL, final ApiResult result) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userId + "/qrcodedurl/", qrCodeURL);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }

    public void uploadqrcode(byte[] rqcodeData, final ApiResult status) {
        String name = "qrcode/" + mApp.getFirebaseAuth().getCurrentUser().getUid() + ".jpg";
        StorageReference vcardRef = mStorage.child(name);
        UploadTask uploadVcardTask = vcardRef.putBytes(rqcodeData);
        uploadVcardTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                status.onSuccess(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                status.onSuccess(downloadUrl);
            }
        });
    }

    //======================================================================================================

    public void uploadVcard(byte[] vcardData, final ApiResult status) {
        String name = "Cards/" + mApp.getFirebaseAuth().getCurrentUser().getUid() + ".jpg";
        StorageReference vcardRef = mStorage.child(name);
        UploadTask uploadVcardTask = vcardRef.putBytes(vcardData);
        uploadVcardTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                status.onSuccess(null);
                // Vcard_Upload Approach MoEngage
                PayloadBuilder builder = new PayloadBuilder();
                builder.putAttrString("Vcard upload Failure", "Failure");
                MoEHelper.getInstance(mctx).trackEvent("UploadVcard", builder.build());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                status.onSuccess(downloadUrl);
                // Vcard_Upload Approach MoEngage
                PayloadBuilder builder = new PayloadBuilder();
                builder.putAttrString("Vcard upload Success", "Success")
                        .putAttrString("Vcard DownloadUrl", String.valueOf(downloadUrl))
                        .putAttrDate("Vcard uploaded Date", new Date());
                MoEHelper.getInstance(mctx).trackEvent("UploadVcard Success", builder.build());
            }
        });
    }

    public void checkAndSignUpUser(final String uid, final String emailId, final ApiResult status) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users/" + uid);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("usertype").getValue(String.class);
                    if (userType.equalsIgnoreCase("retailer")) {
                        Answers.getInstance().logLogin(new LoginEvent()
                                .putMethod("Digits")
                                .putSuccess(true));
                        status.onSuccess(mctx.getString(R.string.status_success));
                        //mOBILE NUMBER VERIFICATION Approach MoEngage
                        PayloadBuilder builder = new PayloadBuilder();
                        builder.putAttrString("MobileNumber Verification", mctx.getString(R.string.status_success))
                                .putAttrDate("Verified Date", new Date());
                        MoEHelper.getInstance(mctx).trackEvent("Digits", builder.build());
                    } else {
                        status.onFailure(mctx.getString(R.string.error_usertype));
                        //1st Approach MoEngage
                        PayloadBuilder builder = new PayloadBuilder();
                        builder.putAttrString("MobileNumber Verification", mctx.getString(R.string.status_failed))
                                .putAttrString("User EmailId", emailId)
                                .putAttrDate("Verified Date", new Date());
                        MoEHelper.getInstance(mctx).trackEvent("Digits", builder.build());
                    }
                } else {
                    // It is a new user. Need to create an entry.
                    signUpUser(uid, emailId, status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                status.onSuccess(mctx.getString(R.string.error_loginfailed));
            }
        });
    }

    public void signUpUser(final String uid, String emailId, final ApiResult status) {

        //credential
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(new ProfileContants().USERNAME, emailId);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + uid + "/emailid/", emailId);
        childUpdates.put("/users/" + uid + "/credential/", userInfo);
        childUpdates.put("/users/" + uid + "/userinfo/emailid", emailId);
        childUpdates.put("/users/" + uid + "/otherinfo/createddate", System.currentTimeMillis());
        childUpdates.put("/users/" + uid + "/usertype/", "retailer");

        //1st Approach MoEngage
        PayloadBuilder builder = new PayloadBuilder();
        builder.putAttrString("New SignIn User", emailId)
                .putAttrDate("New SignIn Date", new Date());
        MoEHelper.getInstance(mctx).trackEvent("New Users", builder.build());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            status.onSuccess(mctx.getString(R.string.status_success));

                        } else {
                            status.onSuccess(mctx.getString(R.string.error_loginfailed));
                        }
                    }
                });
    }

    public void getUserProfile(final String userId, final ApiResult result) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + userId);
        //addListenerForSingleValueEvent
        userRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        result.onSuccess(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onFailure(databaseError.getMessage());
                    }
                });
    }


    public void getHelpVideos(final String userId, final ApiResult result) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("videos/");
        userRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        HelpVideos userHelpVideo = dataSnapshot.getValue(HelpVideos.class);
                        result.onSuccess(userHelpVideo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onFailure(databaseError.getMessage());
                    }
                });
    }

    public void checkUserProfle(final String userId, final ApiResult result) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getUserinfo() != null && user.getOtherinfo() != null && user.getCompanyinfo() != null) {
                        result.onSuccess("success");
                    } else {
                        result.onFailure("failure");
                    }
                } else {
                    result.onFailure("failure");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure("failure");
            }
        });
    }

    public void getStateList(final ApiResult result) {
        DatabaseReference countryRef = FirebaseDatabase.getInstance().getReference("countrycodes/india/");
     /*   countryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> statesList = new ArrayList<String>();
                if(dataSnapshot.exists()){
                    HashMap<String, String> stateMap = (HashMap<String, String>)dataSnapshot.getValue();
                    statesList.addAll(new ArrayList<String>(stateMap.keySet()));
                }
                result.onSuccess(statesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(null);
            }
        });*/
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> statesList = new ArrayList<String>();
                if (dataSnapshot.exists()) {
                    HashMap<String, String> stateMap = (HashMap<String, String>) dataSnapshot.getValue();
                    statesList.addAll(new ArrayList<String>(stateMap.keySet()));
                }
                result.onSuccess(statesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(null);
            }
        });
    }

    public void getSupplierlist(final String userId, final ApiResult result) {
        DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions/" + userId);
        //  subscriptionRef.keepSynced(true);
      /*  subscriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> usersSupplierIds = new ArrayList<String>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        usersSupplierIds.add(supplierSnapshot.getKey());
                    }
                }

                DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users");
                supplierRef.orderByChild("usertype").equalTo("supplier").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userDetailsSnapshot : dataSnapshot.getChildren()) {
                                if (!usersSupplierIds.contains(userDetailsSnapshot.getKey())) {
                                    try {
                                        Supplier supplier = userDetailsSnapshot.getValue(Supplier.class);
                                        if (!supplier.getOtherinfo().getByInviteCode()) {
                                            if (supplier.getOtherinfo().getStatus().equalsIgnoreCase("enabled")) {
                                                supplier.setId(userDetailsSnapshot.getKey());
                                                supplierList.add(supplier);
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                        result.onSuccess(supplierList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onFailure(databaseError.getMessage());
                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });*/

         /*Logic change in fetch supplier code*/


        subscriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> usersSupplierIds = new ArrayList<String>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        usersSupplierIds.add(supplierSnapshot.getKey());
                    }
                }

                DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users");
                supplierRef.orderByChild("usertype").equalTo("supplier").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userDetailsSnapshot : dataSnapshot.getChildren()) {
                                if (!usersSupplierIds.contains(userDetailsSnapshot.getKey())) {
                                    try {
                                        Supplier supplier = userDetailsSnapshot.getValue(Supplier.class);
                                        if (!supplier.getOtherinfo().getByInviteCode()) {
                                            if (supplier.getOtherinfo().getStatus().equalsIgnoreCase("enabled")) {
                                                supplier.setId(userDetailsSnapshot.getKey());
                                                supplierList.add(supplier);
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                        result.onSuccess(supplierList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onFailure(databaseError.getMessage());
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });

        /*Change in logic end*/
    }

    public void getSupplierlistbyinvitecode(final String userId, final String invitecode, final ApiResult result) {
        DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions/" + userId);
        // subscriptionRef.keepSynced(true);
        /*subscriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> usersSupplierIds = new ArrayList<String>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        usersSupplierIds.add(supplierSnapshot.getKey());
                    }
                }
                DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users");
                supplierRef.orderByChild("usertype").equalTo("supplier").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userDetailsSnapshot : dataSnapshot.getChildren()) {
                                if (!usersSupplierIds.contains(userDetailsSnapshot.getKey())) {
                                    try {
                                        Supplier supplier = userDetailsSnapshot.getValue(Supplier.class);
                                        if(supplier.getOtherinfo().getByInviteCode()) {
                                            if(supplier.getOtherinfo().getInviteCode().equalsIgnoreCase(invitecode)) {
                                                if (supplier.getOtherinfo().getStatus().equalsIgnoreCase("enabled")) {
                                                    supplier.setId(userDetailsSnapshot.getKey());
                                                    supplierList.add(supplier);
                                                    addSupplier(userId, userDetailsSnapshot.getKey(), new ApiResult() {
                                                        @Override
                                                        public void onSuccess(Object data) {

                                                        }

                                                        @Override
                                                        public void onFailure(String response) {

                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                       result.onSuccess(supplierList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onFailure(databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });*/


        /*Change in logic of fetch supplier list by invite code*/


        subscriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> usersSupplierIds = new ArrayList<String>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        usersSupplierIds.add(supplierSnapshot.getKey());
                    }
                }
                DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users");
                supplierRef.orderByChild("usertype").equalTo("supplier").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Supplier> supplierList = new ArrayList<Supplier>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userDetailsSnapshot : dataSnapshot.getChildren()) {
                                if (!usersSupplierIds.contains(userDetailsSnapshot.getKey())) {
                                    try {
                                        Supplier supplier = userDetailsSnapshot.getValue(Supplier.class);
                                        if (supplier.getOtherinfo().getByInviteCode()) {
                                            if (supplier.getOtherinfo().getInviteCode().equalsIgnoreCase(invitecode)) {
                                                if (supplier.getOtherinfo().getStatus().equalsIgnoreCase("enabled")) {
                                                    supplier.setId(userDetailsSnapshot.getKey());
                                                    supplierList.add(supplier);
                                                    addSupplier(userId, userDetailsSnapshot.getKey(), new ApiResult() {
                                                        @Override
                                                        public void onSuccess(Object data) {

                                                        }

                                                        @Override
                                                        public void onFailure(String response) {

                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                        result.onSuccess(supplierList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onFailure(databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }

             /*Change in logic of fetch supplier list by invite code*/
        });
    }

    public void updateMembersSubscriptiontable(final String userId) {
        DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions/" + userId);
       /* subscriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, DataSnapshot> usersSupplierIds = new HashMap<String, DataSnapshot>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        usersSupplierIds.put(supplierSnapshot.getKey(), supplierSnapshot);
                    }
                    for (final String supplierId: usersSupplierIds.keySet()) {
                        DatabaseReference membersRef =  FirebaseDatabase.getInstance().getReference("members/"+supplierId+"/"+userId);
                        membersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                } else {
                                    Map<String, Object> supplierData = new HashMap<>();

                                    Map<String, Object> childUpdates = new HashMap<>();

                                    supplierData.put("status",usersSupplierIds.get(supplierId).child("status").getValue(String.class));
                                    supplierData.put("timestamp",usersSupplierIds.get(supplierId).child("timestamp").getValue(Long.class));

                                    childUpdates.put("/members/" + supplierId + "/" + userId + "/", supplierData);


                                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                                            new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                // result.onSuccess(mctx.getString(R.string.status_success));
                                            }
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        /*Logic Update member subscriptionable list*/

        subscriptionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, DataSnapshot> usersSupplierIds = new HashMap<String, DataSnapshot>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        usersSupplierIds.put(supplierSnapshot.getKey(), supplierSnapshot);
                    }
                    for (final String supplierId : usersSupplierIds.keySet()) {
                        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference("members/" + supplierId + "/" + userId);
                        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                } else {
                                    Map<String, Object> supplierData = new HashMap<>();

                                    Map<String, Object> childUpdates = new HashMap<>();

                                    supplierData.put("status", usersSupplierIds.get(supplierId).child("status").getValue(String.class));
                                    supplierData.put("timestamp", usersSupplierIds.get(supplierId).child("timestamp").getValue(Long.class));

                                    childUpdates.put("/members/" + supplierId + "/" + userId + "/", supplierData);


                                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                                            new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        // result.onSuccess(mctx.getString(R.string.status_success));
                                                    }
                                                }
                                            });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*logic for Update member subscriptionable list end*/
    }

    public void getUserSupplierlist(String userId, final ApiResult result) {
        final ArrayList<Supplier> supplierList = new ArrayList<>();
        supplierCount = 0;
        updateMembersSubscriptiontable(userId);
        DatabaseReference subcriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions/" + userId);
        //subcriptionRef.keepSynced(true);
     /*   subcriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, DataSnapshot> usersSupplierIds = new HashMap<String, DataSnapshot>();
                if (dataSnapshot.exists()) {
                    // Get all the keys from the subscription.
                    //Check if it has
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                            usersSupplierIds.put(supplierSnapshot.getKey(), supplierSnapshot);
                        }
                    }
                }

                if(!usersSupplierIds.containsKey("WDSLSgxI10eiWVey4RVWY5niElE3")){
                    usersSupplierIds.put("WDSLSgxI10eiWVey4RVWY5niElE3", null);
                }
                if(!usersSupplierIds.containsKey("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2")){
                    usersSupplierIds.put("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2", null);
                }

                supplierCount = usersSupplierIds.size();
                supplierList.clear();

                for (final String supplierId: usersSupplierIds.keySet()) {
                    DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users/"+supplierId);
                    supplierRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.exists()) {
                                    DataSnapshot supplierDetails = usersSupplierIds.get(supplierId);

                                    Supplier supplier = dataSnapshot.getValue(Supplier.class);
                                    supplier.setId(dataSnapshot.getKey());
                                    if (dataSnapshot.child("otherinfo").hasChild("ispublic")) {
                                        supplier.getOtherinfo().setIsUserPublic(dataSnapshot.child("otherinfo")
                                                .child("ispublic").getValue(Boolean.class));
                                    }
                                    if (dataSnapshot.child("otherinfo").hasChild("iscart")) {
                                        supplier.getOtherinfo().setCartEnabled(dataSnapshot.child("otherinfo")
                                                .child("iscart").getValue(Boolean.class));
                                    }

                                    // Get the groups for the supplier.
                                    if(supplierDetails != null) {
                                        SupplierGroups groups = supplierDetails.getValue(SupplierGroups.class);
                                        if (!groups.getGroups().isEmpty()) {
                                            groups.setGroupIds(new ArrayList<String>(groups.getGroups().keySet()));
                                            groups.getGroups().clear();
                                        }

                                        if (supplierDetails.hasChild("status")) {
                                            supplier.setRetailerStatus(supplierDetails.child("status").getValue(String.class));
                                        }

                                        //Add the groups.
                                        supplier.setSupplierGroups(groups);
                                    } else {
                                        supplier.setRetailerStatus("add");
                                    }
                                    supplierList.add(supplier);
                                } else {
                                    supplierCount = supplierCount - 1;
                                }
                            } catch (Exception e) {
                                supplierCount = supplierCount - 1;
                            } finally {
                                if (supplierList.size() == supplierCount) {
                                    result.onSuccess(supplierList);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (supplierList.size() == supplierCount) {
                                result.onSuccess(supplierList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(databaseError.getMessage());
            }
        });*/

        /*logic change for Get user supplier list */

        subcriptionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, DataSnapshot> usersSupplierIds = new HashMap<String, DataSnapshot>();
                if (dataSnapshot.exists()) {
                    // Get all the keys from the subscription.
                    //Check if it has
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                            usersSupplierIds.put(supplierSnapshot.getKey(), supplierSnapshot);
                        }
                    }
                }

                if (!usersSupplierIds.containsKey("WDSLSgxI10eiWVey4RVWY5niElE3")) {
                    usersSupplierIds.put("WDSLSgxI10eiWVey4RVWY5niElE3", null);
                }
                if (!usersSupplierIds.containsKey("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2")) {
                    usersSupplierIds.put("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2", null);
                }

                supplierCount = usersSupplierIds.size();
                supplierList.clear();

                for (final String supplierId : usersSupplierIds.keySet()) {
                    DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users/" + supplierId);
                    supplierRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.exists()) {
                                    DataSnapshot supplierDetails = usersSupplierIds.get(supplierId);

                                    Supplier supplier = dataSnapshot.getValue(Supplier.class);
                                    supplier.setId(dataSnapshot.getKey());
                                    if (dataSnapshot.child("otherinfo").hasChild("ispublic")) {
                                        supplier.getOtherinfo().setIsUserPublic(dataSnapshot.child("otherinfo")
                                                .child("ispublic").getValue(Boolean.class));
                                    }
                                    if (dataSnapshot.child("otherinfo").hasChild("iscart")) {
                                        supplier.getOtherinfo().setCartEnabled(dataSnapshot.child("otherinfo")
                                                .child("iscart").getValue(Boolean.class));
                                    }

                                    // Get the groups for the supplier.
                                    if (supplierDetails != null) {
                                        SupplierGroups groups = supplierDetails.getValue(SupplierGroups.class);
                                        if (!groups.getGroups().isEmpty()) {
                                            groups.setGroupIds(new ArrayList<String>(groups.getGroups().keySet()));
                                            groups.getGroups().clear();
                                        }

                                        if (supplierDetails.hasChild("status")) {
                                            supplier.setRetailerStatus(supplierDetails.child("status").getValue(String.class));
                                        }

                                        //Add the groups.
                                        supplier.setSupplierGroups(groups);
                                    } else {
                                        supplier.setRetailerStatus("add");
                                    }
                                    supplierList.add(supplier);
                                } else {
                                    supplierCount = supplierCount - 1;
                                }
                            } catch (Exception e) {
                                supplierCount = supplierCount - 1;
                            } finally {
                                if (supplierList.size() == supplierCount) {
                                    result.onSuccess(supplierList);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (supplierList.size() == supplierCount) {
                                result.onSuccess(supplierList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(databaseError.getMessage());
            }
        });

        /*End of logic change for Get user supplier list */
    }

    public void addSupplier(final String userId, final String supplierId, final ApiResult result) {
        DatabaseReference supplierRef = FirebaseDatabase.getInstance().getReference("users/" + supplierId);
        /*supplierRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final User supplierUser = dataSnapshot.getValue(User.class);
                    if (dataSnapshot.child("otherinfo").hasChild("ispublic")) {
                        if(supplierUser != null && supplierUser.getOtherinfo() != null) {
                            supplierUser.getOtherinfo().setIsUserPublic(dataSnapshot.child("otherinfo")
                                    .child("ispublic").getValue(Boolean.class));
                        }
                    }
                    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups/"+supplierId);
                    groupRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> groupIds = new ArrayList<String>();
                            User userDetails = mApp.getPreference().getUser(mctx);

                            if(userDetails != null && userDetails.getUserinfo() != null && userDetails.getUserinfo().getAddressinfo() == null){
                                result.onFailure(mctx.getString(R.string.error_addsupplierfailed));
                                return;
                            } else if (userDetails == null){
                                result.onFailure(mctx.getString(R.string.error_addsupplierfailed));
                                return;
                            }

                            String userState = mApp.getPreference().getUser(mctx).getUserinfo().getAddressinfo().getState();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot criteriaSnapshot : dataSnapshot.getChildren()) {
                                    DataSnapshot criteria = criteriaSnapshot.child("criteria");
                                    if(criteria.hasChild("usertype")){
                                        if (criteria.child("usertype").getValue(String.class).equals("retailer")) {
                                            groupIds.add(criteriaSnapshot.getKey());
                                        }
                                    }

                                    if(criteria.hasChild("state")){
                                        for (DataSnapshot stateSnapshot : criteria.child("state").getChildren()) {
                                            if(stateSnapshot.getKey().equals(userState)){
                                                if (!groupIds.contains(criteriaSnapshot.getKey())){
                                                    groupIds.add(criteriaSnapshot.getKey());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //Add the supplier to the user.
                            //Data to be updated to Supplier table ie members.
                            Map<String, Object> supplierData = new HashMap<>();
                            supplierData.put("timestamp", System.currentTimeMillis());
                            if (supplierUser.getOtherinfo() != null && supplierUser.getOtherinfo().isUserPublic()
                                    && supplierUser.getOtherinfo().getStatus().equalsIgnoreCase("enabled")
                                    && !groupIds.isEmpty()) {
                                supplierData.put("status", "accepted");
                            } else {
                                supplierData.put("status", "pending");
                            }

                            Map<String,Object> groupData = new HashMap<>();
                            for (String groupId: groupIds) {
                                groupData.put(groupId, true);
                            }

                            //Register for topics
                            registerForTopics(groupIds);

                            //Data to be updated to Retailer table ie subscriptions.
                            Map<String, Object> retailerData = new HashMap<>();
                            retailerData.put("groups", groupData);
                            retailerData.put("timestamp", System.currentTimeMillis());
                            if (supplierUser.getOtherinfo() != null && supplierUser.getOtherinfo().isUserPublic()
                                    && supplierUser.getOtherinfo().getStatus().equalsIgnoreCase("enabled")
                                    && !groupIds.isEmpty()) {
                                retailerData.put("status", "accepted");
                            } else {
                                retailerData.put("status", "pending");
                            }

                            Map<String, Object> childUpdates = new HashMap<>();
                            if(supplierData != null && retailerData != null){
                                childUpdates.put("/members/" + supplierId + "/" + userId + "/", supplierData);
                                childUpdates.put("/subscriptions/" + userId + "/" + supplierId + "/", retailerData);

                                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                                        new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            result.onSuccess(mctx.getString(R.string.status_success));
                                        } else {
                                            result.onFailure(mctx.getString(R.string.error_updatefailed));
                                        }
                                    }
                                });
                            } else {
                                result.onFailure(mctx.getString(R.string.error_updatefailed));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            result.onFailure(mctx.getString(R.string.status_failed));
                        }
                    });
                } else {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(databaseError.getMessage());
            }
        });*/

        /*Change in logic for add supplier code*/
        supplierRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final User supplierUser = dataSnapshot.getValue(User.class);
                    if (dataSnapshot.child("otherinfo").hasChild("ispublic")) {
                        if (supplierUser != null && supplierUser.getOtherinfo() != null) {
                            supplierUser.getOtherinfo().setIsUserPublic(dataSnapshot.child("otherinfo")
                                    .child("ispublic").getValue(Boolean.class));
                        }
                    }
                    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups/" + supplierId);
                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> groupIds = new ArrayList<String>();
                            User userDetails = mApp.getPreference().getUser(mctx);

                            if (userDetails != null && userDetails.getUserinfo() != null && userDetails.getUserinfo().getAddressinfo() == null) {
                                result.onFailure(mctx.getString(R.string.error_addsupplierfailed));
                                return;
                            } else if (userDetails == null) {
                                result.onFailure(mctx.getString(R.string.error_addsupplierfailed));
                                return;
                            }

                            String userState = mApp.getPreference().getUser(mctx).getUserinfo().getAddressinfo().getState();
                            String userPin =  mApp.getPreference().getUser(mctx).getUserinfo().getAddressinfo().getPincode();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot criteriaSnapshot : dataSnapshot.getChildren()) {
                                    DataSnapshot criteria = criteriaSnapshot.child("criteria");
                                    if (criteria.hasChild("usertype")) {
                                        if (criteria.child("usertype").getValue(String.class).equals("retailer")) {
                                            groupIds.add(criteriaSnapshot.getKey());
                                        }
                                    }

                                    if (criteria.hasChild("state")) {
                                        for (DataSnapshot stateSnapshot : criteria.child("state").getChildren()) {
                                            if (stateSnapshot.getKey().toLowerCase().equals(userState)) {
                                                if (!groupIds.contains(criteriaSnapshot.getKey())) {
                                                    groupIds.add(criteriaSnapshot.getKey());
                                                }
                                            }
                                        }
                                    }

                                    if (criteria.hasChild("pincode")) {
                                        for (DataSnapshot stateSnapshot : criteria.child("pincode").getChildren()) {
                                            if (stateSnapshot.getKey().equals(userPin)) {
                                                if (!groupIds.contains(criteriaSnapshot.getKey())) {
                                                    groupIds.add(criteriaSnapshot.getKey());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //Add the supplier to the user.
                            //Data to be updated to Supplier table ie members.
                            Map<String, Object> supplierData = new HashMap<>();
                            supplierData.put("timestamp", System.currentTimeMillis());
                            if (supplierUser.getOtherinfo() != null && supplierUser.getOtherinfo().isUserPublic()
                                    && supplierUser.getOtherinfo().getStatus().equalsIgnoreCase("enabled")
                                    && !groupIds.isEmpty()) {
                                supplierData.put("status", "accepted");
                            } else {
                                supplierData.put("status", "pending");
                            }

                            Map<String, Object> groupData = new HashMap<>();
                            for (String groupId : groupIds) {
                                groupData.put(groupId, true);
                            }

                            //Register for topics
                            registerForTopics(groupIds);

                            //Data to be updated to Retailer table ie subscriptions.
                            Map<String, Object> retailerData = new HashMap<>();
                            retailerData.put("groups", groupData);
                            retailerData.put("timestamp", System.currentTimeMillis());
                            if (supplierUser.getOtherinfo() != null && supplierUser.getOtherinfo().isUserPublic()
                                    && supplierUser.getOtherinfo().getStatus().equalsIgnoreCase("enabled")
                                    && !groupIds.isEmpty()) {
                                retailerData.put("status", "accepted");
                            } else {
                                retailerData.put("status", "pending");
                            }

                            Map<String, Object> childUpdates = new HashMap<>();
                            if (supplierData != null && retailerData != null) {
                                childUpdates.put("/members/" + supplierId + "/" + userId + "/", supplierData);
                                childUpdates.put("/subscriptions/" + userId + "/" + supplierId + "/", retailerData);

                                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                                        new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    result.onSuccess(mctx.getString(R.string.status_success));
                                                } else {
                                                    result.onFailure(mctx.getString(R.string.error_updatefailed));
                                                }
                                            }
                                        });
                            } else {
                                result.onFailure(mctx.getString(R.string.error_updatefailed));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            result.onFailure(mctx.getString(R.string.status_failed));
                        }
                    });
                } else {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(databaseError.getMessage());
            }
        });

          /*End of Change in logic for add supplier code*/
    }

    private void getProductKeysForSubCategories(String categoryId, final ApiResult result) {
        final ArrayList<SubCategory> subCategoryList = new ArrayList<>();

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("subcategory/" + categoryId);
        // categoryRef.keepSynced(true);
       /* categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot subCatSnapshot : dataSnapshot.getChildren()) {
                        SubCategory subCategory = subCatSnapshot.getValue(SubCategory.class);
                        subCategory.setId(subCatSnapshot.getKey());
                        if(!subCategory.getProductlist().isEmpty()){
                            subCategory.setProductIds(new ArrayList<String>(subCategory.getProductlist().keySet()));
                            subCategory.getProductlist().clear();
                        }
                        subCategoryList.add(subCategory);
                    }
                }

                result.onSuccess(subCategoryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(subCategoryList);
            }
        });*/

        /*Change in logic for Product keys list*/

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot subCatSnapshot : dataSnapshot.getChildren()) {
                        SubCategory subCategory = subCatSnapshot.getValue(SubCategory.class);
                        subCategory.setId(subCatSnapshot.getKey());
                        if (!subCategory.getProductlist().isEmpty()) {
                            subCategory.setProductIds(new ArrayList<String>(subCategory.getProductlist().keySet()));
                            subCategory.getProductlist().clear();
                        }
                        subCategoryList.add(subCategory);
                    }
                }

                result.onSuccess(subCategoryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(subCategoryList);
            }
        });

        /*End of logic in change in productkeys list*/
    }

    private void getProductKeysForGroups(String suppliersId, ArrayList<String> groupIds, final String searchStr,
                                         final ApiResult result) {
        grpProductCount = groupIds.size();
        Log.d(" **** 01 **** ", String.valueOf(groupIds));
        final HashMap<String, Product> productsList = new HashMap<>();

        for (String groupId : groupIds) {
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groupprice/" + suppliersId + "/" + groupId);
            // groupRef.keepSynced(true);
           /* groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                            Product product = new Product();
                            if(productSnapshot.hasChild("productinfo")){
                                product.setName(productSnapshot.child("productinfo").child("name").getValue(String.class));
                                if (dataSnapshot.child("productinfo").hasChild("isnew")) {
                                    product.setIsnew(dataSnapshot.child("productinfo")
                                            .child("isnew").getValue(Boolean.class));
                                }
                            }

                            if(searchStr != null){
                                if (!(product.getName().contains(searchStr.toLowerCase()))) {
                                    product = null;
                                }
                            }

                            if(product != null) {
                                product.setProductId(productSnapshot.getKey());
                                //Get the price ie dp instead of mrp
                                product.setPrice(productSnapshot.child("price").child("dp").getValue(String.class));
                                //Get the pricedrop json
                                if (productSnapshot.hasChild("pricedrop")) {
                                    try {
                                        PriceDrop priceDrop = new PriceDrop();
                                        priceDrop.setDropamount(productSnapshot.child("pricedrop").child("dropamount").getValue(String.class));
                                        priceDrop.setStartdate(productSnapshot.child("pricedrop").child("startdate").getValue(Long.class));
                                        product.setPriceDrop(priceDrop);
                                    } catch (Exception e) {

                                    }
                                }
                                //Get the offer json
                                if (productSnapshot.hasChild("offer")) {
                                    try {
                                        Offer offer = new Offer();
                                        offer.setOfferamount(productSnapshot.child("offer").child("offeramount").getValue(String.class));
                                        offer.setStartdate(productSnapshot.child("offer").child("startdate").getValue(Long.class));
                                        offer.setEnddate(productSnapshot.child("offer").child("enddate").getValue(Long.class));
                                        product.setOffer(offer);
                                    } catch (Exception e) {

                                    }
                                }

                                productsList.put(product.getProductId(),product);
                            }
                        }
                    }

                    if (grpProductCount > 0) {
                        grpProductCount--;
                    }

                    if (grpProductCount == 0) {
                        result.onSuccess(productsList);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (grpProductCount > 0) {
                        grpProductCount--;
                    }

                    if (grpProductCount == 0) {
                        result.onSuccess(productsList);
                    }
                }
            });*/


            /*Logic for product keys group*/

            groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                            Product product = new Product();
                            if (productSnapshot.hasChild("productinfo")) {
                                product.setName(productSnapshot.child("productinfo").child("name").getValue(String.class));
                                if (dataSnapshot.child("productinfo").hasChild("isnew")) {
                                    product.setIsnew(dataSnapshot.child("productinfo")
                                            .child("isnew").getValue(Boolean.class));
                                }
                            }

                            if (searchStr != null) {
                                if (!(product.getName().contains(searchStr.toLowerCase()))) {
                                    product = null;
                                }
                            }

                            if (product != null) {
                                product.setProductId(productSnapshot.getKey());
                                //Get the price ie dp instead of mrp
                                product.setPrice(productSnapshot.child("price").child("dp").getValue(String.class)); // // TODO: 18-02-2017  getMOP MRP model
                                product.setDp(productSnapshot.child("price").child("dp").getValue(String.class));
                                product.setMop(productSnapshot.child("price").child("mop").getValue(String.class));
                                product.setMrp(productSnapshot.child("price").child("mrp").getValue(String.class));

                                //Get the pricedrop json
                                if (productSnapshot.hasChild("pricedrop")) {
                                    try {
                                        PriceDrop priceDrop = new PriceDrop();
                                        priceDrop.setDropamount(productSnapshot.child("pricedrop").child("dropamount").getValue(String.class));
                                        priceDrop.setStartdate(productSnapshot.child("pricedrop").child("startdate").getValue(Long.class));
                                        product.setPriceDrop(priceDrop);
                                    } catch (Exception e) {

                                    }
                                }
                                //Get the offer json
                                if (productSnapshot.hasChild("offer")) {
                                    try {
                                        Offer offer = new Offer();
                                        offer.setOfferamount(productSnapshot.child("offer").child("offeramount").getValue(String.class));
                                        offer.setStartdate(productSnapshot.child("offer").child("startdate").getValue(Long.class));
                                        offer.setEnddate(productSnapshot.child("offer").child("enddate").getValue(Long.class));
                                        product.setOffer(offer);
                                    } catch (Exception e) {
                                    }
                                }
                                productsList.put(product.getProductId(), product);
                            }
                        }
                    }
                    if (grpProductCount > 0) {
                        grpProductCount--;
                    }
                    if (grpProductCount == 0) {
                        result.onSuccess(productsList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (grpProductCount > 0) {
                        grpProductCount--;
                    }

                    if (grpProductCount == 0) {
                        result.onSuccess(productsList);
                    }
                }
            });

            /*End */
        }
    }

    private void getProductStockDetails(String supplierId, final HashMap<String, Product> productsList, final ApiResult result) {
        DatabaseReference stockRef = FirebaseDatabase.getInstance().getReference("stock/" + supplierId);
        Log.d(" **** 02 **** ", "getProductStockDetails");
        /*stockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot productSnapshot: dataSnapshot.getChildren()) {

                        if(productsList.containsKey(productSnapshot.getKey())){
                            Long stockRemaining = Long.valueOf(productSnapshot.child("stockremaining").getValue(String.class));
                            if (stockRemaining > 0) {
                                String val = productsList.get(productSnapshot.getKey()).getName();
                                Log.i("Arpi",val);
                                productsList.get(productSnapshot.getKey()).setStockremaining(stockRemaining);
                            }
                        }
                    }
                }
                result.onSuccess(productsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(productsList);

            }
        });*/

        stockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {

                        if (productsList.containsKey(productSnapshot.getKey())) {
                            Long stockRemaining = Long.valueOf(productSnapshot.child("stockremaining").getValue(String.class));
                            if (stockRemaining > 0) {
                                String val = productsList.get(productSnapshot.getKey()).getName();
                                Log.i("Arpi", val);
                                productsList.get(productSnapshot.getKey()).setStockremaining(stockRemaining);
                            }
                        }
                    }
                }
                result.onSuccess(productsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(productsList);

            }
        });
    }

    private void getProductList(HashMap<String, Product> groupProductsList, ArrayList<SubCategory> subCategoryProductsList,
                                final ApiResult result) {

        Log.d(" **** 03 **** ", "Get Product LIst");
        final HashMap<String, ArrayList<Product>> mProductMap = new HashMap<>();
        for (SubCategory subCategory : subCategoryProductsList) {
            String subCatName = subCategory.getName();

            for (String productkey : subCategory.getProductIds()) {
                if (groupProductsList.containsKey(productkey)) {
                    ArrayList<Product> productsList;
                    if (!mProductMap.containsKey(subCatName)) {
                        productsList = new ArrayList<>();
                    } else {
                        productsList = mProductMap.get(subCatName);
                    }

                    productsList.add(groupProductsList.get(productkey));
                    mProductMap.put(subCatName, productsList);
                }
            }
        }

        result.onSuccess(mProductMap);

    }


    public void getSupplierProductList(final SupplierBindData supplierData, final String categoryId,
                                       final String searchStr, final ApiResult result) {
        Log.d(" **** 04 **** ", "getSupplierProductList");

        getProductKeysForSubCategories(categoryId, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                final ArrayList<SubCategory> subCategoryProductlist = (ArrayList<SubCategory>) data;
                getProductKeysForGroups(supplierData.getId(), supplierData.getGroupIds(), searchStr, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        HashMap<String, Product> groupProductsList = (HashMap<String, Product>) data;

                        getProductStockDetails(supplierData.getId(), groupProductsList, new ApiResult() {
                            @Override
                            public void onSuccess(Object data) {
                                HashMap<String, Product> stockProductsList = (HashMap<String, Product>) data;

                                getProductList(stockProductsList, subCategoryProductlist, new ApiResult() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        HashMap<String, ArrayList<Product>> productMap = (HashMap<String, ArrayList<Product>>) data;
                                        result.onSuccess(productMap);
                                    }

                                    @Override
                                    public void onFailure(String response) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(String response) {

                            }
                        });


                    }

                    @Override
                    public void onFailure(String response) {

                    }
                });
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }

    public void getProductDetails(String productId, final ApiResult result) {
        Log.d(" **** 05 **** ", "getProductDetails");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products/" + productId);
    /*    productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = null;
                if(dataSnapshot.exists()){
                    product = dataSnapshot.getValue(Product.class);
                }
                result.onSuccess(product);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Product product = null;
                result.onSuccess(product);
            }
        });*/

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = null;
                if (dataSnapshot.exists()) {
                    product = dataSnapshot.getValue(Product.class);
                }
                result.onSuccess(product);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Product product = null;
                result.onSuccess(product);

            }
        });
    }


    public void getProductsForGroup(final HashMap<String, DataSnapshot> productKeys, final String supplierId, final String categoryId,
                                    final String searchStr, final ApiResult result) {
        final HashMap<String, Product> productList = new HashMap<>();
        final ArrayList<Integer> productGroupCount = new ArrayList<>();
        productGroupCount.add(productKeys.size());
        for (final String productKey : productKeys.keySet()) {
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products/" + productKey);
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("----->", String.valueOf(dataSnapshot.getChildrenCount()));
                    if (dataSnapshot.exists()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (searchStr != null) {
                            if (!(product.getName().contains(searchStr.toLowerCase()) || product.getDescription().contains(searchStr.toLowerCase()))) {
                                product = null;
                            }
                        }

                        if (categoryId != null && product != null) {
                            for (DataSnapshot categoryDataSnapshot : dataSnapshot.child("users").child(supplierId).getChildren()) {
                                if (!categoryDataSnapshot.getKey().equalsIgnoreCase("searchid")) {
                                    if (!categoryId.equalsIgnoreCase(categoryDataSnapshot.getKey())) {
                                        product = null;
                                    }
                                }
                            }
                        }

                        if (product != null) {
                            product.setProductId(productKey);
                            DataSnapshot productAtt = productKeys.get(productKey);
                            //Get the price ie dp instead of mrp
                            product.setPrice(productAtt.child("price").child("dp").getValue(String.class));
                            //Get the pricedrop json
                            if (productAtt.hasChild("pricedrop")) {
                                try {
                                    PriceDrop priceDrop = new PriceDrop();
                                    priceDrop.setDropamount(productAtt.child("pricedrop").child("dropamount").getValue(String.class));
                                    priceDrop.setStartdate(productAtt.child("pricedrop").child("startdate").getValue(Long.class));
                                    product.setPriceDrop(priceDrop);
                                } catch (Exception e) {

                                }
                            }
                            //Get the offer json
                            if (productAtt.hasChild("offer")) {
                                try {
                                    Offer offer = new Offer();
                                    offer.setOfferamount(productAtt.child("offer").child("offeramount").getValue(String.class));
                                    offer.setStartdate(productAtt.child("offer").child("startdate").getValue(Long.class));
                                    offer.setEnddate(productAtt.child("offer").child("enddate").getValue(Long.class));
                                    product.setOffer(offer);
                                } catch (Exception e) {

                                }
                            }

                            //Get the subCategoryIds and CategoryId for product
                            ArrayList<String> subCategoryIds = new ArrayList<String>();
                            for (DataSnapshot categoryDataSnapshot : dataSnapshot.child("users").child(supplierId).getChildren()) {
                                if (!categoryDataSnapshot.getKey().equalsIgnoreCase("searchid")) {
                                    subCategoryIds.clear();
                                    product.setCategoryId(categoryDataSnapshot.getKey());
                                    for (DataSnapshot subcategoryDataSnapshot : categoryDataSnapshot.getChildren()) {
                                        subCategoryIds.add(subcategoryDataSnapshot.getKey());
                                    }
                                }
                            }
                            product.setSubcategoryIds(subCategoryIds);
                            productList.put(product.getProductId(), product);

                            FirebaseDatabase.getInstance().getReference().child("stock").child(supplierId).
                                    orderByKey().equalTo(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Long stockRemaining = Long.valueOf(dataSnapshot.child(productKey).child("stockremaining").getValue(String.class));
                                        if (stockRemaining > 0) {
                                            productList.get(productKey).setStockremaining(stockRemaining);
                                        }
                                    }

                                    int count = productGroupCount.get(0);
                                    count--;
                                    productGroupCount.remove(0);
                                    productGroupCount.add(0, count);
                                    if (productGroupCount.get(0) == 0) {
                                        result.onSuccess(new ArrayList<>(productList.values()));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    int count = productGroupCount.get(0);
                                    count--;
                                    productGroupCount.remove(0);
                                    productGroupCount.add(0, count);
                                    if (productGroupCount.get(0) == 0) {
                                        result.onSuccess(new ArrayList<>(productList.values()));
                                    }
                                }
                            });
                        } else {
                            int count = productGroupCount.get(0);
                            count--;
                            productGroupCount.remove(0);
                            productGroupCount.add(0, count);
                            if (productGroupCount.get(0) == 0) {
                                result.onSuccess(new ArrayList<>(productList.values()));
                            }
                        }
                    } else {
                        int count = productGroupCount.get(0);
                        count--;
                        productGroupCount.remove(0);
                        productGroupCount.add(0, count);
                        if (productGroupCount.get(0) == 0) {
                            result.onSuccess(new ArrayList<>(productList.values()));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    int count = productGroupCount.get(0);
                    count--;
                    productGroupCount.remove(0);
                    productGroupCount.add(0, count);
                    if (productGroupCount.get(0) == 0) {
                        result.onSuccess(productList);
                    }
                }
            });
        }
    }

    public void getCategoryList(final String supplierId, final ApiResult result) {
        final ArrayList<Category> mCategoryList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("category/" + supplierId);
        categoryRef.keepSynced(true);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        Category category = categorySnapshot.getValue(Category.class);
                        category.setId(categorySnapshot.getKey());
                        mCategoryList.add(category);
                    }
                }
                result.onSuccess(mCategoryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });
    }

    public void getOrderList(final String supplierId, String uid, final ApiResult result) {
        final ArrayList<Order> mOrderList = new ArrayList<>();
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders/" + supplierId + "/" + uid);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        Order myorder = orderSnapshot.getValue(Order.class);
                        myorder.setOrderId(orderSnapshot.getKey());
                        DataSnapshot productSnapshot = orderSnapshot.child("productlist");
                        mOrderList.add(myorder);
                    }
                }
                result.onSuccess(mOrderList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });
    }

    public void getSchemeList(final String supplierId, String uid, final ApiResult result) {
        grpProductCount = 0;
        final HashMap<String, ArrayList<Scheme>> mCategoryToSchemesMap = new HashMap<>();
        DatabaseReference schemeRef = FirebaseDatabase.getInstance().getReference("subscriptions/" + uid + "/" + supplierId + "/groups");
        schemeRef.keepSynced(true);
        schemeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("----->", String.valueOf(dataSnapshot.getChildrenCount()));
                if (dataSnapshot.exists()) {
                    ArrayList<String> groupIds = new ArrayList<String>();
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        groupIds.add(groupSnapshot.getKey());
                    }

                    grpSchemeCount = groupIds.size();

                    if (grpSchemeCount != 0) {
                        for (String groupId : groupIds) {
                            DatabaseReference mySchemeRef = FirebaseDatabase.getInstance().getReference("schemes/" + supplierId + "/" + groupId);
                            mySchemeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final HashMap<String, DataSnapshot> productKeys = new HashMap<>();
                                        for (DataSnapshot schemeSnapshot : dataSnapshot.getChildren()) {
                                            Scheme scheme = schemeSnapshot.getValue(Scheme.class);
                                            scheme.setSchemeId(schemeSnapshot.getKey());

                                            ArrayList<Scheme> schemeList = new ArrayList<Scheme>();
                                            String category = scheme.getCategory();
                                            if (mCategoryToSchemesMap.containsKey(category)) {
                                                schemeList = mCategoryToSchemesMap.get(category);
                                            } else {
                                                schemeList = new ArrayList<Scheme>();
                                            }
                                            schemeList.add(scheme);
                                            mCategoryToSchemesMap.put(category, schemeList);
                                        }
                                        grpSchemeCount--;
                                    } else {
                                        grpSchemeCount--;
                                    }

                                    if (grpSchemeCount == 0) {
                                        result.onSuccess(mCategoryToSchemesMap);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    grpSchemeCount--;
                                    if (grpSchemeCount == 0) {
                                        result.onSuccess(mCategoryToSchemesMap);
                                    }
                                }
                            });
                        }
                    } else {
                        result.onSuccess(mCategoryToSchemesMap);
                    }
                } else {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });
    }

    public void getNotificationList(final String supplierId, String uid, final ApiResult result) {
        grpProductCount = 0;
        final HashMap<String, ArrayList<Notification>> mCategoryToNotificationMap = new HashMap<>();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("subscriptions/" + uid + "/" + supplierId + "/groups");
        notificationRef.keepSynced(true);
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> groupIds = new ArrayList<String>();
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        groupIds.add(groupSnapshot.getKey());
                    }
                    grpNotificationCount = groupIds.size();

                    if (grpNotificationCount != 0) {
                        for (String groupId : groupIds) {
                            DatabaseReference myNotificationRef = FirebaseDatabase.getInstance().getReference("notification/" + supplierId + "/" + groupId);
                            myNotificationRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final HashMap<String, DataSnapshot> productKeys = new HashMap<>();
                                        for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                                            Notification notification = notificationSnapshot.getValue(Notification.class);
                                            notification.setNotificationId(notificationSnapshot.getKey());

                                            ArrayList<Notification> notificationList = new ArrayList<Notification>();
                                            String category = notification.getCategory();
                                            if (mCategoryToNotificationMap.containsKey(category)) {
                                                notificationList = mCategoryToNotificationMap.get(category);
                                            } else {
                                                notificationList = new ArrayList<Notification>();
                                            }
                                            notificationList.add(notification);
                                            mCategoryToNotificationMap.put(category, notificationList);
                                        }
                                        grpNotificationCount--;
                                    } else {
                                        grpNotificationCount--;
                                    }
                                    if (grpNotificationCount == 0) {
                                        result.onSuccess(mCategoryToNotificationMap);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    grpSchemeCount--;
                                    if (grpSchemeCount == 0) {
                                        result.onSuccess(mCategoryToNotificationMap);
                                    }
                                }
                            });
                        }
                    } else {
                        result.onSuccess(mCategoryToNotificationMap);
                    }
                } else {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });
    }


    public void getComplaintList(final String supplierId, String uid, final ApiResult result) {
        final ArrayList<Complaint> mComplaintList = new ArrayList<>();
        DatabaseReference complaintRef = FirebaseDatabase.getInstance().getReference("complaints/" + supplierId + "/" + uid);
        complaintRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot complaintSnapshot : dataSnapshot.getChildren()) {
                        Complaint complaint = complaintSnapshot.getValue(Complaint.class);
                        complaint.setComplaintId(complaintSnapshot.getKey());
                        mComplaintList.add(complaint);
                    }
                }
                result.onSuccess(mComplaintList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });
    }

    public void addComplaint(String supplierId, String uid, HashMap<String, Object> complaintInfo, final ApiResult result) {
        final String complaintKey = FirebaseDatabase.getInstance().getReference("complaints/" + supplierId + "/" + uid).push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/complaints/" + supplierId + "/" + uid + "/" + complaintKey + "/", complaintInfo);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }

    public void addOrderToCart(final String supplierId, final String uid, HashMap<String, Object> productInfo, final ApiResult result) {
        final String orderkey = String.valueOf(productInfo.get("productid"));

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/orderscart/" + uid + "/" + supplierId + "/" + orderkey + "/", productInfo);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            getTheOrdersCount(supplierId, uid, result);
                        } else {
                            result.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }


    public void getTheOrdersCount(String supplierId, String uid, final ApiResult result) {
        DatabaseReference ordercountRef = FirebaseDatabase.getInstance().getReference("orderscart/" + uid + "/" + supplierId);
        ordercountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = mctx.getString(R.string.status_success) + "::";
                if (dataSnapshot.exists()) {
                    data += dataSnapshot.getChildrenCount();
                } else {
                    data += "0";
                }
                result.onSuccess(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String data = mctx.getString(R.string.status_success) + "::0";
                result.onSuccess(data);
            }
        });
    }

    public void getOrdersFromCart(String uid, String supplierid, final ApiResult result) {
        final ArrayList<OrderProduct> mOrderList = new ArrayList<>();

        DatabaseReference getOrdersRef = FirebaseDatabase.getInstance().getReference("orderscart/" + uid + "/" + supplierid);
        getOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderProduct order = orderSnapshot.getValue(OrderProduct.class);
                        mOrderList.add(order);
                    }
                }
                result.onSuccess(mOrderList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onFailure(mctx.getString(R.string.status_failed));
            }
        });
    }


    public void removeOrdersFromCart(String supplierid, String uid, String orderId, final ApiResult result) {

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().
                getReference("orderscart/" + uid + "/" + supplierid + "/" + orderId);

        ordersRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    result.onSuccess(mctx.getString(R.string.status_success));
                } else {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }
        });
    }

    public void removeAllOrdersFromCart(String uid, String supplierid, final ApiResult result) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().
                getReference("orderscart/" + uid + "/" + supplierid);
        ordersRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    result.onSuccess(mctx.getString(R.string.status_success));
                } else {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }
        });
    }

    public void checkStock(final String supplierid, final HashMap<String, Long> productIdToQuantityMap, final ApiResult result) {
        final ArrayList<Boolean> productStatus = new ArrayList<>();
        final ArrayList<String> productIds = new ArrayList<String>(productIdToQuantityMap.keySet());
        for (final String productId : productIds) {
            DatabaseReference stockRef = FirebaseDatabase.getInstance().getReference("stock/" + supplierid + "/" + productId);
            stockRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean status = true;
                    Long stockRemaining = 0l;
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("stockremaining")) {
                            stockRemaining = Long.valueOf(dataSnapshot.child("stockremaining").getValue(String.class));
                        }
                        if (stockRemaining <= 0) {
                            status = false;
                        }
                    }

                    if (status) {
                        if (stockRemaining > 0) {
                            stockRemaining = stockRemaining - productIdToQuantityMap.get(productId);
                        }
                        productIdToQuantityMap.put(productId, stockRemaining);
                    }
                    productStatus.add(status);

                    if (productStatus.size() == productIds.size()) {
                        if (productStatus.contains(false)) {
                            result.onSuccess(mctx.getString(R.string.error_stock));
                        } else {
                            updateStock(supplierid, productIdToQuantityMap, result);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    productStatus.add(true);
                    productIdToQuantityMap.put(productId, 0l);
                    if (productStatus.size() == productIds.size()) {
                        if (productStatus.contains(false)) {
                            result.onSuccess(mctx.getString(R.string.error_stock));
                        } else {
                            updateStock(supplierid, productIdToQuantityMap, result);
                        }
                    }
                }
            });
        }
    }

    public void updateStock(String supplierid, final HashMap<String, Long> productIdToQuantityMap, final ApiResult result) {
        Map<String, Object> childUpdates = new HashMap<>();
        for (String productId : productIdToQuantityMap.keySet()) {
            childUpdates.put("/stock/" + supplierid + "/" + productId + "/stockremaining/", String.valueOf(productIdToQuantityMap.get(productId)));
        }

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        result.onSuccess(mctx.getString(R.string.status_success));
                    }
                });

    }

    public void addOrder(String supplierId, String uid, HashMap<String, Object> allorders, HashMap<String, Object> orderinfo, final ApiResult result) {
        final String orderKey = FirebaseDatabase.getInstance().getReference("orders/" + supplierId + "/" + uid).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/orders/" + supplierId + "/" + uid + "/" + orderKey + "/orderinfo", orderinfo);
        childUpdates.put("/orders/" + supplierId + "/" + uid + "/" + orderKey + "/productlist", allorders);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onFailure(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }

    public void sendRegistrationId(String uid, String registrationId, final ApiResult result) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/fcmregistration/" + uid, registrationId);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onFailure(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }

    public void refreshTheSupplierGroups(final String supplierId, final ApiResult result) {
        final String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups/" + supplierId);
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> groupIds = new ArrayList<String>();
                User user = mApp.getPreference().getUser(mctx);
                if (user == null || user.getUserinfo() == null) {
                    return;
                }
                String userState = user.getUserinfo().getAddressinfo().getState();
                String userPin = user.getUserinfo().getAddressinfo().getPincode();
                if (dataSnapshot.exists()) {
                    Log.e("User State", userState);
                    for (DataSnapshot criteriaSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot criteria = criteriaSnapshot.child("criteria");
                        if (criteria.hasChild("usertype")) {
                            if (criteria.child("usertype").getValue(String.class).equals("retailer")) {
                                groupIds.add(criteriaSnapshot.getKey());
                            }
                        }

                        if (criteria.hasChild("state")) {
                            for (DataSnapshot stateSnapshot : criteria.child("state").getChildren()) {
                                Log.e("snap State", stateSnapshot.getKey() );
                                if (stateSnapshot.getKey().toLowerCase().equals(userState)) {
                                    Log.e("True","true");
                                    if (!groupIds.contains(criteriaSnapshot.getKey())) {
                                        groupIds.add(criteriaSnapshot.getKey());
                                    }
                                }
                            }
                        }
                        if (criteria.hasChild("pincode")) {
                            for (DataSnapshot stateSnapshot : criteria.child("pincode").getChildren()) {
                                if (stateSnapshot.getKey().equals(userPin)) {
                                    if (!groupIds.contains(criteriaSnapshot.getKey())) {
                                        groupIds.add(criteriaSnapshot.getKey());
                                    }
                                }
                            }
                        }
                    }
                }
                //Add the supplier to the user.
                Map<String, Object> groupData = new HashMap<>();
                for (String groupId : groupIds) {
                    groupData.put(groupId, true);
                }

                //Register for topics
                registerForTopics(groupIds);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/subscriptions/" + userId + "/" + supplierId + "/groups/", groupData);

                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    if (result != null) {
                                        result.onSuccess(mctx.getString(R.string.status_success));
                                    }
                                } else {
                                    if (result != null) {
                                        result.onFailure(mctx.getString(R.string.error_updatefailed));
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (result != null) {
                    result.onFailure(mctx.getString(R.string.status_failed));
                }
            }
        });
    }

    public void checkAndSubscribeForTopic() {
        boolean isRegistered = mApp.getPreference().isTopicsRegistered(mctx);
        if (!isRegistered) {
            final ArrayList<String> topicsList = new ArrayList<>();
            DatabaseReference productGroupReference = FirebaseDatabase.getInstance().
                    getReference("subscriptions/" + mApp.getFirebaseAuth().getCurrentUser().getUid());
            productGroupReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot grouplistDataSnapshot = supplierSnapshot.child("groups");
                            for (DataSnapshot groupSnapshot : grouplistDataSnapshot.getChildren()) {
                                //if(groupSnapshot.getValue(Boolean.class)){
                                topicsList.add(groupSnapshot.getKey());
                                //}
                            }
                        }
                    }

                    registerForTopics(topicsList);
                    mApp.getPreference().setTopicsRegistered(mctx, true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void registerForTopics(ArrayList<String> list) {
        list.add("news");
        if (!list.isEmpty()) {
            for (String topicKey : list) {
                FirebaseMessaging.getInstance().subscribeToTopic(topicKey);
            }
        }
    }

    public void getAddressByPincode(final String zip, final ApiResult result) {
        final Geocoder geocoder = new Geocoder(mctx);
        try {
            List<Address> addresses = geocoder.getFromLocationName(zip, 1);
            if (addresses != null && !addresses.isEmpty()) {
                //Obtaining lat and long from zip
                Address address = addresses.get(0);
                List<Address> myaddress = geocoder.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
                if (myaddress != null && !myaddress.isEmpty()) {
                    //obtaining address from lat and long
                    Address thisaddress = myaddress.get(0);
                    result.onSuccess(thisaddress);
                }
            } else {
                // Display appropriate message when Geocoder services are not available
                result.onFailure(mctx.getString(R.string.Unable_to_fetch_the_address_for_given_pincode));
            }
        } catch (IOException e) {
            // handle exception
            result.onFailure(e.getMessage());
        }
    }

    private void getProductClaimKey(final String supplierId, String productId, final String name, final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
        DatabaseReference claimsRef = FirebaseDatabase.getInstance().getReference("claims/" + userId + "/" + supplierId);
        claimsRef.orderByChild("productid").equalTo(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "-1";
                if (dataSnapshot.exists()) {
                    for (DataSnapshot claimDataSnapshot : dataSnapshot.getChildren()) {
                        String productName = claimDataSnapshot.child("productname").getValue(String.class);
                        if (name.equalsIgnoreCase(productName)) {
                            key = claimDataSnapshot.getKey();
                            break;
                        }
                    }
                }
                result.onSuccess(key);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess("-1");
            }
        });
    }

    public void addProductToClaim(final String supplierId, final String supplierName, final Product product, final String imei,
                                  final ApiResult result) {
        getProductClaimKey(supplierId, product.getProductId(), product.getName(), new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                String claimid = (String) data;
                Map<String, Object> childUpdates = new HashMap<>();

                String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();

                if (claimid.equalsIgnoreCase("-1")) {
                    claimid = FirebaseDatabase.getInstance().getReference("claims/" + userId + "/" + supplierId).push().getKey();

                    //product info
                    childUpdates.put("/claims/" + userId + "/" + supplierId + "/" + claimid
                            + "/productid/", product.getProductId());
                    childUpdates.put("/claims/" + userId + "/" + supplierId + "/" + claimid
                            + "/productname/", product.getName());

                    //supplier info
                    HashMap<String, Object> supplierInfo = new HashMap<>();
                    supplierInfo.put("id", supplierId);
                    supplierInfo.put("name", supplierName);
                    childUpdates.put("/claims/" + userId + "/" + supplierId + "/" + claimid + "/supplierinfo/", supplierInfo);
                }


                //product
                HashMap<String, Object> productinfo = new HashMap<>();
                productinfo.put("imei", imei);
                productinfo.put("status", "claim");
                productinfo.put("price", product.getPriceDrop().getDropamount());

                String productid = FirebaseDatabase.getInstance()
                        .getReference("claims/" + userId + "/" + supplierId + "/" + claimid + "/productlist")
                        .push().getKey();

                childUpdates.put("/claims/" + userId + "/" + supplierId + "/" + claimid + "/productlist/" + productid, productinfo);

                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    result.onSuccess(mctx.getString(R.string.status_success));
                                } else {
                                    result.onSuccess(mctx.getString(R.string.error_claim));
                                }
                            }
                        });
            }

            @Override
            public void onFailure(String response) {
                result.onSuccess(mctx.getString(R.string.error_claim));
            }
        });
    }

    public void getRecordsList(final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
        final ArrayList<Record> mRecordList = new ArrayList<>();

        DatabaseReference claimRef = FirebaseDatabase.getInstance().getReference("claims/" + userId);
        claimRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot recordSnapshot : supplierSnapshot.getChildren()) {
                            Record record = recordSnapshot.getValue(Record.class);
                            record.setRecordId(recordSnapshot.getKey());
                            ArrayList<RecordInfo> recordInfoList = new ArrayList<RecordInfo>();
                            DataSnapshot productList = recordSnapshot.child("productlist");
                            for (DataSnapshot productSnapshot : productList.getChildren()) {
                                RecordInfo info = new RecordInfo();
                                info.setId(productSnapshot.getKey());
                                info.setImei(productSnapshot.child("imei").getValue(String.class));
                                info.setPrice(productSnapshot.child("price").getValue(String.class));
                                info.setStatus(productSnapshot.child("status").getValue(String.class));

                                recordInfoList.add(info);
                            }
                            record.setRecordList(recordInfoList);
                            mRecordList.add(record);
                        }
                    }
                }

                result.onSuccess(mRecordList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(mRecordList);
            }
        });
    }


    /*Changed application logic till above code*/
    public void deleteRecord(String supplierId, String recordId, final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();

        DatabaseReference recordRef = FirebaseDatabase.getInstance().
                getReference("claims/" + userId + "/" + supplierId + "/" + recordId);
        recordRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    result.onSuccess(mctx.getString(R.string.status_success));
                } else {
                    result.onFailure(mctx.getString(R.string.error_deletefailed));
                }
            }
        });
    }

    public void updateRecordInfoStatus(String supplierId, String recordId, String recordInfoId,
                                       String status, final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/claims/" + userId + "/" + supplierId + "/" + recordId
                + "/productlist/" + recordInfoId + "/status", status.trim().toLowerCase());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            result.onSuccess(mctx.getString(R.string.status_success));
                        } else {
                            result.onSuccess(mctx.getString(R.string.error_updatefailed));
                        }
                    }
                });
    }

    public void deleteRecordInfo(String supplierId, String recordId, String recordInfoId, final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();

        DatabaseReference recordRef = FirebaseDatabase.getInstance().
                getReference("claims/" + userId + "/" + supplierId + "/" + recordId + "/productlist/" + recordInfoId);
        recordRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    result.onSuccess(mctx.getString(R.string.status_success));
                } else {
                    result.onFailure(mctx.getString(R.string.error_deletefailed));
                }
            }
        });
    }

    private HashMap<String, Object> createSchemeRecordInfo(SchemeRecord record) {
        SchemeConstants constants = new SchemeConstants();

        //record
        HashMap<String, Object> recordinfo = new HashMap<>();
        recordinfo.put(constants.ENROLLED, record.getEnrolled());
        recordinfo.put(constants.SETTLED, record.getSettled());

        if (!mApp.getUtils().isStringEmpty(record.getEarnings())) {
            recordinfo.put(constants.EARNINGS, record.getEarnings());
        }

        if (!mApp.getUtils().isStringEmpty(record.getSettledby())) {
            recordinfo.put(constants.SETTLEDBY, record.getSettledby());
        }

        if (!mApp.getUtils().isStringEmpty(record.getVoucherno())) {
            recordinfo.put(constants.VOUCHERNO, record.getVoucherno());
        }

        if (record.getDate() != 0) {
            recordinfo.put(constants.DATE, record.getDate());
        }

        if (record.getSupplierinfo() != null) {
            HashMap<String, Object> supplierInfo = new HashMap<>();
            supplierInfo.put(constants.ID, record.getSupplierinfo().getId());
            supplierInfo.put(constants.NAME, record.getSupplierinfo().getName());
            recordinfo.put(constants.SUPPLIERINFO, supplierInfo);
        }

        if (record.getSchemeinfo() != null) {
            HashMap<String, Object> schemeInfo = new HashMap<>();
            schemeInfo.put(constants.SCHEMEID, record.getSchemeinfo().getId());
            schemeInfo.put(constants.SCHEMENAME, record.getSchemeinfo().getName());
            recordinfo.put(constants.SCHEMEINFO, schemeInfo);
        }

        return recordinfo;
    }

    public void addSchemeRecord(SchemeRecord record, final ApiResult result) {

        if (record.getSupplierinfo() != null && record.getSchemeinfo() != null) {
            String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
            SchemeConstants constants = new SchemeConstants();

            HashMap<String, Object> recordinfo = createSchemeRecordInfo(record);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(constants.SCHEMERECORDS + "/" + userId + "/" + record.getSupplierinfo().getId()
                    + "/" + record.getSchemeinfo().getId(), recordinfo);

            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                result.onSuccess(mctx.getString(R.string.status_success));
                            } else {
                                result.onSuccess(mctx.getString(R.string.error_schemerecord));
                            }
                        }
                    });
        } else {
            result.onSuccess(mctx.getString(R.string.error_schemerecord));
        }
    }

    public void getSchemeRecord(String schemeId, String supplierId, final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
        SchemeConstants constants = new SchemeConstants();
        DatabaseReference schemerecordRef = FirebaseDatabase.getInstance().getReference(constants.SCHEMERECORDS + "/" + userId + "/" + supplierId + "/" + schemeId);
        schemerecordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SchemeRecord record = dataSnapshot.getValue(SchemeRecord.class);
                    result.onSuccess(record);
                } else {
                    result.onSuccess(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(null);
            }
        });
    }

    public void getSchemeRecordList(final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
        final ArrayList<SchemeRecord> mRecordList = new ArrayList<>();
        SchemeConstants constants = new SchemeConstants();
        DatabaseReference schemerecordRef = FirebaseDatabase.getInstance().getReference(constants.SCHEMERECORDS + "/" + userId);
        schemerecordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot recordSnapshot : supplierSnapshot.getChildren()) {
                            SchemeRecord record = recordSnapshot.getValue(SchemeRecord.class);
                            mRecordList.add(record);
                        }
                    }
                }
                result.onSuccess(mRecordList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(mRecordList);
            }
        });
    }

    private HashMap<String, Object> createDoaRecordInfo(DoaRecord record) {
        DoaConstants constants = new DoaConstants();

        //record
        HashMap<String, Object> recordinfo = new HashMap<>();
        recordinfo.put(constants.SETTLED, record.getSettled());

        if (!mApp.getUtils().isStringEmpty(record.getSettledby())) {
            recordinfo.put(constants.SETTLEDBY, record.getSettledby());
        }

        if (!mApp.getUtils().isStringEmpty(record.getVoucherno())) {
            recordinfo.put(constants.VOUCHERNO, record.getVoucherno());
        }

        if (record.getDate() != 0) {
            recordinfo.put(constants.DATE, record.getDate());
        }

        if (record.getSupplierinfo() != null) {
            HashMap<String, Object> supplierInfo = new HashMap<>();
            supplierInfo.put(constants.ID, record.getSupplierinfo().getId());
            supplierInfo.put(constants.NAME, record.getSupplierinfo().getName());
            recordinfo.put(constants.SUPPLIERINFO, supplierInfo);
        }

        if (record.getProductinfo() != null) {
            HashMap<String, Object> productInfo = new HashMap<>();
            productInfo.put(constants.PRODUCTID, record.getProductinfo().getId());
            productInfo.put(constants.PRODUCTNAME, record.getProductinfo().getName());
            productInfo.put(constants.CATEGORY, record.getProductinfo().getCategory());
            productInfo.put(constants.IMEI, record.getProductinfo().getImei());
            recordinfo.put(constants.PRODUCTINFO, productInfo);
        }

        return recordinfo;
    }

    public void addDoaRecord(DoaRecord record, final ApiResult result) {

        if (record.getSupplierinfo() != null && record.getProductinfo() != null) {
            String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
            DoaConstants constants = new DoaConstants();

            HashMap<String, Object> recordinfo = createDoaRecordInfo(record);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(constants.DOARECORDS + "/" + userId + "/" + record.getSupplierinfo().getId()
                    + "/" + record.getProductinfo().getId(), recordinfo);

            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                result.onSuccess(mctx.getString(R.string.status_success));
                            } else {
                                result.onSuccess(mctx.getString(R.string.error_schemerecord));
                            }
                        }
                    });
        } else {
            result.onSuccess(mctx.getString(R.string.error_schemerecord));
        }
    }

    public void getDoaRecordList(final ApiResult result) {
        String userId = mApp.getFirebaseAuth().getCurrentUser().getUid();
        final ArrayList<DoaRecord> mRecordList = new ArrayList<>();
        DoaConstants constants = new DoaConstants();
        DatabaseReference doaRef = FirebaseDatabase.getInstance().getReference(constants.DOARECORDS + "/" + userId);
        doaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot supplierSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot recordSnapshot : supplierSnapshot.getChildren()) {
                            DoaRecord record = recordSnapshot.getValue(DoaRecord.class);
                            mRecordList.add(record);
                        }
                    }
                }
                result.onSuccess(mRecordList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(mRecordList);
            }
        });
    }

    public void getProductsForCategory(String supplierId, String categoryName, final ApiResult result) {
        final HashMap<String, String> productMap = new HashMap<>();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("supplierproducts/" + supplierId);
        productsRef.orderByChild("category").equalTo(categoryName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        productMap.put(productSnapshot.child("namesearchkey").
                                getValue(String.class), productSnapshot.getKey());
                    }
                }
                result.onSuccess(productMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(productMap);
            }
        });
    }

    public void getState_CityNames(String userId, final ApiResult result) {
        final HashMap<String, List<Object>> state_cityMap = new HashMap<>();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference(AppContants.SERVICECENTERS + "/State_City_Names");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        List<Object> cityNames = new ArrayList<>();
                        try {

                            List<Object> objects = (List<Object>) productSnapshot.getValue();
                            for (int i = 0; i < objects.size(); i++) {
                                cityNames.add(objects.get(i));
                            }
                            state_cityMap.put(productSnapshot.getKey(), cityNames);
                        } catch (Exception e) {
                            result.onFailure(mctx.getString(R.string.status_failed));
                        }
                    }
                }
                result.onSuccess(state_cityMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(state_cityMap);
            }
        });
    }


    public void getServiceCentersList(String userID, String stateSelected, String citySelected, final ApiResult result) {
        final ArrayList<ServiceCenterInfo> serviceCenterList = new ArrayList<>();
        DatabaseReference serviceCentersRef = FirebaseDatabase.getInstance().getReference(AppContants.SERVICECENTERS + "/ServiceCenters");
        //serviceCentersRef.orderByChild("Stateservicecenter_STATE").equalTo(stateSelected).addValueEventListener(new ValueEventListener(){
        serviceCentersRef.orderByChild("servicecenter_CITY").equalTo(citySelected).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot serviceCenterSnapshot : dataSnapshot.getChildren()) {
                        ServiceCenterInfo serviceCenterInfo = serviceCenterSnapshot.getValue(ServiceCenterInfo.class);
                        serviceCenterList.add(serviceCenterInfo);
                    }
                }
                result.onSuccess(serviceCenterList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.onSuccess(null);
            }
        });
    }


    public void getSubscriptionInfo(String uid, final ApiResult apiResult) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + "uid" + "/appSubscriptionInfo/" + " subscription_PLAN");
        //addListenerForSingleValueEvent
        userRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        String subscription_PLAN = String.valueOf(dataSnapshot.getValue());
                        // AppSubscriptionInfo appSubscriptionInfo = dataSnapshot.getValue(AppSubscriptionInfo.class);
                        apiResult.onSuccess(subscription_PLAN);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        apiResult.onFailure(databaseError.getMessage());
                    }
                });

    }

    //Retrive the FeedData.
    public void retriveFeedsData(List<String> feedUserId, final ApiResult apiResult) {
        final List<Feed> mList = new ArrayList<>();
        for (String followingId : feedUserId) {
            // mList.clear();
            if (followingId != null && !followingId.isEmpty()) {
                DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT).getRef();
                feedReference.orderByChild("idUser").equalTo(followingId).limitToLast(5).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Feed feed = snapshot.getValue(Feed.class);
                                mList.add(feed);
                            }
                        }
                        apiResult.onSuccess(mList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        apiResult.onFailure(databaseError.getMessage());
                    }
                });
            }
        }
        int i = 20;
    }

}








