package org.app.mydukan.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.NotificationData;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

/**
 * Created by arpithadudi on 9/30/16.
 */

public class NotificationsActivity extends BaseActivity {

    //UI reference
    private RecyclerView mRecyclerView;
    private TextView mNoDataView;

    //Variables
    private MyDukan mApp;
    private FirebaseRecyclerAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);

        mApp = (MyDukan) getApplicationContext();

        //setup actionbar
        setupActionBar();
        setupListView();
        setupTheAdapter();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.notifications_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListView() {
        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        mNoDataView.setText("No Notifications");
        mNoDataView.setVisibility(View.GONE);

        //setup the recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.listview);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                NotificationsActivity.this.getApplicationContext(), false));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupTheAdapter(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notifications").child(mApp.getFirebaseAuth().getCurrentUser().getUid());
        mAdapter = new FirebaseRecyclerAdapter<NotificationData,ViewHolder>(
                NotificationData.class,R.layout.notification_listitem,
                ViewHolder.class,ref) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, NotificationData model, int position) {
                viewHolder.mMessageView.setText(model.getMessage());
                viewHolder.mTimeView.setText(String.valueOf(mApp.getUtils().dateFormatter(model.getTimestamp(), "dd-MM-yy")));
                viewHolder.mDeleteBtn.setTag(getRef(position).getKey());
                viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ref.child((String)view.getTag()).removeValue();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(mAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mMessageView;
        private TextView mTimeView;
        private Button mDeleteBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mTimeView = (TextView) itemView.findViewById(R.id.time);
            mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
