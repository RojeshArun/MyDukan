package org.app.mydukan.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by rojesharunkumar on 19/11/17.
 */

public class BaseFragment extends Fragment {

    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
    }

    public void showProgress() {
        progressDialog.show();
    }

    public void hideProgress(){
        progressDialog.hide();
    }

}
