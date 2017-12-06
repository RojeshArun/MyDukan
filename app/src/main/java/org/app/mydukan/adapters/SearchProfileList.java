package org.app.mydukan.adapters;

import android.support.v7.widget.RecyclerView;
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

import org.app.mydukan.R;
import org.app.mydukan.activities.MyNetworksActivity;
import org.app.mydukan.data.ChattUser;

import java.util.List;


/**
 * Created by Shivayogi Hiremath on 19/07/2017.
 */

public class SearchProfileList extends RecyclerView.Adapter<SearchProfileList.MyViewHolder>{

    private List<ChattUser> mList;
    private OnClickItemProfile onClickItemProfile;
    public static final String FOLLOW_ROOT = "following";
    public static final String MYFOLLOW_ROOT = "followers";

    public SearchProfileList(List<ChattUser> mList, OnClickItemProfile onClickItemProfile) {
        this.mList = mList;
        this.onClickItemProfile = onClickItemProfile;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_topbar,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ChattUser chattUser = mList.get(position);

        holder.setTvName(chattUser.getName());
        holder.setTvContent( chattUser.getEmail() );
        holder.setTvTime( chattUser.getPhoneNumber() );
        holder.setIvAvatar( chattUser.getPhotoUrl() );
        holder.setTvType( chattUser.getUserType());

       holder.changeFollowing( chattUser.getuId());

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivAvatar;
        private TextView tvName,tvEmail,tvPhoneNum,tvFollow,tvType;
        private Button followBTN;
        private RelativeLayout viewProfile;
        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            ivAvatar = (ImageView)itemView.findViewById( R.id.img_profile );
            tvName = (TextView)itemView.findViewById( R.id.tv_profileName );
            tvEmail = (TextView)itemView.findViewById( R.id.tv_profileEmail );
            tvPhoneNum = (TextView)itemView.findViewById( R.id.tv_profilePhoneNum );
            tvFollow = (TextView)itemView.findViewById( R.id.profile_Followers );
            tvType = (TextView)itemView.findViewById( R.id.profile_Type );
            followBTN = (Button) itemView.findViewById(R.id.btn_follow);
            viewProfile= (RelativeLayout) itemView.findViewById(R.id.layout_vProfile);

            followBTN.setOnClickListener(this);
         //   viewProfile.setOnClickListener(this);
        }

        public void setIvAvatar(String url){
            if (ivAvatar == null)return;
            if (url==null){
                Glide.with(ivAvatar.getContext())
                        .load(R.mipmap.ic_launcher)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50,50)
                        .into( ivAvatar );
            }else
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

       /* public void setIvContent(String url){
            if (tvPhoneNum == null)return;
            *//*Picasso.with(ivContent.getContext()).load(url).placeholder(R.drawable.ic_cast_grey)
                    .resize(400,400) // optional
                    .into(ivContent);*//*
            Glide.with(tvPhoneNum.getContext()).load(url).centerCrop().override(300,300).into(tvPhoneNum);
        }
*/
        public void setTvName(String text){
            if (tvName == null)return;
            tvName.setText( text );
        }
        public void setTvType(String text){
            if (tvType == null)return;
            tvType.setText( text );
        }

        public void setTvTime(String text){
            if (tvEmail == null)return;
            tvEmail.setText(text);
        }

        public void setTvContent(String text){
            if (tvPhoneNum == null)return;
            tvPhoneNum.setText( text );
        }

       /*
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
        }*/

        public void changeFollowing(final String followKey){

            final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
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
                                                          if (dataSnapshot.child(followKey).hasChild(auth.getUid())) {
                                                              // ta curtido
                                                              followBTN.setText("Unfollow");

                                                          } else {
                                                              // nao ta curtido
                                                              followBTN.setText("Follow");
                                                          }
                                                          tvFollow.setText(totalFollowers + " followers");


                                                      }

  //============================================================
         /*
            // followkey is user_id
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
                    tvFollow.setText(totalFollowers+" Followers");
                }*/
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onClick(View view) {
            if (onClickItemProfile != null){
                onClickItemProfile.onClickProfileFeed(getAdapterPosition(),view);
            }
        }
    }
    /**
     * Click item list
     */
    public interface OnClickItemProfile{
        void onClickProfileFeed(int position, View view);
    }

}
