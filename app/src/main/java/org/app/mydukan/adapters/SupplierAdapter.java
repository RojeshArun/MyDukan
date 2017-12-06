package org.app.mydukan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.data.User;
import org.app.mydukan.utils.AppContants;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shivayogi Hiremath on 11-08-2016.
 */
public class SupplierAdapter extends
        RecyclerView.Adapter<SupplierAdapter.ViewHolder> {
    //Variables
    public List<Supplier> mSupplierData;
    private Context mContext;
    public Supplier currentSupplier;
    public MyDukan mApp;
    public supplierAdapterListener mListener;
    public int supplierViewType = 1; //1 for UserSupplier, 2 for all supplier

    public SupplierAdapter(Context context, int suppliertype, supplierAdapterListener supplierListener) {
        mContext = context;
        mSupplierData = new ArrayList<Supplier>();
        supplierViewType = suppliertype;
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = supplierListener;
    }

    public void clearsupplier() {
        mSupplierData.clear();
    }

    public void addsupplier(ArrayList<Supplier> supplier) {
        mSupplierData.addAll(supplier);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;

        contactView = inflater.inflate(R.layout.supplier_card, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        currentSupplier = mSupplierData.get(position);

        String name = mApp.getUtils().toCamelCase(currentSupplier.getCompanyinfo().getName());
        holder.mSupplierName.setText(name);
        if(mSupplierData.get(position).getId().equalsIgnoreCase("WDSLSgxI10eiWVey4RVWY5niElE3")){
            holder.mSupplierName.setTextColor(Color.parseColor("#400000"));
            holder.mSupplierName.setTypeface(Typeface.DEFAULT_BOLD);
            holder.mSupplierName.setText("MOBILE DP PRIME");
        }

        if(mSupplierData.get(position).getId().equalsIgnoreCase("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2")){
            holder.mSupplierName.setText("MOBILE DP FREE");
        }

        if (currentSupplier.getUserinfo().getName().contains(AppContants.MOBILE_DP)) {
            holder.mSupplierimg.setImageResource(R.drawable.ic_mobiledp);
          //  holder.mSupplierName.setTypeface(Typeface.DEFAULT_BOLD);
        } else if (currentSupplier.getUserinfo().getName().contains(AppContants.AIRTEL)) {
            holder.mSupplierimg.setImageResource(R.drawable.ic_airtel);
        } else if (currentSupplier.getUserinfo().getName().contains(AppContants.ASUS)) {
            holder.mSupplierimg.setImageResource(R.drawable.ic_ic_store_black_24dp);
        } else {
            holder.mSupplierimg.setImageResource(R.drawable.ic_ic_store_black_24dp);
        }

        if (supplierViewType == 1) {
            holder.mAddSupplierbtn.setVisibility(View.GONE);
            holder.mOpenbtn.setVisibility(View.VISIBLE);
            if(currentSupplier.getRetailerStatus().equals("pending")) {
                holder.mOpenbtn.setVisibility(View.VISIBLE);
                holder.mAddSupplierbtn.setVisibility(View.GONE);
                holder.mOpenbtn.setText("PENDING");
                holder.mOpenbtn.setBackgroundColor(android.graphics.Color.rgb(128,128,128));
            }else if(currentSupplier.getRetailerStatus().equals("accepted")){
                holder.mAddSupplierbtn.setVisibility(View.GONE);
                holder.mOpenbtn.setVisibility(View.VISIBLE);
                holder.mOpenbtn.setText("OPEN");
                holder.mOpenbtn.setBackgroundColor(mApp.getResources().getColor(R.color.green_500));
                holder.mSupplierLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.OnSupplierOpenClick(position);
                        }
                    }
                });
                holder.mSupplierName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.OnSupplierOpenClick(position);
                        }
                    }
                });
                holder.mOpenbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.OnSupplierOpenClick(position);
                        }
                    }
                });
                holder.mSupplierimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.OnSupplierOpenClick(position);
                        }
                    }
                });
            } else if(currentSupplier.getRetailerStatus().equals("add")){
                holder.mOpenbtn.setVisibility(View.GONE);
                holder.mAddSupplierbtn.setVisibility(View.VISIBLE);
                holder.mAddSupplierbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if (mListener != null) {
                        mListener.OnSupplierAddClick(position);
                    }
                    }
                });
            }else if(currentSupplier.getRetailerStatus().equals("rejected")){
                holder.mOpenbtn.setVisibility(View.VISIBLE);
                holder.mAddSupplierbtn.setVisibility(View.GONE);
                holder.mOpenbtn.setText("REJECTED");
                holder.mOpenbtn.setBackgroundColor(android.graphics.Color.rgb(128,128,128));
            }
        } else if (supplierViewType == 2) {
            holder.mAddSupplierbtn.setVisibility(View.VISIBLE);
            holder.mOpenbtn.setVisibility(View.GONE);

            holder.mSupplierLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.OnSupplierAddClick(position);
                    }
                }
            });
            holder.mSupplierName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.OnSupplierAddClick(position);
                    }
                }
            });
            holder.mAddSupplierbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.OnSupplierAddClick(position);
                    }
                }
            });
            holder.mSupplierimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.OnSupplierAddClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSupplierData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout mSupplierLayout;
        private TextView mSupplierName;
        private Button mAddSupplierbtn;
        private Button mOpenbtn;
        private ImageView mSupplierimg;
        private CardView mSupplierLayout;

        public ViewHolder(final View itemView) {
            super(itemView);
//            mSupplierLayout = (LinearLayout) itemView.findViewById(R.id.supplierlayoutId);
            mSupplierLayout = (CardView) itemView.findViewById(R.id.supplierlayoutId);
            mSupplierName = (TextView) itemView.findViewById(R.id.supplierFullName);
            mAddSupplierbtn = (Button) itemView.findViewById(R.id.addorsubscribebtn);
            mOpenbtn = (Button) itemView.findViewById(R.id.openbtn);
            mSupplierimg = (ImageView) itemView.findViewById(R.id.supplierImage);
        }
    }

    public interface supplierAdapterListener {
        void OnSupplierAddClick(int position);

        void OnSupplierOpenClick(int position);
    }

}
