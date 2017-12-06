package org.app.mydukan.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.app.mydukan.data.Notification;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class NotificationsPagerFragment extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    String supplierName;
    ArrayList<ArrayList<Notification>> mNotificationList;

    public NotificationsPagerFragment(FragmentManager fm, String supplierName, ArrayList<ArrayList<Notification>> list ) {
        super(fm);
        this.tabCount = list.size();
        this.mNotificationList = list;
        this.supplierName = supplierName;
    }

    @Override
    public Fragment getItem(int position) {
        NotificationFragment notificationFragment = new NotificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppContants.SUPPLIER_NAME, supplierName);
        notificationFragment.setArguments(bundle);
        notificationFragment.setData(mNotificationList.get(position));
        return notificationFragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public void setData(ArrayList<ArrayList<Notification>> list){
        mNotificationList.clear();
        mNotificationList.addAll(list);
        tabCount = mNotificationList.size();
    }
}
