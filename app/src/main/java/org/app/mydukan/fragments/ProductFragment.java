package org.app.mydukan.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.app.mydukan.R;
import org.app.mydukan.activities.ProductDescriptionActivity;
import org.app.mydukan.adapters.ProductsAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by shivayogi hiremath on 11-08-2016.
 */
public class ProductFragment extends android.support.v4.app.Fragment implements ProductsAdapter.ProductAdapterListener {
    //Ui reference
    private RecyclerView mProductRecyclerView;
    private TextView mProductEmptyView;
    FloatingActionMenu mFilterFAB;
    FloatingActionButton mViewAllFAB, mAlphaOrderFAB, mPriceDescFAB, mPriceAseFAB, mNewFAB, mPriceDropFAB;
    private SupplierBindData mSupplier;

    //Variables
    private ArrayList<Product> mProductList = new ArrayList<>();
    private ProductsAdapter mProductAdapter;
    private ProductFragmentListener mListener;
    private MyDukan mApp;
    private int tabposition = 0;
    private boolean isCartEnabled = false;
    private boolean  isPriceDropON=false;
    ArrayList<Product> sortedlist_PrivceDrop;
    ArrayList<Product> sortedlist_MRPMONTH;

    LinearLayout addLayout; //cmplnt_lst_add_id
    ScrollView scrollViewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        mApp = (MyDukan) getActivity().getApplicationContext();
        addLayout = (LinearLayout) v.findViewById(R.id.cmplnt_lst_add_id); //cmplnt_lst_add_id
        //FAB
        setupFAB(v);
        setupProductCard(v);

        
        /*mProductRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && mFilterFAB.isShown())
                    mFilterFAB.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    mFilterFAB.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });*/
        MobileAds.initialize(getActivity(), "ca-app-pub-1640690939729824/3964207396");
        AdView cALdview = (AdView) v.findViewById(R.id.adView_MainFragment);//adView_MainFragment
        AdRequest adRequest = new AdRequest.Builder().build();
        cALdview.loadAd(adRequest);

