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
import org.app.mydukan.data.Complaint;
import org.app.mydukan.data.Scheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shivayogi Hiremath on 9/13/16.
 */
public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {
    private List<Complaint> mSchemesList;
    private String mSupplierName;
    private Context mContext;
    private MyDukan mApp;
    private ComplaintsAdapterListener mListener;

    public ComplaintsAdapter(Context context, String supplierName, ComplaintsAdapterListener listener) {
        mContext = context;
        mSchemesList = new ArrayList<Complaint>();
        mSupplierName = supplierName;
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<Complaint> list) {
        mSchemesList.clear();
        mSchemesList.addAll(list);
    }

    @Override
    public ComplaintsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.complaints_listitem, parent, false);
        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ComplaintsAdapter.ViewHolder holder, final int position) {
        Complaint complaint = mSchemesList.get(position);

        holder.mIdView.setText("Id:" + complaint.getComplaintId());
        holder.mNameView.setText("Supplier Name :" + mApp.getUtils().toCamelCase(mSupplierName));
        holder.mDateView.setText("Create Date : " + String.valueOf(mApp.getUtils().dateFormatter(complaint.getCreateddate(), "dd-MM-yy")));
        holder.mStatusView.setText("Status: " + complaint.getStatus());
        if(!mApp.getUtils().isStringEmpty(complaint.getComment())){
            holder.mCommentView.setVisibility(View.VISIBLE);
            holder.mCommentView.setText("Comment:" + complaint.getComment());
        } else {
            holder.mCommentView.setVisibility(View.GONE);
        }

        holder.mComplaintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSchemesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mComplaintLayout;
        private TextView mIdView;
        private TextView mNameView;
        private TextView mDateView;
        private TextView mStatusView;
        private TextView mCommentView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mComplaintLayout = (LinearLayout) itemView.findViewById(R.id.complaintLayout);
            mIdView = (TextView) itemView.findViewById(R.id.complaintNo);
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mDateView = (TextView) itemView.findViewById(R.id.filedDate);
            mStatusView = (TextView) itemView.findViewById(R.id.status);
            mCommentView = (TextView) itemView.findViewById(R.id.comment);
        }
    }

    public interface ComplaintsAdapterListener {
        void OnClick(int position);
    }
}