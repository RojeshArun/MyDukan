package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.OrderProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 9/10/16.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderProduct> mOrderList;
    private Context mContext;
    private MyDukan mApp;
    private OrderAdapterListener mListener;
    private String mViewtype;

    public final String VIEW = "VIEW";
    public final String EDIT = "EDIT";

    public OrderAdapter(Context context, OrderAdapterListener listener) {
        mContext = context;
        mOrderList = new ArrayList<OrderProduct>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void setViewtype(String viewType) {
        this.mViewtype = viewType;
    }

    public void addItems(ArrayList<OrderProduct> list){
        mOrderList.clear();
        mOrderList.addAll(list);
    }
    public void clearItems(){
        mOrderList.clear();
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;

        contactView = inflater.inflate(R.layout.order_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final OrderAdapter.ViewHolder holder, final int position) {
        OrderProduct myorder = mOrderList.get(position);
        holder.mDisplayLayout.setVisibility(View.VISIBLE);
        holder.mEditLayout.setVisibility(View.GONE);

        holder.mNameView.setText(mApp.getUtils().toCamelCase(myorder.getProductname()));
        holder.mQuantityView.setText(mApp.getResources().getString(R.string.quantity) + ":" +String.valueOf(myorder.getQuantity()));
        holder.mPriceView.setText(mApp.getUtils().getPriceFormat(String.valueOf(myorder.getPrice())));
        holder.mEditBtn.setTag(myorder.getQuantity());
        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mEditLayout.setVisibility(View.VISIBLE);
                holder.mQuantityEditView.setText(String.valueOf(view.getTag()));
            }
        });

        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnDeleteClick(position);
            }
        });

        holder.mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noOfItems = holder.mQuantityEditView.getText().toString();
                mListener.OnEditDone(position,noOfItems);
            }
        });

        if(mViewtype.equals(VIEW)){
            holder.mEditBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mDisplayLayout;
        private RelativeLayout mEditLayout;
        private TextView mNameView;
        private TextView mPriceView;
        private TextView mQuantityView;
        private EditText mQuantityEditView;
        private Button mEditBtn;
        private Button mOkBtn;
        private Button mDeleteBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            mDisplayLayout = (RelativeLayout) itemView.findViewById(R.id.displayLayout);
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mPriceView = (TextView) itemView.findViewById(R.id.price);
            mQuantityView = (TextView) itemView.findViewById(R.id.quantity);
            mEditBtn = (Button) itemView.findViewById(R.id.editBtn);

            mEditLayout = (RelativeLayout) itemView.findViewById(R.id.editLayout);
            mQuantityEditView = (EditText) itemView.findViewById(R.id.quantityEditText);
            mOkBtn = (Button) itemView.findViewById(R.id.okBtn);
            mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
        }
    }

    public interface OrderAdapterListener {
        void OnDeleteClick(int position);
        void OnEditDone(int position, String quantity);
    }
}
