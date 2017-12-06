package org.app.mydukan.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.adapters.MyFeedAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyProfileActivity extends BaseActivity implements MyFeedAdapter.OnClickItemFeed, View.OnClickListener {
    String profileUID="";
    User profileDetails = new User();
    Feed feed;
    ChattUser chattUser;
    private List<Feed> mList;
    private ProgressBar mProgressBar;
    TextView tv_profile, tv_profilrEmail, tv_ProfileType, tvFollowers, tv_FeedText,tv_LinkText;
    ImageView tv_profileImg,img_BackBtn;
    Button btn_Follow;
    LinearLayout feedPost_view;
    private RecyclerView recyclerView;

    private Bitmap bitmap;
    private Uri filePath;
    String mobile = "";
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_IMAGE_REQUEST = 1;
    private static final String IMAGE_DIRECTORY = "/mydukan";
    private String profileUrl;

    private static String path = "";
    String img_str;
    String imgString=null;
    String feedText="";
    String feedLink="";
    private MyDukan mApp;

    //=========  1.distributor  2.Company Professional 3.Store Promoter / Sales Executive
    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final String MYFOLLOW_ROOT = "followers";
    public static final int GET_PHOTO = 13;
    private boolean flagFollow;
    private boolean flagLike;
    Button addPost,btn_Following,btn_Followers;
    Button uploadImgBtn,uploadLink;
    ImageView addedImageView;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mApp = (MyDukan) getApplicationContext();
        mProgressBar = (ProgressBar) findViewById(R.id.pb_1);
        initViews();
        tv_profile = (TextView) findViewById(R.id.profile_Name_1);
        tv_ProfileType = (TextView) findViewById(R.id.profile_Profection_1);
        tv_profilrEmail = (TextView) findViewById(R.id.profile_Email_1);
        tv_FeedText = (TextView) findViewById(R.id.editTextprofile_1);
        tv_LinkText = (TextView) findViewById(R.id.et_Link);

        tv_profileImg = (ImageView) findViewById(R.id.profile_IMG_1);
        img_BackBtn= (ImageView) findViewById(R.id.back_button);
        btn_Follow = (Button) findViewById(R.id.btn_follow_1);
        tvFollowers = (TextView) findViewById(R.id.profile_Followers_1);
        btn_Followers = (Button) findViewById(R.id.view_followers);
        btn_Following = (Button) findViewById(R.id.view_following);
        feedPost_view = (LinearLayout) findViewById(R.id.layout_feedPost_1);

        uploadImgBtn = (Button) findViewById(R.id.cameraBtn_1);
        uploadLink = (Button) findViewById(R.id.linkBtn_1);
        addedImageView = (ImageView) findViewById(R.id.img_addedImg_1);

        requestStoragePermission();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(null, addedImageView);

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();

            }
        });
        uploadLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_LinkText.setVisibility(View.VISIBLE);
            }
        });
        //intent.putExtra(AppContants.VIEW_PROFILE,profileID);
        Bundle mybundle = getIntent().getExtras();
        if (mybundle != null) {
            if (mybundle.containsKey(AppContants.CHAT_USER_PROFILE)) {
                chattUser = (ChattUser) mybundle.getSerializable(AppContants.CHAT_USER_PROFILE);

                if (chattUser != null) {
                    profileUID = chattUser.getuId();
                    getProfileData(profileUID);
                    changeFollowing(profileUID);
                    getListFollowing(profileUID);
                } else {
                    Toast.makeText(MyProfileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
            }
        }
        addPost = (Button) findViewById(R.id.btn_post_1);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedText = String.valueOf(tv_FeedText.getText());
                feedLink = String.valueOf(tv_LinkText.getText());

                if(!feedLink.isEmpty()){
                    boolean isLink= hyperLink(feedLink);
                    if(!isLink){
                        showOkAlert(MyProfileActivity.this,"Error", "Please add the proper Link","ok");
                    }
                }


                if (mApp.getUtils().isStringEmpty(feedText) && (imgString==null)) {
                    Toast.makeText(MyProfileActivity.this, "Please add something to post.", Toast.LENGTH_LONG).show();
                }
                if (mApp.getUtils().isStringEmpty(imgString) && (imgString==null)&& !(mApp.getUtils().isStringEmpty(tv_FeedText.getText().toString()))  ){
                    sendPhotoFirebase(feedText,feedLink);
                  //  Toast.makeText(MyProfileActivity.this, "Please add something to post.", Toast.LENGTH_LONG).show();
                }
                if (!mApp.getUtils().isStringEmpty(imgString) && !(imgString==null)) {
                    sendPhotoFirebase(imgString,feedText,feedLink);
                }

             /*   sendPhotoFirebase(imgString,feedText);
                Toast.makeText(MyProfileActivity.this, "Please add something to post.", Toast.LENGTH_LONG).show();
*/
            }
        });

        btn_Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feed != null) {
                    addFollow(feed);
                }
            }
        });


        btn_Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileUID.isEmpty()||(profileUID)==null){
                    return;
                }
                Intent activityintent = new Intent( MyProfileActivity.this, FeedProfileFollowActivity.class);
                activityintent.putExtra(AppContants.PROFILE_ID_FOLLOWING,profileUID);
                activityintent.putExtra(AppContants.MYPROFILE_FOLLOW, AppContants.MYPROFILE_FOLLOW);
                startActivity(activityintent);
            }
        });
        btn_Followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileUID.isEmpty()||(profileUID)==null){
                    return;
                }
                Intent activityintent = new Intent(MyProfileActivity.this, FeedProfileFollowActivity.class);
                activityintent.putExtra(AppContants.PROFILE_ID_FOLLOWERS,profileUID);
                activityintent.putExtra(AppContants.MYPROFILE_FOLLOW, AppContants.MYPROFILE_FOLLOW);
                startActivity(activityintent);
            }
        });
        img_BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        //initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) findViewById(R.id.adView_myProfile);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    // get the following profile count.
    public void getListFollowing(final String followingKey) {// followingkey is user_id
        showProgress(true);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalFollowing = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(followingKey)) {
                        totalFollowing = snapshot.getChildrenCount();
                        //  mFollowersList = (HashMap<String, String>)
                        break;
                    }
                }
                btn_Following.setText(totalFollowing+"\nFollowing");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
    }

    private void showFileChooser() {
        /*Intent intent = new Intent();
        intent.setType("image");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/

        /*
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image*//*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
        */
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /*   private void getPhoto() {
           new SandriosCamera(this, GET_PHOTO)
                   .setShowPicker(false)
                   .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
                   .enableImageCropping(true)
                   .launchCamera();
       }
   */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, GET_PHOTO);
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
              //  Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == GET_PHOTO && resultCode == RESULT_OK) {
            Log.e("File", "" + data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
            sendPhotoFirebase(data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
        }*/

        //Getting the imagesFrom the Camera Intent

        if (requestCode == GET_PHOTO && resultCode == RESULT_OK) {
//            Log.e("File", "" + data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
//            sendPhotoFirebase(data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
//            filePath= Uri.parse(data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));

            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imgString = (saveImage(bitmap));
            addedImageView.setImageBitmap(bitmap);
            addedImageView.setVisibility(View.VISIBLE);

          /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteFormat = stream.toByteArray();
            // get the base 64 string
            imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);*/

        }

        //Gettting the imagesFrom the Galary

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgString = saveImage(bitmap);
                addedImageView.setImageBitmap(bitmap);
                addedImageView.setVisibility(View.VISIBLE);

                /*//  tv_profileImg feedPost_view
                if (filePath != null) {
                    Glide.with(MyProfileActivity.this)
                            .load(filePath)
                            .centerCrop()
                            .transform(new CircleTransform(MyProfileActivity.this))
                            .override(120, 150)
                            .into(addedImageView);
                }*/

              /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteFormat = stream.toByteArray();
                // get the base 64 string
                imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendPhotoFirebase(String feedText,String feedLink) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT);
        String key = databaseReference.push().getKey();
        if (user != null) {
            Feed feed = new Feed();
            feed.setIdUser(user.getUid());
            feed.setName(user.getDisplayName());
            feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
            feed.setText(feedText);
            feed.setLink(feedLink);
            feed.setPhotoFeed("");
            feed.setTime(getCurrentTimeStamp());
            feed.setIdFeed(key);
            databaseReference.child(key).setValue(feed);
        }
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private void sendPhotoFirebase(String file, final String feedText,final String feedLink) {
        //=======================
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        imgString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        this.feedText = String.valueOf(tv_FeedText.getText());
       /* if ((imgString == null) || (imgString.isEmpty())) {
            Toast.makeText(this, "Please add the image ", Toast.LENGTH_LONG).show();
            return;
        }
        if ((this.feedText == null) || (this.feedText.isEmpty())) {
            Toast.makeText(this, "Please write few words regarding this Post ", Toast.LENGTH_LONG).show();
            return;
        }*/
        //=======================
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.show();
        Uri uri = Uri.fromFile(new File(file));
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydukan-1024.appspot.com");
//       // StorageReference spaceRef = storageRef.child("child name");
        StorageReference reference = storageRef.child("image/" + "feedImages/" + Calendar.getInstance().getTime());
//        StorageReference reference = FirebaseStorage.getInstance().getReference().child("image_feed/" + Calendar.getInstance().getTime() + ".jpg");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT);
                String key = databaseReference.push().getKey();
                if (user != null) {
                    Feed feed = new Feed();
                    feed.setIdUser(user.getUid());
                    feed.setName(user.getDisplayName());
                    feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
                    feed.setText(feedText);
                    feed.setLink(feedLink);
                    feed.setPhotoFeed(taskSnapshot.getDownloadUrl().toString());
                    feed.setTime(getCurrentTimeStamp());
                    feed.setIdFeed(key);
                    databaseReference.child(key).setValue(feed);
                }
                dialog.dismiss();
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                addedImageView.setVisibility(View.GONE);
                Toast.makeText(MyProfileActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setMessage("Uploaded " + ((int) progress) + "%...");
                tv_FeedText.setText("");
                addedImageView.setVisibility(View.GONE);
            }
        });
    }

    public static String getCurrentTimeStamp() {
        //  SimpleDateFormat sdfDate = new SimpleDacteFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        DateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current_Date = new Date();
        String strDate = sdfDate.format(current_Date);
        return strDate;
    }

    // Change the following button Text(Follow - UnFollow).
    public void changeFollowing(final String followKey) {// followkey is user_id
        showProgress(true);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
        // final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        // final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference(FOLLOW_ROOT+"/"+auth.getUid());

        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalFollowers = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(followKey)) {
                        totalFollowers = snapshot.getChildrenCount();
                        break;
                    }
                }
                if (dataSnapshot.child(followKey).hasChild(mApp.getFirebaseAuth().getCurrentUser().getUid())) {
                    // ta curtido
                    btn_Follow.setText("Unfollow");
                    showProgress(false);
                } else {
                    // nao ta curtido
                    btn_Follow.setText("Follow");
                    showProgress(false);
                }
                tvFollowers.setText(totalFollowers + " followers");
                btn_Followers.setText(totalFollowers+"\nFollowers");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void getProfileData(String profileUID) {
        showProgress(true);
        ApiManager.getInstance(this).getUserProfile(profileUID, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
              /*  long totalLike = 0;
                for (DataSnapshot snapshot : data.getChildren()) {
                    if (snapshot.getKey().equals(feedKey)) {
                        totalLike = snapshot.getChildrenCount();
                        break;
                    }
                }*/


                profileDetails = (User) data;
                if (profileDetails == null) {
                    showProgress(false);
                    return;
                }
                profileDetails.getId();
                if(profileDetails!=null){
                    initProfileView(profileDetails, chattUser);
                    retriveFeedsData(profileDetails, chattUser.getuId());
                }else{
                    Toast.makeText(MyProfileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
                showProgress(false);
            }

            @Override
            public void onFailure(String response) {
                Toast.makeText(MyProfileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });
    }

    private void retriveFeedsData(User profileDetails, String UserId) {
        mList = new ArrayList<>();
        if (UserId != null && !UserId.isEmpty()) {
            DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT).getRef();
            feedReference.orderByChild("idUser").equalTo(UserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Feed feed = snapshot.getValue(Feed.class);
                            mList.add(feed);
                        }
                        initRecyclerView(mList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showProgress(false);
                }
            });
        }

        showProgress(false);
    }

    private void initProfileView(User profileDetails, ChattUser mChattUser) {
        try {
            if (profileDetails.getCompanyinfo() != null) {
                tv_profile.setText(profileDetails.getCompanyinfo().getName());
            } else if (profileDetails.getUserinfo() != null) {
                tv_profile.setText(profileDetails.getUserinfo().getName());
            } else {
                tv_profile.setText("My Profile");
            }
            String profileRole = "";
            if (profileDetails.getOtherinfo() != null) {
                profileRole = profileDetails.getOtherinfo().getRole();
            }
            if (profileRole.isEmpty() && profileRole != null) {
                tv_ProfileType.setVisibility(View.VISIBLE);
                tv_ProfileType.setText(profileDetails.getOtherinfo().getRole());
            } else {
                tv_ProfileType.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Answers.getInstance().logCustom(new CustomEvent("MyNetwork_Profile ")
                    .putCustomAttribute("UnLoading Profile", "roble inLoading company info,user Role"));
            throw new IllegalArgumentException(e);
        }

        tv_profilrEmail.setText(profileDetails.getUserinfo().getEmailid() + " " + profileDetails.getUserinfo().getNumber());
        Answers.getInstance().logCustom(new CustomEvent("My Profile ")
                .putCustomAttribute("profileDetails.getUserinfo().getEmailid()", "Profile Viewed"));
  /*
  if (profileDetails.getOtherinfo().getRole()!=null || !(profileDetails.getOtherinfo().getRole().isEmpty()) ){
            switch (profileDetails.getOtherinfo().getRole()){
                case "distributor":
                    feedPost_view.setVisibility(View.VISIBLE);
                    break;
                case "Company Professional":
                    feedPost_view.setVisibility(View.VISIBLE);
                    break;
                case "Store Promoter / Sales Executive":
                    feedPost_view.setVisibility(View.VISIBLE);
                    break;
                default:
                    feedPost_view.setVisibility(View.GONE);
                    break;

            }
        }
        */

        if (mChattUser.getPhotoUrl() != null) {
            Glide.with(getApplicationContext())
                    .load(mChattUser.getPhotoUrl())
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50, 50)
                    .into(tv_profileImg);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_action_profile)
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50, 50)
                    .into(tv_profileImg);
        }
    }
    private void addFollow(final Feed feed) {
        showProgress(true);
        flagFollow = true;
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
        final DatabaseReference referenceIFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
        //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid()+"/"+feed.getIdUser());
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flagFollow) {
                    if (dataSnapshot.child(auth.getUid()).hasChild(feed.getIdUser())) {
                        referenceFollow.child(auth.getUid()).child(feed.getIdUser()).removeValue();//removing userid to following list  .
                        referenceIFollow.child(feed.getIdUser()).child(auth.getUid()).removeValue();//removing the user mCatId to distributor following list.
                        btn_Follow.setText("Follow");
                        flagFollow = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(feed.getIdFeed(), "unFollow Clicked"));
                        showProgress(false);
                    } else {
                        referenceFollow.child(auth.getUid()).child(feed.getIdUser()).setValue(true);//adding userid to following list  .
                        referenceIFollow.child(feed.getIdUser()).child(auth.getUid()).setValue(true);//adding the user mCatId to distributor following list.
                        btn_Follow.setText("UnFollow");
                        flagFollow = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(feed.getIdFeed(), "Follow Clicked"));
                        showProgress(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
       // code here to show dialog
       // super.onBackPressed();
        Intent intent = new Intent(this, MyNetworksActivity.class);
        startActivity(intent);
        // optional depending on your needs
    }

    /**
     * Init data in RecyclerView
     *
     * @param list
     */
    private void initRecyclerView(List<Feed> list) {
        // recyclerView.setAdapter(new AdapterListFeed(list, this));
        Collections.sort(list, new Comparator<Feed>() {
            // DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            @Override
            public int compare(Feed o1, Feed o2) {
                try {
                    return f.parse(o1.getTime()).compareTo(f.parse(o2.getTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        // reverse the list
        Collections.reverse(list);
        mList = list;
        recyclerView.setAdapter(new MyFeedAdapter(list, this));
    }

    /**
     * Bind views XML with JavaAPI
     */
    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_feed_1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onClickItemFeed(int position, View view) {
        final Feed feed = mList.get(position);
        switch (view.getId()) {
            case R.id.iv_like:  // Like_BTN
                addLike(feed);
                break;
            case R.id.btn_follow:  // follow_BTN
                addFollow(feed);
                break;
            case R.id.iv_Delete: // followBTN
                if (feed.getIdUser() != null && !(feed.getIdUser().isEmpty())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Delete Post");
                    alertDialog.setMessage("Do you want to delete the Post ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    detetePost(feed);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                 /*   String profileID = feed.getIdUser();
                    Intent intent = new Intent(this, MyProfileActivity.class);
                    intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
                    startActivity(intent);
                */
                }
                break;
            case R.id.tv_contentLink:  // HyperLink click
               String textHyper= feed.getText();
                if(textHyper.isEmpty()|| textHyper==null){
                    return;
                }else{
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean hyperLink(String textHyper) {
        /*final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(textHyper);//replace with string to compare*/

        List<String> containedUrls = new ArrayList<String>();
       // String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        String urlRegex ="^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(textHyper);
        while (urlMatcher.find())
        {
            containedUrls.add(textHyper.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
            return true;
        }
        return false;
      /*  if(m.find()) {
            System.out.println("String contains URL");
            return true;
        }
        return false;*/
    }

    private void addLike(final Feed feed) {
        flagLike = true;
        final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(LIKE_ROOT);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flagLike) {
                    if (dataSnapshot.child(feed.getIdFeed()).hasChild(auth.getUid())) {
                        referenceLike.child(feed.getIdFeed()).child(auth.getUid()).removeValue();
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(feed.getIdFeed(), "Like Clicked"));
                        flagLike = false;
                    } else {
                        referenceLike.child(feed.getIdFeed()).child(auth.getUid()).setValue(true);
                        flagLike = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(feed.getIdFeed(), "unLike Clicked"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void detetePost(Feed feed) {
        showProgress(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT + "/" + feed.getIdFeed());
        databaseReference.removeValue();

        String feedIMG_Url =feed.getPhotoFeed();
        if(!(feedIMG_Url==null)&& !(feedIMG_Url.isEmpty()) ){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(feed.getPhotoFeed());
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Toast.makeText(MyProfileActivity.this, "Post is removed file", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Toast.makeText(MyProfileActivity.this, "Post is did not removed", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        }


        showProgress(false);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    private void removeFollowing(final Feed feed) {
        //  showProgress(true);
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
        final DatabaseReference referenceIFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
        //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid()+"/"+feed.getIdUser());
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(auth.getUid()).hasChild(feed.getIdUser())) {
                    referenceFollow.child(auth.getUid()).child(feed.getIdUser()).removeValue();//removing userid to following list  .
                    referenceIFollow.child(feed.getIdUser()).child(auth.getUid()).removeValue();//removing the user mCatId to distributor following list.
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //showProgress(false);
            }
        });

    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}
