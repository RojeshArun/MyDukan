package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 9/10/16.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    public List<Notification> mNotificationList;
    private Context mContext;
    public MyDukan mApp;
    public NotificationAdapterListener mListener;

    public NotificationAdapter(Context context, NotificationAdapterListener listener) {
        mContext = context;
        mNotificationList = new ArrayList<Notification>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<Notification> list){
        mNotificationList.clear();
        mNotificationList.addAll(list);
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;

        contactView = inflater.inflate(R.layout.allnotification_listitem, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, final int position) {
        Notification notification = mNotificationList.get(position);

        holder.mTextView.setText(notification.getNotificationText().toUpperCase());
        holder.mMessageView.setText(notification.getMessage());
        if(notification.getTimestamp() != null) {
            holder.mCreateddate.setText(mApp.getUtils().dateFormatter(notification.getTimestamp()));
        }
        holder.mNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mNotificationLayout;
        private TextView mTextView;
        private TextView mMessageView;
        private TextView mCreateddate;


        public ViewHolder(final View itemView) {
            super(itemView);
            mNotificationLayout = (LinearLayout) itemView.findViewById(R.id.notificationlayout);
            mTextView = (TextView) itemView.findViewById(R.id.notificationtext);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mCreateddate = (TextView) itemView.findViewById(R.id.time);
        }
    }

    public interface NotificationAdapterListener {
        void OnClick(int position);
    }
}
