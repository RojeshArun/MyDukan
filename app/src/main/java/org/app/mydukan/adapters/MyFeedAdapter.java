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
import com.squareup.picasso.Picasso;

import org.app.mydukan.R;
import org.app.mydukan.activities.MyNetworksActivity;
import org.app.mydukan.activities.MyProfileActivity;
import org.app.mydukan.data.Feed;

import java.util.List;


/**
 * Created by Shivayogi Hiremath on 24/07/2017.
 */

public class MyFeedAdapter extends RecyclerView.Adapter<MyFeedAdapter.MyViewHolder>{

    private List<Feed> mList;
    private OnClickItemFeed onClickItemFeed;

    public MyFeedAdapter(List<Feed> mList, MyProfileActivity onClickItemFeed) {
        this.mList = mList;
        this.onClickItemFeed = onClickItemFeed;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_feedposts,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Feed feed = mList.get(position);
        holder.setTvName(feed.getName());
        holder.setTvContent( feed.getText() );
        holder.setTvTime( feed.getTime() );
        if(feed.getLink()!=null){
            holder.setTvLink(feed.getLink());
        }
        holder.setIvAvatar( feed.getPhotoAvatar() );
        holder.setIvContent( feed.getPhotoFeed() );
        holder.changeLikeImg( feed.getIdFeed() );
        holder.changeFollowing( feed.getIdUser());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivAvatar,ivLike;
        private ImageView ivContent,ivDelete;
        private TextView tvName,tvTime,tvContent,tvLike,tvLink;
        private Button followBTN;
        private RelativeLayout viewProfile;
        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            ivAvatar = (ImageView)itemView.findViewById( R.id.iv_avatar );
            ivDelete= (ImageView)itemView.findViewById( R.id.iv_Delete);
            tvName = (TextView)itemView.findViewById( R.id.tv_name );
            tvTime = (TextView)itemView.findViewById( R.id.tv_time );
            tvLike = (TextView)itemView.findViewById(R.id.tv_like);
            tvContent = (TextView)itemView.findViewById( R.id.tv_content );
            tvLink = (TextView)itemView.findViewById( R.id.tv_contentLink );

            ivContent = (ImageView)itemView.findViewById( R.id.iv_feed );
            ivLike = (ImageView)itemView.findViewById( R.id.iv_like );
            followBTN = (Button) itemView.findViewById(R.id.btn_follow);
            viewProfile= (RelativeLayout) itemView.findViewById(R.id.layout_vProfile);
            ivLike.setOnClickListener(this);
            followBTN.setOnClickListener(this);
            viewProfile.setOnClickListener(this);
            ivDelete.setOnClickListener(this);
            tvContent.setOnClickListener(this);
            tvLink.setOnClickListener(this);
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
                        .load( url )
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50,50)
                        .into(ivAvatar);
            }
        }

        public void setIvContent(String url){
            if (ivContent == null)return;
            if(url.isEmpty()){
                ivContent.setVisibility(View.GONE);
                return;
            }

            /*Picasso.with(ivContent.getContext()).load(url).placeholder(R.drawable.ic_cast_grey)
                    .resize(400,400) // optional
                    .into(ivContent);*/

            Glide.with(ivContent.getContext())
                    .load(url)
                    .placeholder(R.drawable.img_holder)
                    .centerCrop()
                   // .override(600,300)
                    .into(ivContent);

          /*
            Picasso.with(ivContent.getContext()).load(url)
                    .resize(900,1000)
                    //.centerCrop()
                    .placeholder(R.color.cardview_shadow_start_color)                      // optional
                    .into(ivContent);
                    */
        }

        public void setTvName(String text){
            if (tvName == null)return;
            tvName.setText( text );
        }
        public void setTvLink(String text){
            if (tvLink == null)return;
            if(text.isEmpty() || text==null){
                tvLink.setVisibility(View.GONE);
                return;
            }else{
                tvLink.setVisibility(View.VISIBLE);
                String sourceString = "To get more information "+"<b><font color='red'>" +"Click Here" + "</font></b> ";
                tvLink.setText(Html.fromHtml(sourceString));
            }
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

        @Override
        public void onClick(View view) {
            if (onClickItemFeed != null){
                onClickItemFeed.onClickItemFeed(getAdapterPosition(),view);
            }
        }
    }
    /**
     * Click item list
     */
    public interface OnClickItemFeed{
        void onClickItemFeed(int position, View view);
    }


   /* public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    Pattern p = Pattern.compile(URL_REGEX);
    Matcher m = p.matcher("example.com");//replace with string to compare
if(m.find()) {
        System.out.println("String contains URL");
    }*/
}
