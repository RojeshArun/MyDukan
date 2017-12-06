
package org.app.mydukan.fragments.myschemes.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Scheme;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rojesharunkumar on 20/10/17.
 */


public class BrandsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<List<Scheme>> brandsList;
    private IBrandsItemHolderClick onClick;


    public BrandsAdapter(Fragment allSchemesFragments) {
        brandsList = new ArrayList<>();
        onClick = (IBrandsItemHolderClick) allSchemesFragments;

    }

    public interface IBrandsItemHolderClick {
        void onItemClick(int pos);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.brands_item, parent, false);
        return new BrandsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BrandsViewHolder brandsViewHolder = (BrandsViewHolder) holder;
        List<Scheme> brands = brandsList.get(position);
        brandsViewHolder.txtBrandTitle.setText(brands.get(0).getCategory());
        brandsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return brandsList.size();
    }

    public void notifyDataSetChanged(List<List<Scheme>> mBrandsList) {
        if (mBrandsList != null) {
            this.brandsList = mBrandsList;
        }
        notifyDataSetChanged();
    }

    public static class BrandsViewHolder extends RecyclerView.ViewHolder {
        TextView txtBrandTitle;

        public BrandsViewHolder(View itemView) {
            super(itemView);
            txtBrandTitle = (TextView) itemView.findViewById(R.id.txt_brand_title);
        }
    }

}

