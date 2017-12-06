package org.app.mydukan.fragments;

import android.content.Context;
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
import org.app.mydukan.activities.Schemes.SchemeDetailsActivity;
import org.app.mydukan.activities.Schemes.SchemeListActivity;
import org.app.mydukan.adapters.SchemesAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeInfo;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.data.SupplierInfo;
import org.app.mydukan.fragments.myschemes.fragmetns.MySelectedSchemesHelper;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class SchemeFragment extends BaseFragment implements SchemesAdapter.SchemesAdapterListener {

    //Ui reference
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    //Variables
    private String mSupplierId;
    private String mSupplierName;
    private ArrayList<Scheme> mSchemeList = new ArrayList<>();
    private SchemesAdapter mSchemesAdapter;
    private MyDukan mApp;

    private SchemeRecord mSchemeRecord;
    private ArrayList<SchemeRecord> mRecordList;
    private SchemeListActivity mActivity;
    private int brandPos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (SchemeListActivity) context;
        mActivity.showProgress();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fetch Enrolled Schemes

        mRecordList = MySelectedSchemesHelper.getInstance().getRecordList();

        if (mSchemeList != null && mRecordList != null) {
            MySelectedSchemesHelper.getInstance().updateMySelectedList(mRecordList);
            // Compare SchemesList and  Records List
            for (int i = 0; i < mRecordList.size(); i++) {
                for (int j = 0; j < mSchemeList.size(); j++) {
                    if (mRecordList.get(i).getSchemeinfo().getId().equals(mSchemeList.get(j).getSchemeId())) {
                        mSchemeList.get(j).setHasEnrolled(true);
                        // Any change update item TODO
                        // MySelectedSchemesHelper.getInstance().updateAt(mSchemeList,pos);
                    }
                }
            }
            if (mSchemesAdapter != null) {
                Collections.sort(mSchemeList, new DateComparator());
                Collections.reverse(mSchemeList);
                mSchemesAdapter.addItems(mSchemeList);
                mSchemesAdapter.notifyDataSetChanged();

            }

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scheme, container, false);
        mApp = (MyDukan) getActivity().getApplicationContext();

        Bundle bundle = getArguments();
        if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
            mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
        }
        if (bundle != null && bundle.containsKey(AppContants.SUPPLIER_NAME)) {
            mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
        }
        setupSchemeCard(v);
        setTheSchemes();
        return v;
    }

    private void setupSchemeCard(View v) {
        View supplierView = v.findViewById(R.id.schemelayout);
        mSchemesAdapter = new SchemesAdapter(getActivity(), this);

        //setup the recyclerview
        mRecyclerView = (RecyclerView) supplierView.findViewById(R.id.listview);
        mEmptyView = (TextView) supplierView.findViewById(R.id.nodata_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mSchemesAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity().getApplicationContext(), false));
    }

    public void setData(ArrayList<Scheme> schemeList, int pos) {
        mSchemeList = schemeList;
        this.brandPos = pos;
    }

    private void setTheSchemes() {
        if (mSchemeList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        Collections.sort(mSchemeList, new DateComparator());
        Collections.reverse(mSchemeList);

        mSchemesAdapter.addItems(mSchemeList);
        mSchemesAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnClick(int position) {
        Intent intent = new Intent(getActivity(), SchemeDetailsActivity.class);
        intent.putExtra(AppContants.SCHEME, mSchemeList.get(position));
        intent.putExtra(AppContants.SUPPLIER_NAME, mSupplierName);
        intent.putExtra(AppContants.SUPPLIER_ID, mSupplierId);
        startActivity(intent);
    }

    @Override
    public void OnEnrolled(Scheme scheme, final int pos, boolean isChecked) {
        //Show Progress Bar
        mActivity.showProgress();
        getSchemeRecordInfo(scheme, pos,isChecked);
        mSchemeList.get(pos).setHasEnrolled(isChecked);
    }

    private void getSchemeRecordInfo(final Scheme scheme, final int pos, final boolean isChecked) {
        ApiManager.getInstance(getActivity()).getSchemeRecord(scheme.getSchemeId(),
                mSupplierId, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data != null) {
                            mSchemeRecord = (SchemeRecord) data;
                        } else {
                            mSchemeRecord = new SchemeRecord();
                            SupplierInfo info = new SupplierInfo();
                            info.setId(mSupplierId);
                            info.setName(mSupplierName);
                            mSchemeRecord.setSupplierinfo(info);

                            SchemeInfo schemeInfo = new SchemeInfo();
                            schemeInfo.setId(scheme.getSchemeId());
                            schemeInfo.setName(scheme.getName());
                            mSchemeRecord.setSchemeinfo(schemeInfo);
                        }

                        if (mSchemeRecord.getEnrolled() != true) {
                            mSchemeRecord.setEnrolled(true);
                            addSchemeRecord(mSchemeRecord, pos);
                        }
                    // Update My Selected List TODO
                    MySelectedSchemesHelper.getInstance().addSchemeAt(brandPos,pos,isChecked);

                    }

                    @Override
                    public void onFailure(String response) {
                        if (mActivity != null)
                            mActivity.dismissProgress();

                    }
                });
    }


    private void addSchemeRecord(SchemeRecord record, final int pos) {
        ApiManager.getInstance(getActivity()).addSchemeRecord(record, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                String result = (String) data;
                if (!result.equalsIgnoreCase(getString(R.string.status_success))) {
                    mSchemeRecord.setEnrolled(!mSchemeRecord.getEnrolled());
                } else {
                    if (mSchemeRecord.getEnrolled()) {
                        mSchemeList.get(pos).setHasEnrolled(true);
                    } else {
                        mSchemeList.get(pos).setHasEnrolled(false);
                    }
                }
                if (mActivity != null)
                    mActivity.dismissProgress();
            }

            @Override
            public void onFailure(String response) {
                if (mActivity != null)
                    mActivity.dismissProgress();

            }
        });

    }

    private class DateComparator implements Comparator<Scheme> {
        public int compare(Scheme s1, Scheme s2) {
            return s1.getStartdate() < s2.getStartdate() ? -1 :
                    s1.getStartdate() > s2.getStartdate() ? 1 : 0;
        }
    }
}
