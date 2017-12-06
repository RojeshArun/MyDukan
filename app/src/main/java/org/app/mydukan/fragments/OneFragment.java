package org.app.mydukan.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
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
import org.app.mydukan.activities.CommentsActivity;
import org.app.mydukan.activities.FeedPrifileActivity;
import org.app.mydukan.activities.MainActivity;
import org.app.mydukan.activities.MyProfileActivity;
import org.app.mydukan.activities.WebViewActivity;
import org.app.mydukan.adapters.AdapterListFeed;
import org.app.mydukan.adapters.SupplierAdapter;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.app.mydukan.R.id.view;


public class OneFragment extends Fragment implements AdapterListFeed.OnClickItemFeed, View.OnClickListener {
    View mView;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final int GET_PHOTO = 11;
    private String userType = "Retailer";

    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    private List<Feed> mList=new ArrayList<>();
    private List<Feed> mFeedList;
    FloatingActionButton addPost;
    private ImageView profileIMG;
    private TextView profileName, profileEmail, newPOST;
    private boolean flagLike;
    private boolean flagFollow;
    Context context;
    AdView mAdView;
    private FloatingActionButton appNewPost;
    //Variables
    private AdapterListFeed adapterListFeed;
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_one, container, false);
        context = mView.getContext();
        initViews();
        retrieveData();
        //initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(context, "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) mView.findViewById(R.id.adView_myNetwork_one);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return mView;
    }

    /**
     * Retrieve Data from Firebase
     */
    private void retrieveData() {
        showProgress(true);
        final List<String> feedUserId = new ArrayList<>();
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT + "/" + auth.getUid());
        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        feedUserId.add(snapshot.getKey());
                    }
                    retriveFeedsData(feedUserId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
 /*   private void retriveFeedsData(List<String> feedUserId) {
        mList = new ArrayList<>();
        for (String followingId : feedUserId) {
            if (followingId != null && !followingId.isEmpty()) {
                DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT).getRef();
                feedReference.orderByChild("idUser").equalTo(followingId).addValueEventListener(new ValueEventListener() {
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
            // initRecyclerView(mList);
        }
        showProgress(false);
    }

*/

    //==================================================================
    private void retriveFeedsData(List<String> feedUserId) {
        showProgress(true);
      //  mList.clear();
        try {
            ApiManager.getInstance(getActivity()).retriveFeedsData(feedUserId,new ApiResult(){
                @Override
                public void onSuccess(Object data) {

                   List<Feed> cList = (ArrayList<Feed>) data;

                    if (cList.isEmpty()) {
                        showProgress(false);
                        return;
                    } else {
                       // mList.clear();
                      //  AdapterListFeed.clearDat();
                        initRecyclerView(cList);
                        showProgress(false);
                    }
                }

                @Override
                public void onFailure(String response) {
                    //  mSupplierEmptyView.setVisibility(View.VISIBLE);
                    if(response.equals(getString(R.string.status_failed)))
                        showProgress(false);
                }
            });
        } catch (Exception e) {
            //  dismissProgress();
            showProgress(false);
        }
    }

    //==================================================================

    private void clearTheData() {
        if (!mList.isEmpty()) {
            mList.clear();
        }
    }

    @Override
    public void onClickItemFeed(int position, View view) {
        Feed feed = mList.get(position);
        switch (view.getId()) {
            case R.id.iv_like:  // Like_BTN
                addLike(feed);
                break;
            case R.id.btn_follow:  // follow_BTN
                addFollow(feed);
                break;
            case R.id.layout_vProfile: // followBTN
                if (feed.getIdUser() != null && !(feed.getIdUser().isEmpty())) {
                    String profileID = feed.getIdUser();
                    Intent intent = new Intent(getContext(), FeedPrifileActivity.class);
                    intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
                    startActivity(intent);
                }
                break;
            case R.id.tv_contentLink:  // HyperLink click
                String textHyper= feed.getText();
                if(textHyper.isEmpty()|| textHyper==null){
                    return;
                }else{
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
                    startActivity(intent);
                }
                break;
            case R.id.comment:
                Intent intent=new Intent(getContext(),CommentsActivity.class);
                intent.putExtra(AppContants.FEED,feed);
                startActivity(intent);
                break;


        }
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

    private void addFollow(final Feed feed) {
        flagFollow = true;
        final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
        //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid()+"/"+feed.getIdUser());
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flagFollow) {
                    if (dataSnapshot.child(auth.getUid()).hasChild(feed.getIdUser())) {
                        referenceLike.child(auth.getUid()).child(feed.getIdUser()).removeValue();
                        flagFollow = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(feed.getIdFeed(), "unFollow Clicked"));
                    } else {
                        referenceLike.child(auth.getUid()).child(feed.getIdUser()).setValue(true);
                        flagFollow = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(feed.getIdFeed(), "Follow Clicked"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Init data in RecyclerView
     *
     * @param list
     */
    private void initRecyclerView(List<Feed> list) {
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
        mList=(list);
        // adapterListFeed.addFeedData(mList);
        // adapterListFeed.notifyDataSetChanged();

        recyclerView.setAdapter(new AdapterListFeed(list, this));
    }

    /**
     * Bind views XML with JavaAPI
     */
    private void initViews() {
        mProgressBar = (ProgressBar) mView.findViewById(R.id.pb);
        recyclerView = (RecyclerView) mView.findViewById(R.id.rv_list_feed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

/*
    //Listview to find when user is scrolling and reach the top i.e firstVisibleItem == 0

recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
        int visibleItemCount, int totalItemCount) {

            if (firstVisibleItem == 0 && !loading) {
                msgNum += 10;
                setMessagesList(msgNum); //your load more function
            }
        }
    });

*/


    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View view) {

    }
}
