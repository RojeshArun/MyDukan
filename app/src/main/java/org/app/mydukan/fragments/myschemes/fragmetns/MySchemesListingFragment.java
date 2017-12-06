
package org.app.mydukan.fragments.myschemes.fragmetns;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.wooplr.spotlight.utils.Utils;

import org.app.mydukan.R;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.fragments.myschemes.adapter.GridSpacingItemDecoration;
import org.app.mydukan.fragments.myschemes.adapter.MySchemesAdapter;

import java.io.Serializable;
import java.util.List;


/**
 * Created by rojesharunkumar on 06/11/17.
 */


public class MySchemesListingFragment extends Fragment
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    private RecyclerView mBrandsRecycleView;
    private MySchemesActivity mActivity;
    private MySchemesAdapter mAdapter;
    private List<Scheme> mSchemesList;
    private Button btnAdd;


    public static MySchemesListingFragment newInstance(List<Scheme> schemeList) {

        Bundle args = new Bundle();
        args.putSerializable("schemes", (Serializable) schemeList);
        MySchemesListingFragment fragment = new MySchemesListingFragment();
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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSchemesList = (List<Scheme>) getArguments().getSerializable("schemes");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schemes_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnAdd.setText("Select My Schemes");
        btnAdd.setOnClickListener(this);
        mBrandsRecycleView = (RecyclerView) view.findViewById(R.id.lst_schemes);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        mAdapter = new MySchemesAdapter(mSchemesList);
        mBrandsRecycleView.setLayoutManager(layoutManager);
        mBrandsRecycleView.addItemDecoration(
                new GridSpacingItemDecoration(2, Utils.dpToPx(10), true));
        mBrandsRecycleView.setAdapter(mAdapter);
        if (mSchemesList != null) {
            mAdapter.notifyDataSetChanged(mSchemesList);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
              //  saveMySelectedSchemes();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        gotoAddSchemeFragment();
        return super.onOptionsItemSelected(item);

    }

    private void gotoAddSchemeFragment() {
        /*CreateSchemeFragment createSchemeFragment = CreateSchemeFragment.newInstance();
        mActivity.addFragment(createSchemeFragment,true);
    */}
}

