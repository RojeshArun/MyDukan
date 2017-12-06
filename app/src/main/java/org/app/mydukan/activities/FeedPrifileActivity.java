package org.app.mydukan.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
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
import com.sandrios.sandriosCamera.internal.SandriosCamera;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FeedPrifileActivity extends AppCompatActivity implements View.OnClickListener {
    String profileUID;
    User profileDetails = new User();
    Feed feed;
    private ProgressBar mProgressBar;
    TextView tv_profile, tv_profilrEmail, tv_ProfileType,tvFollowers;
    ImageView tv_profileImg;
    Button btn_Follow,btn_Following,btn_Followers;
    LinearLayout feedPost_view;

//=========================================
    private ListView mCompleteListView;
    private Button mAddItemToList;
    private HashMap<String,String> mFollowersList =new HashMap<>();
    private List<String> mFollowingList =new ArrayList<String>();
    private static final int MIN = 0, MAX = 10000;
    //======================================
    private Bitmap bitmap;
    private Uri filePath;
    String mobile = "";
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_IMAGE_REQUEST = 1;
    private String profileUrl;

    private static String path="";
    //====================
    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final String MYFOLLOW_ROOT = "followers";
    public static final int GET_PHOTO = 13;
    private boolean flagFollow;
    Button addPost;
    Button uploadImgBtn;
    ImageView addedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_profile);

        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        tv_profile = (TextView) findViewById(R.id.profile_Name);
        tv_ProfileType = (TextView) findViewById(R.id.profile_Profection);
        tv_profilrEmail = (TextView) findViewById(R.id.profile_Email);
        tv_profileImg = (ImageView) findViewById(R.id.profile_IMG);
        btn_Follow = (Button) findViewById(R.id.btn_follow);
        btn_Followers = (Button) findViewById(R.id.view_followers);
        btn_Following = (Button) findViewById(R.id.view_following);
        tvFollowers = (TextView) findViewById(R.id.profile_Followers);

        feedPost_view= (LinearLayout) findViewById(R.id.layout_feedPost);

        uploadImgBtn = (Button) findViewById(R.id.cameraBtn);
        addedImageView = (ImageView) findViewById(R.id.img_addedImg);

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

