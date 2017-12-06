package org.app.mydukan.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.Feed;
import org.app.mydukan.fragments.OneFragment;
import org.app.mydukan.fragments.TwoFragment;
import org.app.mydukan.utils.AppContants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MyNetworksActivity extends AppCompatActivity {

    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final int GET_PHOTO = 11;
    private String userType="Retailer";
    public static final String FRAGMENT_TAG = "search_fragment";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView profileIMG, backBTN;
    private FloatingActionButton fab_addPosts;
    RelativeLayout profile_view;
    private TextView profileName,profileEmail;
    ImageView searchBTN, addProfileBTN;
    ChattUser chattUser;
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    private List<Feed> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_networks);
        Answers.getInstance().logCustom(new CustomEvent("MyNetwork")
                .putCustomAttribute("MyNetwork_page", "Mynetwork page Opened"));
        profileIMG= (ImageView) findViewById(R.id.img_pic);

        profileName=(TextView) findViewById(R.id.tv_profileName);
        profileEmail=(TextView) findViewById(R.id.tv_profileEmail);
        profile_view = (RelativeLayout) findViewById(R.id.profile_view);
        searchBTN =(ImageView) findViewById(R.id.btn_Search);

        backBTN =(ImageView) findViewById(R.id.back_button);
        fab_addPosts  = (FloatingActionButton) findViewById(R.id.fab_addPosts);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        getCurrentUserData(auth);

    /*  ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200);
        //config.setContentTextColor(Color.parseColor("#400000"));
        config.setDismissTextColor(Color.parseColor("#64DD17"));
        config.setMaskColor(Color.parseColor("#dc4b4b4b"));

        // half second between each showcase view  Mask colors #EA80FC   #D81B60  #009688  #E0E0E0
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1010_id");
        sequence.setConfig(config);
        sequence.addSequenceItem(searchBTN, "click here to search the Profile and follow to see their Posts", "Okay");

        sequence.start();
        */

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Fragment mFragment = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.mCatId.search_container, mFragment).commit();*/
                Intent intent = new Intent(MyNetworksActivity.this, Search_MyNetworkActivity.class);
                startActivity(intent);
                Answers.getInstance().logCustom(new CustomEvent("MyNetwork_search")
                        .putCustomAttribute("Search Button", " searh button Clicked"));
            }
        });



        fab_addPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("MyNetwork_search")
                        .putCustomAttribute("Own Profile", "Own profile Clicked"));
                Intent intent = new Intent(MyNetworksActivity.this, MyProfileActivity.class);
                intent.putExtra(AppContants.CHAT_USER_PROFILE, (Serializable) chattUser);
                startActivity(intent);

            }
        });
        profile_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Fragment mFragment = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.mCatId.search_container, mFragment).commit();
                */

                Answers.getInstance().logCustom(new CustomEvent("MyNetwork_search")
                        .putCustomAttribute("Own Profile", "Own profile Clicked"));
                Intent intent = new Intent(MyNetworksActivity.this, MyProfileActivity.class);
                intent.putExtra(AppContants.CHAT_USER_PROFILE, (Serializable) chattUser);
                startActivity(intent);
            }
        });

    }

    private void getCurrentUserData(FirebaseUser auth) {
        //showProgress(true);
        mList = new ArrayList<>();
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("chat_USER/" + auth.getUid());
        feedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    chattUser = dataSnapshot.getValue(ChattUser.class);
                    userType =  chattUser.getUserType();
                    InitProfileView(chattUser);
                    if(userType.equalsIgnoreCase("Ditributor")){

                    }
                }
                // showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        Intent intent = new Intent(MyNetworksActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void InitProfileView(ChattUser chattUser) {

        profileName.setText(chattUser.getName());
        profileEmail.setText(chattUser.getEmail());
        if( chattUser.getPhotoUrl()!=null)
        {
            Glide.with(this)
                    .load( chattUser.getPhotoUrl() )
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50,50)
                    .into(profileIMG);
        }else{
            Glide.with(this)
                    .load( R.mipmap.ic_launcher )
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50,50)
                    .into(profileIMG);
        }
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("MyDukan Posts");
        // tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabTwo);

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("My Network");
       // tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabOne);


      /*  TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("MyDukanPosts");
      //  tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_google, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);*/
    }

    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TwoFragment(), "TWO");
        adapter.addFrag(new OneFragment(), "ONE");
      //  adapter.addFrag(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
