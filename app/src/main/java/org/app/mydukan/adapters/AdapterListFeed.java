package org.app.mydukan.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.app.mydukan.R;
import org.app.mydukan.activities.MyNetworksActivity;
import org.app.mydukan.data.Feed;
import org.app.mydukan.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shivayogi Hiremath on 04/07/2017.
 */

public class AdapterListFeed extends RecyclerView.Adapter<AdapterListFeed.MyViewHolder> {

    final FirebaseUser auth;
    public List<Feed> mList = new ArrayList<>();
    DatabaseReference databaseReference;
    private OnClickItemFeed onClickItemFeed;


    public AdapterListFeed(final List<Feed> mList, OnClickItemFeed onClickItemFeed) {
        this.mList = mList;
        auth = FirebaseAuth.getInstance().getCurrentUser();
        this.onClickItemFeed = onClickItemFeed;
    }

    /*  public static void clearDat(){
        if(!mList.isEmpty()){
            mList.clear();
        }
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_feed2, parent, false);
        return new MyViewHolder(view,onClickItemFeed);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Feed feed = mList.get(position);
        holder.currFeed = feed;

        holder.setTvName(feed.getName());
        holder.setTvContent(feed.getText());
        holder.setTvTime(feed.getTime());
        holder.setIvAvatar(feed.getPhotoAvatar());
        holder.setIvContent(feed.getPhotoFeed());
        if (feed.getLink() != null) {
            holder.setTvLink(feed.getLink());
        }
        holder.getLikes(feed);
//        holder.changeFollowing(feed.getIdUser());

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void addFeedData(List<Feed> list) {
        mList.addAll(list);
    }

   /* public static void clearFeedList() {
        mList.clear();
    }
*/

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        holder.tvLike.setText("loading...");
        holder.ivLike.setImageResource(R.drawable.ic_action_like);
        // TODO: 02-10-2017 remove 'following' data
    }


    /**
     * Click item list
     */
    public interface OnClickItemFeed {
        void onClickItemFeed(int position, View view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Feed currFeed;
        FirebaseUser auth;
        private ImageView ivAvatar, ivLike;
        private ImageView ivContent;
        private TextView tvName, tvTime, tvContent, tvLike, tvLink;
        private Button followBTN;
        private RelativeLayout viewProfile;
        private View like, comment;
        OnClickItemFeed onClickItemFeed;
        public TextView commentTV;

        public MyViewHolder(View itemView,OnClickItemFeed onClickItemFeed) {
            super(itemView);
            auth = FirebaseAuth.getInstance().getCurrentUser();
            itemView.setOnClickListener(this);

            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvLike = (TextView) itemView.findViewById(R.id.tv_like);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvLink = (TextView) itemView.findViewById(R.id.tv_contentLink);
            ivContent = (ImageView) itemView.findViewById(R.id.iv_feed);
            ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
            followBTN = (Button) itemView.findViewById(R.id.btn_follow);
            viewProfile = (RelativeLayout) itemView.findViewById(R.id.layout_vProfile);
            commentTV= (TextView) itemView.findViewById(R.id.commentTV);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like.setOnClickListener(this);
            comment.setOnClickListener(this);
            followBTN.setOnClickListener(this);
            viewProfile.setOnClickListener(this);
            tvLink.setOnClickListener(this);
            this.onClickItemFeed=onClickItemFeed;
        }

        public void setIvAvatar(String url) {
            if (url.equals("default_uri")) {
                Glide.with(ivAvatar.getContext())
                        .load(R.drawable.profile_circle)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50, 50)
                        .into(ivAvatar);
            } else {

                Glide.with(ivAvatar.getContext())
                        // .using(new FirebaseImageLoader())
                        .load(url)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50, 50)
                        .into(ivAvatar);
            }
        }

        public void setIvContent(String url) {
            if (ivContent == null) return;
            if (url.isEmpty()) {
                ivContent.setVisibility(View.GONE);
                return;
            }
            StorageReference storageRefFeed = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            Picasso.with(ivContent.getContext()).load(url).placeholder(R.drawable.img_holder)
                    .resize(300, 180) // optional
                    .into(ivContent);
            //    Glide.with(ivContent.getContext()).using(new FirebaseImageLoader()).load(storageRefFeed).placeholder(R.drawable.img_holder).centerCrop().override(300,300).into(ivContent);
        }

        public void setTvName(String text) {
            if (tvName == null) return;
            tvName.setText(text);
        }

        public void setTvLink(String text) {
            if (tvLink == null) return;
            if (text.isEmpty() || text == null) {
                tvLink.setVisibility(View.GONE);
                return;
            } else {
                tvLink.setVisibility(View.VISIBLE);
                String sourceString = "To get more information " + "<b><font color='red'>" + "Click Here" + "</font></b> ";
                tvLink.setText(Html.fromHtml(sourceString));
            }
        }

        public void setTvTime(String text) {
            if (tvTime == null) return;
            try {
                tvTime.setText(Utils.getDisplayTimeText(text));
            } catch (ParseException e) {
                e.printStackTrace();
                tvTime.setText(text);
            }


        }


        public void setTvContent(String text) {
            if (tvContent == null) return;
            tvContent.setText(text);
        }

        public void getLikes(final Feed feed) {
            changeLikeUI(feed);
            final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(MyNetworksActivity.LIKE_ROOT + "/" + feed.getIdFeed());

            referenceLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot == null) {
                        return;
                    }
                    feed.setLikeCount((int) dataSnapshot.getChildrenCount());
                    if (dataSnapshot.child(auth.getUid()).exists()) {
                        feed.setLiked(true);
                    } else {
                        feed.setLiked(false);
                    }
                    changeLikeUI(feed);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void changeLikeUI(Feed feed) {
            if (!feed.getIdFeed().equalsIgnoreCase(currFeed.getIdFeed())) {
                return;
            }
            if (feed.isLiked()) {
                ivLike.setImageResource(R.drawable.ic_action_uplike);
            } else {
                ivLike.setImageResource(R.drawable.ic_action_like);
            }
            tvLike.setText(feed.getLikeCount() + " likes");
        }

        public void changeFollowing(final String followKey) {// followkey is user_id
            final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(MyNetworksActivity.FOLLOW_ROOT);
            // final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid());

            referenceLike.equalTo(followKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long totalFollowers = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(followKey)) {
                            totalFollowers = snapshot.getChildrenCount();
                            break;
                        }
                    }
                    if (dataSnapshot.child(followKey).hasChild(auth.getUid())) {
                        // ta curtido
                        followBTN.setText("following");
                    } else {
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

        @Override
        public void onClick(View view) {
            if (onClickItemFeed != null) {
                onClickItemFeed.onClickItemFeed(getAdapterPosition(), view);
            }
        }
    }

}

