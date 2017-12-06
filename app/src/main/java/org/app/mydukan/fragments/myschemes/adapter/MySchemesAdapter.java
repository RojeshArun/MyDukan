package org.app.mydukan.fragments.myschemes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Scheme;

import java.util.List;


/**
 * Created by rojesharunkumar on 20/10/17.
 */

public class MySchemesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Scheme> schemeList;

    public MySchemesAdapter(List<Scheme> mSchemesList) {
        schemeList = mSchemesList;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.my_scheme_item, parent, false);
        return new SchemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SchemeViewHolder schemeViewHolder = (SchemeViewHolder) holder;
        Scheme scheme = schemeList.get(position);

        schemeViewHolder.txtTitle.setText(scheme.getName());


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    public void notifyDataSetChanged(List<Scheme> mSchemesList) {
        this.schemeList = mSchemesList;
        notifyDataSetChanged();
    }

    public static class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;

        public SchemeViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_scheme_title);

        }
    }
}