//intent.putExtra(AppContants.VIEW_PROFILE,profileID);

        Bundle mybundle = getIntent().getExtras();
        if (mybundle != null) {
            if (mybundle.containsKey(AppContants.VIEW_PROFILE)) {
                feed = (Feed) mybundle.getSerializable(AppContants.VIEW_PROFILE);

                if (feed != null) {
                    profileUID = feed.getIdUser();
                    getProfileData(profileUID);
                    getListFollowing(feed.getIdUser());
                    changeFollower(feed.getIdUser());

                } else {
                    Toast.makeText(FeedPrifileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //============================================
     /*   mItems = new ArrayList<String>();
        initViews();
        mListAdapter = new CompleteListAdapter(this, mItems);
        mCompleteListView.setAdapter(mListAdapter);
        addItemsToList();*/
       // FeedProfileFollowActivity
        //============================================

        addPost = (Button)  findViewById(R.id.btn_post);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getPhoto();
                sendPhotoFirebase(String.valueOf(filePath));
            }
        });

        btn_Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((feed.getIdUser()).isEmpty()||(feed.getIdUser())==null){
                    return;
                }
                Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                        .putCustomAttribute(feed.getIdFeed(), "Following Checked"));
                Intent activityintent = new Intent(FeedPrifileActivity.this, FeedProfileFollowActivity.class);
                activityintent.putExtra(AppContants.PROFILE_ID_FOLLOWING,feed.getIdUser());
                activityintent.putExtra(AppContants.FEEDPROFILE_FOLLOW, AppContants.FEEDPROFILE_FOLLOW);
                startActivity(activityintent);
            }
        });
        btn_Followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((feed.getIdUser()).isEmpty()||(feed.getIdUser())==null){
                    return;
                }
                Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                        .putCustomAttribute(feed.getIdFeed(), "Followers Checked"));
                Intent activityintent = new Intent(FeedPrifileActivity.this, FeedProfileFollowActivity.class);
                activityintent.putExtra(AppContants.PROFILE_ID_FOLLOWERS,feed.getIdUser());
                activityintent.putExtra(AppContants.FEEDPROFILE_FOLLOW, AppContants.FEEDPROFILE_FOLLOW);
                startActivity(activityintent);
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
    }

    @Override
    public void onClick(View v) {
     /*   switch (v.getId()) {
            case R.mCatId.addItemToList:
                addItemsToList();
                break;
        }*/
    }

    private void showFileChooser() {
        /*Intent intent = new Intent();
        intent.setType("image");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/

        /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image*//*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);*/
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(FeedPrifileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    dispatchTakePictureIntent();
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void getPhoto() {
        new SandriosCamera(this, GET_PHOTO)
                .setShowPicker(false)
                .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
                .enableImageCropping(true)
                .launchCamera();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_PHOTO && resultCode == RESULT_OK) {
//            Log.e("File", "" + data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
//            sendPhotoFirebase(data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
//            filePath= Uri.parse(data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                addedImageView.setImageBitmap(imageBitmap);

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                addedImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendPhotoFirebase(String file) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.show();

        Uri uri = Uri.fromFile(new File(file));
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("image_feed/" + Calendar.getInstance().getTime() + ".jpg");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT);
                String key = databaseReference.push().getKey();

                if (user != null) {
                    Feed feed = new Feed();
                    feed.setIdUser(user.getUid());
                    feed.setName(user.getDisplayName());
                    feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
                    feed.setPhotoFeed(taskSnapshot.getDownloadUrl().toString());
                    feed.setText("post textdata  body content");
                    feed.setTime(getCurrentTimeStamp());
                    feed.setIdFeed(key);
                    databaseReference.child(key).setValue(feed);
                }
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(FeedPrifileActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }
    public static String getCurrentTimeStamp() {
        //  SimpleDateFormat sdfDate = new SimpleDacteFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        DateFormat sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current_Date = new Date();
        String strDate = sdfDate.format(current_Date);
        return strDate;
    }

    // Change the following button Text(Follow - UnFollow).
    public void changeFollower(final String followKey) {// followkey is user_id
        showProgress(true);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
       final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
        // final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        // final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference(FOLLOW_ROOT+"/"+auth.getUid());
//DataSnapshot { key = followers, value = {lw7sgr8jgWPOFiPiioecvcofSFk1={NC8By7oxjjeYVQxSfjiY3nYWoGq1=true}, kVihGQVdNPgwUoHkaLiw6X1czN43={kVihGQVdNPgwUoHkaLiw6X1czN43=true, jaXVRe8e9Wg6tlZDpnfz5iVIE1d2=true, NC8By7oxjjeYVQxSfjiY3nYWoGq1=true}, jaXVRe8e9Wg6tlZDpnfz5iVIE1d2={lw7sgr8jgWPOFiPiioecvcofSFk1=true, kVihGQVdNPgwUoHkaLiw6X1czN43=true, jaXVRe8e9Wg6tlZDpnfz5iVIE1d2=true, NC8By7oxjjeYVQxSfjiY3nYWoGq1=true}, NC8By7oxjjeYVQxSfjiY3nYWoGq1={kVihGQVdNPgwUoHkaLiw6X1czN43=true, jaXVRe8e9Wg6tlZDpnfz5iVIE1d2=true}} }
        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalFollowers = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(followKey)) {
                        totalFollowers = snapshot.getChildrenCount();
                      //  mFollowersList = (HashMap<String, String>)
                        break;
                    }
                }
             //   Toast.makeText(FeedPrifileActivity.this, "FOLLOWERS "+mFollowersList.size(), Toast.LENGTH_LONG).show();
                if (dataSnapshot.child(followKey).hasChild(auth.getUid())) {
                    // ta curtido
                    btn_Follow.setText("Unfollow");
                    Answers.getInstance().logCustom(new CustomEvent("Feed ProfilePage")
                            .putCustomAttribute("Profile Unfollow", "profile Clicked unfollow Clicked"));
                    showProgress(false);
                } else {
                    // nao ta curtido
                    btn_Follow.setText("Follow");
                    Answers.getInstance().logCustom(new CustomEvent("Feed ProfilePage")
                            .putCustomAttribute("Profile follow", "profile Clicked follow Clicked"));
                    showProgress(false);
                }


                tvFollowers.setText(totalFollowers+" Followers");
                btn_Followers.setText(totalFollowers+"\nFollowers");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
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
                Toast.makeText(FeedPrifileActivity.this, "User Profile: " + profileDetails.getCompanyinfo().getName() + ",\n" + profileDetails.getCompanyinfo().getIndustry(), Toast.LENGTH_SHORT).show();
                profileDetails.getId();
                initProfileView(profileDetails, feed);
                showProgress(false);
            }
            @Override
            public void onFailure(String response) {
                Toast.makeText(FeedPrifileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });
    }

    private void initProfileView(User profileDetails, Feed feed) {
        tv_profile.setText(profileDetails.getCompanyinfo().getName());
        tv_ProfileType.setText(profileDetails.getOtherinfo().getRole());
        tv_profilrEmail.setText(profileDetails.getUserinfo().getEmailid() + " " + profileDetails.getUserinfo().getNumber());
        Answers.getInstance().logCustom(new CustomEvent("Feed Profile ")
                .putCustomAttribute("profileDetails.getUserinfo().getEmailid()", "Profile Viewed"));
       /* if (profileDetails.getOtherinfo().getRole()!=null || !(profileDetails.getOtherinfo().getRole().isEmpty()) ){
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
        }*/

        if (feed.getPhotoAvatar() != null) {
            Glide.with(FeedPrifileActivity.this)
                    .load(feed.getPhotoAvatar())
                    .centerCrop()
                    .transform(new CircleTransform(FeedPrifileActivity.this))
                    .override(50, 50)
                    .into(tv_profileImg);
        } else {
            Glide.with(FeedPrifileActivity.this)
                    .load(R.drawable.ic_action_profile)
                    .centerCrop()
                    .transform(new CircleTransform(FeedPrifileActivity.this))
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
                        Answers.getInstance().logCustom(new CustomEvent("feed Profile follow ")
                                .putCustomAttribute("auth.getUid()", "Following: "+profileDetails.getUserinfo().getEmailid()));
                        flagFollow = false;
                        showProgress(false);
                    } else {
                        referenceFollow.child(auth.getUid()).child(feed.getIdUser()).setValue(true);//adding userid to following list  .
                        referenceIFollow.child(feed.getIdUser()).child(auth.getUid()).setValue(true);//adding the user mCatId to distributor following list.
                        btn_Follow.setText("UnFollow");
                        Answers.getInstance().logCustom(new CustomEvent("feed Profile Unfollow ")
                                .putCustomAttribute("auth.getUid()", "Unfollowing: "+profileDetails.getUserinfo().getEmailid()));
                        flagFollow = false;
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


}