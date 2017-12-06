package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class RecordInfoAdapter extends RecyclerView.Adapter<RecordInfoAdapter.ViewHolder> {
    private List<RecordInfo> mRecordsList;
    private Context mContext;
    private MyDukan mApp;
    private RecordsInfoAdapterListener mListener;


    public RecordInfoAdapter(Context context,RecordsInfoAdapterListener listener) {
        mContext = context;
        mRecordsList = new ArrayList<RecordInfo>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<RecordInfo> list) {
        mRecordsList.clear();
        mRecordsList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recordinfo_listitem, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RecordInfo record = mRecordsList.get(position);

        holder.mIMEIView.setText(record.getImei());
        holder.mAmountView.setText( mApp.getUtils().getPriceFormat(String.valueOf(record.getPrice())));
        setupStatusSpinner(holder.mStatusSpinner);
       if(record.getStatus().equalsIgnoreCase("claim")){
            holder.mStatusSpinner.setSelection(0);
        } else {
            holder.mStatusSpinner.setSelection(1);
        }

       if(record.getStatus().equalsIgnoreCase("claim") || record.getStatus().equalsIgnoreCase("Settled by Distributor")){
            holder.checkSetteled.setChecked(true);
           holder.checkSetteled.setText("Settled by Distributor");
        } else {
            holder.checkSetteled.setChecked(false);
           holder.checkSetteled.setText("Pending by Distributor");
        }


        holder.mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mStatus;
                if( holder.checkSetteled.isChecked()){
                  mStatus="Settled by Distributor";
                    holder.checkSetteled.setText(mStatus);
                }else{
                    mStatus="Pending by Distributor";
                    holder.checkSetteled.setText(mStatus);
                }
                mListener.OnUpdateClick(position,mStatus);
               // mListener.OnUpdateClick(position,holder.mStatusSpinner.getSelectedItem().toString());
            }
        });

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnDeleteClick(position);
            }
        });
    }

    private void setupStatusSpinner(Spinner spinner){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.record_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mRecordsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRecordLayout;
        private TextView mIMEIView;
        private TextView mAmountView;
        private Spinner mStatusSpinner;
        private Button mDeleteBtn;
        private Button mUpdateBtn;
        private CheckBox checkSetteled;
        public ViewHolder(final View itemView) {
            super(itemView);
            mRecordLayout = (RelativeLayout) itemView.findViewById(R.id.recorditem);
            mIMEIView = (TextView) itemView.findViewById(R.id.imei);
            mAmountView = (TextView) itemView.findViewById(R.id.amount);
            mStatusSpinner = (Spinner) itemView.findViewById(R.id.statusSpinner);
            checkSetteled =(CheckBox) itemView.findViewById(R.id.checkBox1);

            mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
            mUpdateBtn = (Button) itemView.findViewById(R.id.updateBtn);
        }
    }

    public interface RecordsInfoAdapterListener {
        void OnUpdateClick(int position, String status);
        void OnDeleteClick(int position);
    }
}
