package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 10/13/16.
 */

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {
    private List<Record> mRecordsList;
    private Context mContext;
    private MyDukan mApp;
    private RecordsAdapter.RecordsAdapterListener mListener;

    public RecordsAdapter(Context context,RecordsAdapter.RecordsAdapterListener listener) {
        mContext = context;
        mRecordsList = new ArrayList<Record>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<Record> list) {
        mRecordsList.clear();
        mRecordsList.addAll(list);
    }

    @Override
    public RecordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.records_listitem, parent, false);

        // Return a new holder instance
        return new RecordsAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RecordsAdapter.ViewHolder holder, final int position) {
        Record record = mRecordsList.get(position);

        holder.mNameView.setText(mApp.getUtils().toCamelCase(record.getProductname()));
        holder.mQuantityView.setText(mContext.getString(R.string.quantity) + ":" +
               record.getRecordList().size());

        long amount = 0l;
        try {
            for (RecordInfo recordInfo: record.getRecordList()) {
                amount += Long.valueOf(recordInfo.getPrice());
            }
        }catch (Exception e){

        }
        holder.mAmountView.setText("Total Amount: " + mApp.getUtils().getPriceFormat(String.valueOf(amount)));


        holder.mRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecordsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRecordLayout;
        private TextView mNameView;
        private TextView mQuantityView;
        private TextView mAmountView;
        private Button mDeleteBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            mRecordLayout = (RelativeLayout) itemView.findViewById(R.id.recorditem);
            mQuantityView = (TextView) itemView.findViewById(R.id.quantity);
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mAmountView = (TextView) itemView.findViewById(R.id.amount);
            mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
        }
    }

    public interface RecordsAdapterListener {
        void OnClick(int position);
        void OnDeleteClick(int position);
    }
}
