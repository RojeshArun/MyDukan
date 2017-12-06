package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.app.mydukan.R;
import org.app.mydukan.activities.MyNetworksActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.fragments.OneFragment;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shivayogi Hiremath on 11-08-2016.
 */
public class FeedListAdapter extends
        RecyclerView.Adapter<FeedListAdapter.ViewHolder> {
    //Variables
    public static List<Feed> feeds;
    public Context mContext;
    public Feed currentFeed;
    public MyDukan mApp;
    public FeedListAdapterListener mListener;
    public int supplierViewType = 1; //1 for UserSupplier, 2 for all supplier

   /* public FeedListAdapter(Context context,, FeedListAdapterListener feedListAdapterListener) {
        mContext = context;
        feeds = new ArrayList<>();
        supplierViewType = suppliertype;
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = feedListAdapterListener;
    }*/

    public FeedListAdapter(Context context, List<Feed> mList, FeedListAdapterListener feedListAdapterListener) {
        mContext = context;
        feeds=mList;
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = feedListAdapterListener;
    }


    public static void clearFeeds() {
        feeds.clear();
    }

    public static void addFeeds(ArrayList<Feed> supplier) {
        feeds.addAll(supplier);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;

        contactView = inflater.inflate(R.layout.item_list_feed, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        currentFeed = feeds.get(position);

       // String name = mApp.getUtils().toCamelCase(currentFeed.get.getName());
      //  Feed feed = mList.get(position);

        holder.setTvName(currentFeed.getName());
        holder.setTvContent( currentFeed.getText() );
        holder.setTvTime( currentFeed.getTime() );
        holder.setIvAvatar( currentFeed.getPhotoAvatar() );
        holder.setIvContent( currentFeed.getPhotoFeed() );

        holder.changeLikeImg( currentFeed.getIdFeed() );
        holder.changeFollowing( currentFeed.getIdUser());

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.changeLikeImg(position);

                }
            }
        });
        holder.followBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnAddFollowClick(position);
                }
            }
        });
        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnviewProfileClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar,ivLike;
        private ImageView ivContent;
        private TextView tvName,tvTime,tvContent,tvLike;
        private Button followBTN;
        private RelativeLayout viewProfile;

        public ViewHolder(final View itemView) {
            super(itemView);


            ivAvatar = (ImageView)itemView.findViewById( R.id.iv_avatar );
            tvName = (TextView)itemView.findViewById( R.id.tv_name );
            tvTime = (TextView)itemView.findViewById( R.id.tv_time );
            tvLike = (TextView)itemView.findViewById(R.id.tv_like);
            tvContent = (TextView)itemView.findViewById( R.id.tv_content );
            ivContent = (ImageView)itemView.findViewById( R.id.iv_feed );
            ivLike = (ImageView)itemView.findViewById( R.id.iv_like );
            followBTN = (Button) itemView.findViewById(R.id.btn_follow);
            viewProfile= (RelativeLayout) itemView.findViewById(R.id.layout_vProfile);


        }


        public void setIvAvatar(String url){
            if (ivAvatar == null)return;
            if (url.equals("default_uri")){
                Glide.with(ivAvatar.getContext())
                        .load(R.mipmap.ic_launcher)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50,50)
                        .into( ivAvatar );
            }else{

                Glide.with(ivAvatar.getContext())
                        // .using(new FirebaseImageLoader())
                        .load(url)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50,50)
                        .into(ivAvatar);
            }
        }

        public void setIvContent(String url){
            if (ivContent == null)return;
            StorageReference storageRefFeed = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            /*Picasso.with(ivContent.getContext()).load(url).placeholder(R.drawable.ic_cast_grey)
                    .resize(400,400) // optional
                    .into(ivContent);*/
            Glide.with(ivContent.getContext()).using(new FirebaseImageLoader())
                    .load(storageRefFeed)
                    .fitCenter()
                    .placeholder(R.drawable.img_holder)
                    //.override(600,400)
                    .into(ivContent);
        }

        public void setTvName(String text){
            if (tvName == null)return;
            tvName.setText( text );
        }

        public void setTvTime(String text){
            if (tvTime == null)return;
            tvTime.setText(text);
        }

        public void setTvContent(String text){
            if (tvContent == null)return;
            tvContent.setText( text );
        }

        public void changeLikeImg(final String feedKey){
            final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(MyNetworksActivity.LIKE_ROOT);
            final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            referenceLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long totalLike = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(feedKey)) {
                            totalLike = snapshot.getChildrenCount();
                            break;
                        }
                    }
                    if (dataSnapshot.child(feedKey).hasChild(auth.getUid())){
                        // ta curtido
                        ivLike.setImageResource( R.drawable.ic_action_uplike );
                    }else{
                        // nao ta curtido
                        ivLike.setImageResource( R.drawable.ic_action_like );
                    }
                    tvLike.setText(totalLike+" likes");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void changeFollowing(final String followKey){// followkey is user_id
            final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(MyNetworksActivity.FOLLOW_ROOT);
            // final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid());

            referenceLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long totalFollowers= 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(followKey)) {
                            totalFollowers = snapshot.getChildrenCount();
                            break;
                        }
                    }
                    if (dataSnapshot.child(followKey).hasChild(auth.getUid())){
                        // ta curtido
                        followBTN.setText("following");
                    }else{
                        // nao ta curtido
                        followBTN.setText("follow");
                    }
                    //  tvLike.setText(totalLike+" likes");

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public interface FeedListAdapterListener {
        void changeLikeImg(int position);
        void OnviewProfileClick(int position);
        void OnAddFollowClick(int position);
    }

}
