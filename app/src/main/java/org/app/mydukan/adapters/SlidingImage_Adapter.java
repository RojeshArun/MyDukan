package org.app.mydukan.adapters;

/**
 * Created by Shivu on 04-07-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.picasso.Picasso;

import org.app.mydukan.R;
import org.app.mydukan.activities.PaytmGatewayActivity;
import org.app.mydukan.appSubscription.PriceDropSubscription;
import org.app.mydukan.data.ImageModel;
import org.app.mydukan.data.User;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.Utils;

import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {


    private ArrayList<ImageModel> imageModelArrayList;
    private LayoutInflater inflater;
    private Context context;
    private User userdetails;
    private String userID;
    int[] imgHolders={R.drawable.img_holder_mydukan,R.drawable.img_holder_mydukan,R.drawable.img_holder_mydukan,R.drawable.img_holder_mydukan,R.drawable.img_holder_mydukan};
    int count=0;

    public SlidingImage_Adapter(Context context, ArrayList<ImageModel> imageModelArrayList, User mUserdetails, String mId) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        this.userdetails = mUserdetails;
        this.userID = mId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final LinearLayout layoutImg = (LinearLayout) imageLayout.findViewById(R.id.bannerView);
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        this.setupClickListener(layoutImg, position);
/*
       Glide.with(context)
                .load(imageModelArrayList.get(position).getImage_drawable())
                .centerCrop()
                //.transform(new CircleTransform(MyProfileActivity.this))
                .override(300,200)
                .placeholder(R.drawable.img_holder)
                .into(imageView);

                    Picasso.with(context).load(imageModelArrayList.get(position).getImage_drawable())
                .resize(1000,680)
                //.centerCrop()
                .placeholder(R.drawable.img_holder)                 // optional
                .into(imageView);
                */
        Picasso.with(context).load(imageModelArrayList.get(position).getImage_drawable())
                .resize(600,180)
                //.centerCrop()
                .placeholder(R.drawable.img_holder)                 // optional
                .into(imageView);


        if(imgHolders.length>4){
        count=0;
        }
        // imageView.setImageResource(imageModelArrayList.get(position).getImage_drawable());
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    private void setupClickListener(final LinearLayout view, final int position) {

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Page is clicked!
                Answers.getInstance().logCustom(new CustomEvent("PaytmButton click")
                        .putCustomAttribute("USER_ID/ USER_Email:", userID + "/" + userdetails.getUserinfo().getEmailid()));
                PriceDropSubscription priceDropSubscription=new PriceDropSubscription();
                boolean isSubScried = priceDropSubscription.checkSubscription(context, userdetails);
                if(!isSubScried){
                    Intent nIntent = new Intent(context, PaytmGatewayActivity.class);
                    nIntent.putExtra(AppContants.FP_USER_DETAILS, userdetails);
                    nIntent.putExtra(AppContants.FP_USER_ID, userdetails.getId());
                    context.startActivity(nIntent);
                }
            }
        });

    }
}