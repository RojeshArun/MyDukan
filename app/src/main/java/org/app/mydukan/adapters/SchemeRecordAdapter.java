package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.SchemeRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 11/18/16.
 */

public class SchemeRecordAdapter extends RecyclerView.Adapter<SchemeRecordAdapter.ViewHolder> {
    private List<SchemeRecord> mRecordsList;
    private Context mContext;
    private MyDukan mApp;
    //private SchemeRecordAdapter.RecordsAdapterListener mListener;

    public SchemeRecordAdapter(Context context) {
        mContext = context;
        mRecordsList = new ArrayList<SchemeRecord>();
        mApp = (MyDukan) mContext.getApplicationContext();
        //mListener = listener;
    }

    public void addItems(ArrayList<SchemeRecord> list) {
        mRecordsList.clear();
        mRecordsList.addAll(list);
    }

    @Override
    public SchemeRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.schemerecord_listitem, parent, false);

        // Return a new holder instance
        return new SchemeRecordAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(SchemeRecordAdapter.ViewHolder holder, final int position) {
        SchemeRecord record = mRecordsList.get(position);

        if(!mApp.getUtils().isStringEmpty(record.getVoucherno())){
            holder.mVoucherView.setText("Voucher no: " + record.getVoucherno());
        } else{
            holder.mVoucherView.setVisibility(View.GONE);
        }

        if(record.getDate() != 0){
            holder.mDateView.setText(String.valueOf(mApp.getUtils().dateFormatter(
                    record.getDate(), "dd-MM-yy")));
        } else {
            holder.mDateView.setVisibility(View.GONE);
        }

        holder.mNameView.setText(record.getSchemeinfo().getName());

        if(!mApp.getUtils().isStringEmpty(record.getEarnings())){
            holder.mAmountView.setText("Total Amount: " + record.getEarnings());
        } else{
            holder.mAmountView.setVisibility(View.GONE);
        }

        if(!mApp.getUtils().isStringEmpty(record.getSettledby())){
            holder.mSettledByView.setText("Settled by: " +record.getSettledby());
        } else {
            holder.mSettledByView.setVisibility(View.GONE);
        }

        if(record.getSettled()){
            holder.mStatusView.setText("Settled");
            holder.mStatusView.setTextColor(mContext.getResources().getColor(R.color.green_500));
        } else{
            holder.mStatusView.setText("Not Settled");
            holder.mStatusView.setTextColor(mContext.getResources().getColor(R.color.colorBtnBackground));
        }
    }

    @Override
    public int getItemCount() {
        return mRecordsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mSchemeRecordLayout;
        private TextView mVoucherView;
        private TextView mDateView;
        private TextView mNameView;
        private TextView mAmountView;
        private TextView mSettledByView;
        private TextView mStatusView;

        //private Button mDeleteBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            mSchemeRecordLayout = (LinearLayout) itemView.findViewById(R.id.schemerecorditem);
            mVoucherView = (TextView) itemView.findViewById(R.id.voucherID);
            mDateView = (TextView) itemView.findViewById(R.id.dateID);
            mNameView = (TextView) itemView.findViewById(R.id.nameID);
            mAmountView = (TextView) itemView.findViewById(R.id.amountID);
            mSettledByView = (TextView) itemView.findViewById(R.id.settledbyID);
            mStatusView = (TextView) itemView.findViewById(R.id.statusID);

            //mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
        }
    }

//    public interface RecordsAdapterListener {
//        void OnClick(int position);
//        void OnDeleteClick(int position);
//    }
}
