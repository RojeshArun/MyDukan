package org.app.mydukan.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.utils.AppContants;

import java.util.List;

/**
 * Created by Shivu on 31-07-2017.
 */

public class CustomBaseAdapter extends BaseAdapter {
    Context context;
    List<ChattUser> rowItems;
    String profileType;
    String myProfileId;
    private MyDukan mApp;
    public static final String FOLLOW_ROOT = "following";
    public static final String MYFOLLOW_ROOT = "followers";
    public CustomBaseAdapter(Context context, List<ChattUser> items, String type_Profile, String id_profile) {
        this.context = context;
        this.rowItems = items;
        this.profileType=type_Profile;
        this.myProfileId=id_profile;
        mApp = (MyDukan) context.getApplicationContext();
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle,txtRemove;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_layout, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtRemove = (TextView) convertView.findViewById(R.id.tv_Remove);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

         final ChattUser rowItem = (ChattUser) getItem(position);

        holder.txtTitle.setText(rowItem.getName());
        holder.txtDesc.setText(mApp.getUtils().toCamelCase(rowItem.getUserType()));
       // holder.imageView.(rowItem.getPhotoUrl());   mApp.getUtils().toCamelCase(rowItem.getUserType() )
        if( rowItem.getPhotoUrl()!=null)
        {
            Glide.with(context)
                    .load( rowItem.getPhotoUrl() )
                    .centerCrop()
                    .transform(new CircleTransform(context))
                    .override(50,50)
                    .into(holder.imageView);
        }else{
            Glide.with(context)
                    .load( R.mipmap.ic_launcher )
                    .centerCrop()
                    .transform(new CircleTransform(context))
                    .override(50,50)
                    .into(holder.imageView);
        }
        if(profileType.equals( AppContants.MYPROFILE_FOLLOW)){
            holder.txtRemove.setVisibility(View.VISIBLE);
        }
        holder.txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFollowing(myProfileId, rowItem.getuId());
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    private static void removeFollowing(final String idProfile, final String removeId) {
        //  showProgress(true);
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
        final DatabaseReference referenceIFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
        //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid()+"/"+feed.getIdUser());
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceIFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(idProfile).hasChild(removeId)) {
                    referenceFollow.child(removeId).child(idProfile).removeValue();//removing userid to following list  .
                    referenceIFollow.child(idProfile).child(removeId).removeValue();//removing the user id to distributor following list.
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //showProgress(false);
                int i =20;
            }
        });

    }

}