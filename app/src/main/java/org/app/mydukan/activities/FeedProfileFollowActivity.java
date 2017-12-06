package org.app.mydukan.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CustomBaseAdapter;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.Feed;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.List;

public class FeedProfileFollowActivity extends Activity implements
        OnItemClickListener {

    public static final String FOLLOW_ROOT = "following";
    public static final String MYFOLLOW_ROOT = "followers";
    ListView listView;

    List<ChattUser> mList ;

    List<String> followersKey=new ArrayList<>();
    String id_Profile,type_Profile;
    TextView title_tv;
    AdView mAdView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text_list_base_adapter);
        title_tv= (TextView) findViewById(R.id.tv_Title);

        Bundle mybundle = getIntent().getExtras();
        if (mybundle != null) {
            if (mybundle.containsKey(AppContants.FEEDPROFILE_FOLLOW)) {
                type_Profile=mybundle.getString(AppContants.FEEDPROFILE_FOLLOW);
            }
            if (mybundle.containsKey(AppContants.MYPROFILE_FOLLOW)) {
                type_Profile=mybundle.getString(AppContants.MYPROFILE_FOLLOW);
            }
            if (mybundle.containsKey(AppContants.PROFILE_ID_FOLLOWERS)) {
                //PROFILE_FOLLOWING
               // PROFILE_FOLLOWERS
                    title_tv.setText("Profile Followers..");
                 id_Profile=mybundle.getString(AppContants.PROFILE_ID_FOLLOWERS);
                 if((id_Profile != null)&&(!id_Profile.isEmpty())) {
                   getProfileFollowers(id_Profile);
                } else {
                    Toast.makeText(this, "Unable to Get Profile Followers", Toast.LENGTH_SHORT).show();
                }
            }else if (mybundle.containsKey(AppContants.PROFILE_ID_FOLLOWING)){
                id_Profile=mybundle.getString(AppContants.PROFILE_ID_FOLLOWING);
                title_tv.setText("I am Following...");
                if((id_Profile != null)&&(!id_Profile.isEmpty())) {
                    mList = new ArrayList<ChattUser>();
                    if(!(mList.isEmpty())){
                        mList.clear();
                    }
                    getFollowingProfiles(id_Profile);
                } else {
                    Toast.makeText(this, "Unable to Get Profile Followers", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Unable to Get Profiles", Toast.LENGTH_SHORT).show();
                return;
            }
        }

//initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) findViewById(R.id.adView_myFollower);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void getFollowingProfiles(final String id_profile) {
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT+"/"+id_profile);
       referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalFollowers = 0;
                String followerKey="";
                mList = new ArrayList<ChattUser>();
                if(!(mList.isEmpty())){
                    mList.clear();
                }
                if(!(followersKey.isEmpty())){
                    followersKey.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followersKey.add(snapshot.getKey());
                    followerKey=dataSnapshot.getKey();
                    if((followerKey.isEmpty()) && (followerKey==null))  {
                        totalFollowers = snapshot.getChildrenCount();
                        // mFollowersList = (HashMap<String, String>)
                        return;
                    }
                    DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("chat_USER/" + snapshot.getKey());
                    feedReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
                                ChattUser chattUser = dataSnapshot.getValue(ChattUser.class);
                                mList.add(chattUser);
                            }
                            // showProgress(false);
                            listView = (ListView) findViewById(R.id.list);
                            CustomBaseAdapter adapter = new CustomBaseAdapter(FeedProfileFollowActivity.this, mList,type_Profile, id_profile);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(FeedProfileFollowActivity.this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

              //  Toast.makeText(FeedProfileFollowActivity.this, "FOLLOWing"+followersKey.size(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  showProgress(false);
            }
        });

    }

    private void getProfileFollowers(final String id_profile) {

        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT+"/"+id_profile);
       referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalFollowers = 0;
                String followerKey="";
                mList = new ArrayList<ChattUser>();
                if(!(followersKey.isEmpty())){
                    followersKey.clear();
                }
                if(!(mList.isEmpty())){
                    mList.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followersKey.add(snapshot.getKey());
                    followerKey=dataSnapshot.getKey();
                if((followerKey.isEmpty()) && (followerKey==null))  {
                        totalFollowers = snapshot.getChildrenCount();
                        //  mFollowersList = (HashMap<String, String>)
                       return;
                    }

                    DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("chat_USER/" + snapshot.getKey());
                    feedReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
                                ChattUser chattUser = dataSnapshot.getValue(ChattUser.class);
                                mList.add(chattUser);
                            }
                            // showProgress(false);
                            listView = (ListView) findViewById(R.id.list);
                            CustomBaseAdapter adapter = new CustomBaseAdapter(FeedProfileFollowActivity.this, mList, type_Profile,id_profile);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(FeedProfileFollowActivity.this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            //    Toast.makeText(FeedProfileFollowActivity.this, "FOLLOWERS"+followersKey.size(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
              //  showProgress(false);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
      /*  Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + mList.get(position).getName(),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();*/
    }
   /* @Override
    public void onBackPressed()
    {
        // code here to show dialog
        // super.onBackPressed();
        Intent intent = new Intent(this, FeedPrifileActivity.class);
        startActivity(intent);
        finish();
        // optional depending on your needs
    }*/


    private static void removeFollowing(final Feed feed) {
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


}