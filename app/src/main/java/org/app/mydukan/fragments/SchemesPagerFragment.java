package org.app.mydukan.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.app.mydukan.data.Product;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.fragments.myschemes.fragmetns.MySelectedSchemesHelper;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class SchemesPagerFragment extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    String supplierName;
    String supplierId;
    ArrayList<ArrayList<Scheme>> mSchemeList;

    public SchemesPagerFragment(FragmentManager fm, String supplierName, String supplierId,
                                ArrayList<ArrayList<Scheme>> list ) {
        super(fm);
        this.tabCount = list.size();
        this.mSchemeList = list;
        this.supplierName = supplierName;
        this.supplierId = supplierId;
    }

    @Override
    public Fragment getItem(int position) {
        SchemeFragment schemeFragment = new SchemeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppContants.SUPPLIER_NAME, supplierName);
        bundle.putString(AppContants.SUPPLIER_ID, supplierId);
        schemeFragment.setArguments(bundle);
        schemeFragment.setData(mSchemeList.get(position),position);
        return schemeFragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public void setData(ArrayList<ArrayList<Scheme>> list){
        mSchemeList.clear();
        mSchemeList.addAll(list);
        tabCount = mSchemeList.size();
        MySelectedSchemesHelper.getInstance().setAllProductsList(mSchemeList);

    }
}
