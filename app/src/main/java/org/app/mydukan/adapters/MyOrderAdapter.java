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
import org.app.mydukan.data.Category;
import org.app.mydukan.data.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 10/7/16.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    private List<Order> mDataList;
    private Context mContext;
    private MyDukan mApp;
    private MyOrderAdapter.OrderAdapterListener mListener;

    public MyOrderAdapter(Context context, String supplierName, MyOrderAdapter.OrderAdapterListener listener) {
        mContext = context;
        mDataList = new ArrayList<Order>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<Order> list) {
        mDataList.clear();
        mDataList.addAll(list);
    }

    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.myorder_item, parent, false);

        // Return a new holder instance
        return new MyOrderAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(MyOrderAdapter.ViewHolder holder, final int position) {
        Order myorder = mDataList.get(position);

        holder.mOrderidView.setText(": "+myorder.getOrderId());
        holder.mQuantity.setText(String.valueOf(myorder.getProductList().size()));
        holder.mGrandtotal.setText(": Rs."+ myorder.getOrderInfo().getTotalamount());
        holder.mStatusview.setText(": "+myorder.getOrderInfo().getStatus());

        if(!mApp.getUtils().isStringEmpty(myorder.getOrderInfo().getExecutionstatus())){
            holder.mExeStatusLayout.setVisibility(View.VISIBLE);
            holder.mExecutionStatusview.setText(": "+myorder.getOrderInfo().getExecutionstatus());
        } else {
            holder.mExeStatusLayout.setVisibility(View.GONE);
        }

        holder.mOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mOrderLayout;
        private LinearLayout mExeStatusLayout;
        private TextView mOrderidView;
        private TextView mQuantity;
        private TextView mGrandtotal;
        private TextView mStatusview;
        private TextView mExecutionStatusview;

        public ViewHolder(final View itemView) {
            super(itemView);
            mOrderLayout = (LinearLayout) itemView.findViewById(R.id.orderLayout);
            mExeStatusLayout = (LinearLayout) itemView.findViewById(R.id.executionStatusLayout);
            mOrderidView = (TextView) itemView.findViewById(R.id.orderid);
            mQuantity = (TextView) itemView.findViewById(R.id.totalQty);
            mGrandtotal = (TextView) itemView.findViewById(R.id.totalamount);
            mStatusview = (TextView) itemView.findViewById(R.id.status);
            mExecutionStatusview  = (TextView) itemView.findViewById(R.id.executionStatus);
        }
    }

    public interface OrderAdapterListener {
        void OnClick(int position);
    }
}