        //initialization of adview in this activity//
        if(mSupplier!=null) {
            if (mSupplier.getId().equalsIgnoreCase("WDSLSgxI10eiWVey4RVWY5niElE3")) {
                addLayout.setVisibility(View.GONE);
            }
        }
        //end of adview mobAds//


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ProductFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ProductFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        if (mBundle.containsKey(AppContants.POSITION)) {
            tabposition = mBundle.getInt(AppContants.POSITION);
        }
    }

    private void setupProductCard(View v) {
        View supplierView = v.findViewById(R.id.supplierlayout);
        mProductAdapter = new ProductsAdapter(getActivity(), this);

        //setup the recyclerview
        mProductRecyclerView = (RecyclerView) supplierView.findViewById(R.id.listview);
        scrollViewList = (ScrollView)supplierView.findViewById(R.id.scrollViewList);
        mProductEmptyView = (TextView) supplierView.findViewById(R.id.nodata_view);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductRecyclerView.setAdapter(mProductAdapter);
        mProductRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity().getApplicationContext(), false));
        fetchProductdata();
    }

    private void setupFAB(View v) {
        mFilterFAB = (FloatingActionMenu) v.findViewById(R.id.filter_action_menu);
        mFilterFAB.setVisibility(View.VISIBLE);
        mFilterFAB.close(true);

        mViewAllFAB = (FloatingActionButton) v.findViewById(R.id.all_fab);
        mAlphaOrderFAB = (FloatingActionButton) v.findViewById(R.id.atozorder_fab);
        mPriceDescFAB = (FloatingActionButton) v.findViewById(R.id.pricedesc_fab);
        mPriceAseFAB = (FloatingActionButton) v.findViewById(R.id.pricease_fab);
        mNewFAB = (FloatingActionButton) v.findViewById(R.id.new_fab);
        mPriceDropFAB = (FloatingActionButton) v.findViewById(R.id.pricedrop_fab);

        mViewAllFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                isPriceDropON=false;
                mProductAdapter.clearProduct();

                mProductAdapter.addProduct(mProductList);
                mProductAdapter.notifyDataSetChanged();
            }
        });

        mAlphaOrderFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                isPriceDropON=false;
                mProductAdapter.clearProduct();

                Collections.sort(mProductList, new NameComparator());

                mProductAdapter.addProduct(mProductList);
                mProductAdapter.notifyDataSetChanged();
            }
        });

        mPriceDescFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                isPriceDropON=false;
                mProductAdapter.clearProduct();
                Collections.sort(mProductList, new PriceComparator());
                mProductAdapter.addProduct(mProductList);
                mProductAdapter.notifyDataSetChanged();
            }
        });

        mPriceAseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                isPriceDropON=false;
                mProductAdapter.clearProduct();

                Collections.sort(mProductList, new PriceComparator());
                Collections.reverse(mProductList);

                mProductAdapter.addProduct(mProductList);
                mProductAdapter.notifyDataSetChanged();
            }
        });

        mNewFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                isPriceDropON=false;
                ArrayList<Product> list = getNewArrivals();
                if (list.isEmpty()) {
                    showOkAlert(getResources().getString(R.string.info), getResources().getString(R.string.error_newproducts),
                            getResources().getString(R.string.ok));
                } else {
                    mProductAdapter.clearProduct();
                    mProductAdapter.addProduct(list);
                    mProductAdapter.notifyDataSetChanged();
                }
            }
        });

    /*
        mPriceDropFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                // ArrayList<Product> list = getPriceDropProducts();
                mProductList=getPriceDropProducts();
                if(mProductList.isEmpty()){
                    showOkAlert(getResources().getString(R.string.info),getResources().getString(R.string.error_pricedrop),
                            getResources().getString(R.string.ok));
                } else {
                    mProductAdapter.clearProduct();
                    Collections.sort(mProductList,new DateComparator());
                    mProductAdapter.addProduct(mProductList);
                    mProductAdapter.notifyDataSetChanged();
                }

            }
        });

*/

        // bug is in this below code after sorting

        mPriceDropFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterFAB.close(true);
                isPriceDropON=true;
               // ArrayList<Product> list = getPriceDropProducts();
                sortedlist_PrivceDrop = getPriceDropProducts();
                if (sortedlist_PrivceDrop.isEmpty()) {
                    showOkAlert(getResources().getString(R.string.info), getResources().getString(R.string.error_pricedrop),
                            getResources().getString(R.string.ok));
                } else {
                    mProductAdapter.clearProduct();
                    Collections.sort(sortedlist_PrivceDrop, new DateComparator());
                    mProductAdapter.addProduct(sortedlist_PrivceDrop);
                    mProductAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    public void setData(ArrayList<Product> list, boolean flag, SupplierBindData nSupplier) {
        isCartEnabled = flag;
        mProductList = list;
        mSupplier=nSupplier;

    }

    private void fetchProductdata() {
        if (mProductList.isEmpty()) {
            mProductEmptyView.setText("There is no model in this Price Range, Swipe to check other categories");
            mProductEmptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            mProductEmptyView.setVisibility(View.GONE);
        }

        if (mProductAdapter != null) {
            mProductAdapter.clearProduct();
        }

        mProductAdapter.setCartFlag(isCartEnabled);


        mProductAdapter.addProduct(mProductList);
        mProductAdapter.notifyDataSetChanged();
        sortByMOnths();
    }

    private void sortByMOnths( ) {
        sortedlist_MRPMONTH =mProductList;
        if (sortedlist_MRPMONTH.isEmpty()) {

        } else {
            mProductAdapter.clearProduct();
            Collections.sort(sortedlist_MRPMONTH, new mrpMonthComparator());
            mProductAdapter.addProduct(sortedlist_MRPMONTH);
            mProductAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnProductOpenClick(int position, String type) {
        if (type.equalsIgnoreCase(mProductAdapter.VIEW)) {
            Intent intent = new Intent(getActivity(), ProductDescriptionActivity.class);
            intent.putExtra(AppContants.PRODUCT, mProductList.get(position));
            intent.putExtra(AppContants.SUPPLIER, mSupplier);
            startActivity(intent);
        } else if (type.equalsIgnoreCase(mProductAdapter.ADD_CART)) {
            mListener.addProductToCart(mProductList.get(position));
        }
    }

    @Override
    public void OnProductClaimClick(int position) {
        if (isPriceDropON){
            mListener.addProductToClaim(sortedlist_PrivceDrop.get(position));
        }else{
            mListener.addProductToClaim(mProductList.get(position));
        }
       // mListener.addProductToClaim(mProductList.get(position));
    }

    private class PriceComparator implements Comparator<Product> {

        public int compare(Product p1, Product p2) {
            if (mApp.getUtils().isStringEmpty(p1.getPrice())) {
                p1.setPrice("0");
            }

            if (mApp.getUtils().isStringEmpty(p2.getPrice())) {
                p2.setPrice("0");
            }

            int price1 = Integer.valueOf(p1.getPrice());
            int price2 = Integer.valueOf(p2.getPrice());

            if (price1 > price2) {
                return 1;
            } else if (price1 < price2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public class DateComparator implements Comparator<Product> {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public String convertTime(long time) {
            Date date = new Date(time);
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            return format.format(date);
        }

        public int compare(Product p1, Product p2) {
            //descending getPriceDrop().getStartdate();
            long mdate1 = p1.getPriceDrop().getStartdate();
            long mdate2 = p2.getPriceDrop().getStartdate();
            Date d1 = null;
            Date d2 = null;
            d1 = new Date(mdate1);
            d2 = new Date(mdate2);
            return d1.compareTo(d2) * -1;

            //descendi
        }
    }

    public class mrpMonthComparator implements Comparator<Product> {

        public int compare(Product p1, Product p2) {

            if(p1.getMrp()==null || p1.getMrp().isEmpty()){
                p1.setMrp("0");
            }
            if(p2.getMrp()==null || p2.getMrp().isEmpty()){
                p2.setMrp("0");
            }
            Integer  pMRP1= Integer.valueOf(p1.getMrp());
            Integer  pMRP2= Integer.valueOf(p2.getMrp());
            //descending getPriceDrop().getStartdate();
            return pMRP1.compareTo(pMRP2) * -1;

        }
    }

    private class NameComparator implements Comparator<Product> {
        public int compare(Product p1, Product p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }


    private ArrayList<Product> getNewArrivals() {
        ArrayList<Product> result = new ArrayList<Product>();
        for (Product product : mProductList) {
            if (product.getIsnew()) {
                result.add(product);
            }
        }
        return result;
    }


    private ArrayList<Product> getPriceDropProducts() {
        ArrayList<Product> result = new ArrayList<Product>();
        for (Product product : mProductList) {
            if (product.getPriceDrop() != null) {
                result.add(product);
            }
        }
        return result;
    }


    public void showOkAlert(String title, String message, String btnText) {
        if (getActivity() != null) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btnText,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    public interface ProductFragmentListener {
        void addProductToCart(Product product);

        void addProductToClaim(Product product);
    }

}


