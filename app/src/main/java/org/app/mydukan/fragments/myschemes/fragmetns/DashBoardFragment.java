package org.app.mydukan.fragments.myschemes.fragmetns;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.app.mydukan.R;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;


/**
 * Created by rojesharunkumar on 06/11/17.
 */

public class DashBoardFragment extends Fragment implements View.OnClickListener {

    RelativeLayout btnCalculator, btnMySchemes, btnEarnings;
    private MySchemesActivity mActivity;
    private Fragment fragment;

    public static DashBoardFragment newInstance() {

        Bundle args = new Bundle();
        DashBoardFragment fragment = new DashBoardFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCalculator = (RelativeLayout) view.findViewById(R.id.btn_calculator);
        btnCalculator.setOnClickListener(this);

        btnMySchemes = (RelativeLayout) view.findViewById(R.id.btn_my_schemes);
        btnMySchemes.setOnClickListener(this);


        btnEarnings = (RelativeLayout) view.findViewById(R.id.btn_earnings);
        btnEarnings.setOnClickListener(this);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        //   mActivity.clearStack();

        switch (view.getId()) {
            case R.id.btn_calculator:
                loadCalculatorFragment();
                break;
            case R.id.btn_my_schemes:
                loadMySchemesFragment();
                break;
            case R.id.btn_earnings:
                loadMyEarnings();
                break;
            default:
                break;
        }
    }


    private void loadMyEarnings() {
      /*  fragment = EarningsFragments.newInstance();
        mActivity.addFragment(fragment, true);
*/
    }

    private void loadMySchemesFragment() {
        fragment = MyBrandsFragments.newInstance();
        mActivity.addFragment(fragment, true);
    }

    private void loadSchemesFragment() {
       /* fragment = AllBrandsFragments.newInstance();
        mActivity.addFragment(fragment, true);
*/
    }

    private void loadCalculatorFragment() {
       /* CalculatorForm calculatorForm = CalculatorForm.newInstance();
        mActivity.addFragment(calculatorForm, true);
    */
    }
}
