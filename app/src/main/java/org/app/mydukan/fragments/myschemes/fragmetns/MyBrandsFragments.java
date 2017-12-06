
package org.app.mydukan.fragments.myschemes.fragmetns;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wooplr.spotlight.utils.Utils;

import org.app.mydukan.R;
import org.app.mydukan.activities.MainActivity;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.fragments.BaseFragment;
import org.app.mydukan.fragments.SchemeFragment;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.fragments.myschemes.adapter.BrandsAdapter;
import org.app.mydukan.fragments.myschemes.adapter.GridSpacingItemDecoration;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rojesharunkumar on 06/11/17.
 */


public class MyBrandsFragments extends BaseFragment implements BrandsAdapter.IBrandsItemHolderClick {

    DatabaseReference mySchemsRef;
    // List<Brands> mySelectedList;
    private MySchemesActivity mActivity;
    private BrandsAdapter mAdapter;
    private RecyclerView mBrandsRecycleView;
    ValueEventListener listener;

    List<List<Scheme>> mySelectedSchemes;

    public static MyBrandsFragments newInstance() {

        Bundle args = new Bundle();

        MyBrandsFragments fragment = new MyBrandsFragments();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MySchemesActivity) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySelectedSchemes = MySelectedSchemesHelper.getInstance().getAllSelectedSchemesList();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schemes_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       mBrandsRecycleView = (RecyclerView) view.findViewById(R.id.lst_schemes);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        mAdapter = new BrandsAdapter(this);
        mBrandsRecycleView.setLayoutManager(layoutManager);
        mBrandsRecycleView.addItemDecoration(
                new GridSpacingItemDecoration(2, Utils.dpToPx(10), true));
        mBrandsRecycleView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mySelectedSchemes);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
      /*  if (mActivity != null) {
            //    mActivity.dismissProgress();
            mySchemsRef.removeEventListener(listener);
        }*/
    }


    @Override
    public void onItemClick(int pos) {
          gotoMySchemesFragment(mySelectedSchemes.get(pos));
    }

    private void gotoMySchemesFragment(List<Scheme> brand) {
        MySchemesListingFragment frag = MySchemesListingFragment.newInstance(brand);
        mActivity.addFragment(frag, true);
    }
}

