package org.app.mydukan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.adapters.SearchProfileList;
import org.app.mydukan.data.ChattUser;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class Search_MyNetworkActivity extends AppCompatActivity implements SearchProfileList.OnClickItemProfile,View.OnClickListener {

    private static final int RESULT_PICK_CONTACT = 13;

    private ProgressBar mProgressBar;
    private RecyclerView recyclerView;
    TextView searchMobNum;
    Button searchBTN, searchContact_BTN;
    ImageView back_BTN;//back_BTN

    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final String MYFOLLOW_ROOT = "followers";

    private boolean flagFollow;
    private ArrayList<ChattUser> chattUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mynetwork);


        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        searchMobNum = (TextView) findViewById(R.id.searchMobNum);
        searchContact_BTN = (Button) findViewById(R.id.button_Search_ContactList);
        searchBTN = (Button) findViewById(R.id.button_Search);
        back_BTN= (ImageView) findViewById(R.id.back_BTN);
        initViews();
       /// SearchSuggestaionData();

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String mobNum= String.valueOf(searchMobNum.getText());
                if(mobNum.length()!= 10){
                    Toast.makeText(Search_MyNetworkActivity.this, "Please enter 10 digit valid number", Toast.LENGTH_SHORT).show();
                    return;
                }
            if(!mobNum.isEmpty()){
                mobNum="+91"+mobNum;
                SearchUserData(mobNum);
                }
            }
        });
        back_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchContact_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact(v);
            }
        });


      /*
      ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200);
        //config.setContentTextColor(Color.parseColor("#400000"));
        config.setDismissTextColor(Color.parseColor("#64DD17"));
        config.setMaskColor(Color.parseColor("#dc4b4b4b"));

        // half second between each showcase view  Mask colors #EA80FC   #D81B60  #009688  #E0E0E0

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1010_id");
        sequence.setConfig(config);
        sequence.addSequenceItem(mWhatsAppBtn, "Having trouble reach us on whatsapp", "NEXT");
        sequence.start();

       */
    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("ContactView", "Failed to pick contact");
        }
    }
    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
//            textView1.setText(name);

            phoneNo = cursor.getString(phoneIndex);
            searchMobNum.setText(phoneNo.replace("+91","").replace(" ", "" ));
            String mobnum = searchMobNum.getText().toString().trim();
            if(!mobnum.isEmpty()){
                mobnum="+91"+mobnum;
                SearchUserData(mobnum);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }
    //========Dfault search For Mobile Numbers From the User Contact List===================================================================================
    private void SearchSuggestaionData() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //   Toast.makeText(Search_MyNetworkActivity.this, "User Contact List: " + phoneNumber, Toast.LENGTH_SHORT).show();
            //  Toast.makeText(S
           // showProgress(true);
            DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("chat_USER");
            feedReference.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        chattUsers = new ArrayList<>();
                 /*   ChattUser chattUser = dataSnapshot.getValue(ChattUser.class);
                    Toast.makeText(Search_MyNetworkActivity.this, "User: "+chattUser.getName()+" "+chattUser.getEmail(), Toast.LENGTH_SHORT).show();
                   */
                        for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                            ChattUser chattUser = dataSnapshots.getValue(ChattUser.class);
                            chattUsers.add(chattUser);
                        }
                        initRecyclerView(chattUsers);
                        //  Toast.makeText(Search_MyNetworkActivity.this, "User: "+chattUsers.get(0).getName()+" "+chattUsers.get(0).getEmail(), Toast.LENGTH_SHORT).show();
                    }else {
                      //  Toast.makeText(Search_MyNetworkActivity.this, "User Profile Not found for entered Mobile Number or enter the Mobile Number is Not exists" , Toast.LENGTH_SHORT).show();
                    }
                  //showProgress(false);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Search_MyNetworkActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
        phones.close();
    }

    //============================================================================================
    private void SearchUserData(String mobNum ) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
      /*  while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
         //   Toast.makeText(Search_MyNetworkActivity.this, "User Contact List: " + phoneNumber, Toast.LENGTH_SHORT).show();
            phones.close();
        }
*/

        showProgress(true);
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference("chat_USER");
        feedReference.orderByChild("phoneNumber").equalTo(mobNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chattUsers = new ArrayList<>();
                 /*   ChattUser chattUser = dataSnapshot.getValue(ChattUser.class);
                    Toast.makeText(Search_MyNetworkActivity.this, "User: "+chattUser.getName()+" "+chattUser.getEmail(), Toast.LENGTH_SHORT).show();
                   */
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        ChattUser chattUser = dataSnapshots.getValue(ChattUser.class);
                        chattUsers.add(chattUser);
                    }
                    showProgress(false);
                    initRecyclerView(chattUsers);

                   // Toast.makeText(Search_MyNetworkActivity.this, "User: "+chattUsers.get(0).getName()+" "+chattUsers.get(0).getEmail(), Toast.LENGTH_SHORT).show();
                }else {
                    showProgress(false);
                    Toast.makeText(Search_MyNetworkActivity.this, "User Profile Not found for entered Mobile Number or enter the Mobile Number is Not exists" , Toast.LENGTH_SHORT).show();
                }
                showProgress(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Search_MyNetworkActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });
    }

    /**
     * Init data in RecyclerView
     *
     * @param list
     */
    private void initRecyclerView(List<ChattUser> list) {
        recyclerView.setAdapter(new SearchProfileList(list,this));
    }

    /**
     * Bind views XML with JavaAPI
     */
    private void initViews(){
        mProgressBar = (ProgressBar)  findViewById(R.id.pb);
        //    FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.mCatId.fab);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_feed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {

    }
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Intent intent = new Intent(Search_MyNetworkActivity.this, MyNetworksActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickProfileFeed(int position, View view) {
        ChattUser chattUser = chattUsers.get(position);
        switch (view.getId()) {
           /* case R.mCatId.iv_like:  // Like_BTN
                addLike(feed);
                break;*/
            case R.id.btn_follow:  // follow_BTN
                addFollow(chattUser);
                break;

        }
    }



    private void addFollow(final ChattUser chattUser) {
        showProgress(true);
        flagFollow = true;
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT);
        final DatabaseReference referenceIFollow = FirebaseDatabase.getInstance().getReference().child(MYFOLLOW_ROOT);
        //  final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference(MyNetworkActivity.FOLLOW_ROOT+"/"+auth.getUid()+"/"+feed.getIdUser());
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flagFollow) {
                    if (dataSnapshot.child(auth.getUid()).hasChild(chattUser.getuId())) {
                        referenceFollow.child(auth.getUid()).child(chattUser.getuId()).removeValue();//removing userid to following list  .
                        referenceIFollow.child(chattUser.getuId()).child(auth.getUid()).removeValue();//removing the user mCatId to distributor following list.
                       // btn_Follow.setText("Follow");
                        flagFollow = false;
                        showProgress(false);
                    } else {
                        referenceFollow.child(auth.getUid()).child(chattUser.getuId()).setValue(true);//adding userid to following list  .
                        referenceIFollow.child(chattUser.getuId()).child(auth.getUid()).setValue(true);//adding the user mCatId to distributor following list.
                      //  btn_Follow.setText("UnFollow");
                        flagFollow = false;
                        showProgress(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });

    }
}
