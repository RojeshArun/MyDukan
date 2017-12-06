package org.app.mydukan.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dinesh
 */
//Extending FragmentStatePagerAdapter
public class ProductPagerFragment extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    boolean isCartEnabled;
    ArrayList<ArrayList<Product>> mProductList;
    private SupplierBindData mSupplier;
    //Constructor to the class
    public ProductPagerFragment(FragmentManager fm, ArrayList<ArrayList<Product>> list, boolean isCartEnabled, SupplierBindData mSupplier) {
        super(fm);
        //Initializing tab count
        this.isCartEnabled = isCartEnabled;
        this.tabCount = list.size();
        this.mProductList = list;
        this.mSupplier= mSupplier;

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        ProductFragment myfrag = new ProductFragment();
        Bundle product = new Bundle();
        product.putInt(AppContants.POSITION, position);
        myfrag.setArguments(product);
        myfrag.setData(mProductList.get(position),isCartEnabled,mSupplier);
        return myfrag;
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}