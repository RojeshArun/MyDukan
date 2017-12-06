package org.app.mydukan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.NotificationDetailsActivity;
import org.app.mydukan.adapters.NotificationAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Notification;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class NotificationFragment extends android.support.v4.app.Fragment implements NotificationAdapter.NotificationAdapterListener {

    //Ui reference
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    //Variables
    private String mNotificationName;
    private ArrayList<Notification> mNotificationList = new ArrayList<>();
    private NotificationAdapter mNotificationAdapter;
    private MyDukan mApp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_scheme, container, false);
        mApp = (MyDukan) getActivity().getApplicationContext();

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(AppContants.SUPPLIER_NAME)){
            mNotificationName = bundle.getString(AppContants.SUPPLIER_NAME);
        }
        setupSchemeCard(v);
        setTheSchemes();
        return v;
    }

    private void setupSchemeCard(View v) {
        View supplierView = v.findViewById(R.id.schemelayout);
        mNotificationAdapter = new NotificationAdapter(getActivity(), this);

        //setup the recyclerview
        mRecyclerView = (RecyclerView) supplierView.findViewById(R.id.listview);
        mEmptyView = (TextView) supplierView.findViewById(R.id.nodata_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mNotificationAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity().getApplicationContext(), false));
    }

    public void setData(ArrayList<Notification> schemeList){
        mNotificationList = schemeList;
    }

    private void setTheSchemes() {
        if (mNotificationList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        Collections.sort(mNotificationList,new DateComparator());
        Collections.reverse(mNotificationList);
        mNotificationAdapter.addItems(mNotificationList);
        mNotificationAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnClick(int position) {
        Intent intent = new Intent(getActivity(), NotificationDetailsActivity.class);
        intent.putExtra(AppContants.NOTIFICATION, mNotificationList.get(position));
        intent.putExtra(AppContants.SUPPLIER_NAME, mNotificationName);
        startActivity(intent);
    }

    private class DateComparator implements Comparator<Notification> {
        public int compare(Notification n1, Notification n2) {
            return n1.getTimestamp()<n2.getTimestamp()?-1:
                    n1.getTimestamp()>n2.getTimestamp()?1:0;
        }
    }
}
