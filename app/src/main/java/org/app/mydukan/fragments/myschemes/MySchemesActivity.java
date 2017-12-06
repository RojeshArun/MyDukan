package org.app.mydukan.fragments.myschemes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import org.app.mydukan.R;
import org.app.mydukan.activities.Schemes.SchemeListActivity;
import org.app.mydukan.fragments.myschemes.fragmetns.DashBoardFragment;

public class MySchemesActivity extends AppCompatActivity {

    // Fragment Variablse
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private RadioButton mMySchemesTab, mSchemesTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schemes_fragment);

        // Set My Schemes Tab
        mMySchemesTab = (RadioButton) findViewById(R.id.tab_myschemes);
        mSchemesTab = (RadioButton) findViewById(R.id.tab_schemes);

        mSchemesTab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gotoSchemesFragment();
                }
            }
        });
        setupActionBar();
        setDashBoardFragment();
    }

    private void gotoSchemesFragment() {/*
        Intent schemesInent = new Intent(MySchemesActivity.this,SchemeListActivity.class);
        startActivity(schemesInent);*/
        finish();
    }

    private void setDashBoardFragment() {
        DashBoardFragment fragment = DashBoardFragment.newInstance();
        addFragment(fragment, false);
    }


    public void addFragment(Fragment frag, boolean hasToAdd) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, frag);
        if (hasToAdd)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.myschemes_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
