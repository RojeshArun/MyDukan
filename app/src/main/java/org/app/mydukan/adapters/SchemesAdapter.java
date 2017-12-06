package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Scheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 9/10/16.
 */
public class SchemesAdapter extends RecyclerView.Adapter<SchemesAdapter.ViewHolder> {
    public List<Scheme> mSchemesList;
    private Context mContext;
    public MyDukan mApp;
    public SchemesAdapterListener mListener;

    public SchemesAdapter(Context context, SchemesAdapterListener listener) {
        mContext = context;
        mSchemesList = new ArrayList<>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<Scheme> list){
        mSchemesList.clear();
        mSchemesList.addAll(list);
    }

    @Override
    public SchemesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;

        contactView = inflater.inflate(R.layout.schemes_listitem, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(SchemesAdapter.ViewHolder holder, final int position) {
        Scheme scheme = mSchemesList.get(position);

        holder.mNameView.setText(scheme.getName().toUpperCase());
        holder.mDescriptionView.setText("Click for more details");
        holder.mValidityView.setText("Validity "
                + String.valueOf("From: "+mApp.getUtils().dateFormatter(scheme.getStartdate(), "dd-MM-yy")) + " to "
                + String.valueOf(mApp.getUtils().dateFormatter(scheme.getEnddate(), "dd-MM-yy")));

        holder.mSchemeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });
        holder.mEnrolled.setOnCheckedChangeListener(null);
        holder.mEnrolled.setChecked(scheme.isHasEnrolled());

        holder.mEnrolled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.OnEnrolled(mSchemesList.get(position),position,isChecked);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mSchemesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mSchemeLayout;
        private TextView mNameView;
        private TextView mDescriptionView;
        private TextView mValidityView;
        private CheckBox mEnrolled;

        public ViewHolder(final View itemView) {
            super(itemView);
            mSchemeLayout = (RelativeLayout) itemView.findViewById(R.id.schemelayout);
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mDescriptionView = (TextView) itemView.findViewById(R.id.description);
            mValidityView = (TextView) itemView.findViewById(R.id.validitiy);
            mEnrolled = (CheckBox) itemView.findViewById(R.id.chk_enrolled);
        }
    }

    public interface SchemesAdapterListener {
        void OnClick(int position);

        void OnEnrolled(Scheme scheme,int pos,boolean isChecked);
    }
}
