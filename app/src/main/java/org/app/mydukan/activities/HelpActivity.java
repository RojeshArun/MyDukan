package org.app.mydukan.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import org.app.mydukan.R;

/**
 * Created by arpithadudi on 9/6/16.
 */
public class HelpActivity extends BaseActivity {

    TextView mVersionTextView;
    TextView mHelpLineTextView;
    Button button_PrivacyPolicy, youtubeVideo;
    private static String TAG;
    String version;
    int verCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setupActionBar();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            verCode = pInfo.versionCode;
            int i= 20;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        mVersionTextView = (TextView) findViewById(R.id.version);
        mHelpLineTextView = (TextView) findViewById(R.id.helpno);
        button_PrivacyPolicy = (Button) findViewById(R.id.btn_PrivacyPolicy);
        youtubeVideo = (Button) findViewById(R.id.btn_youtube);
        youtubeVideo.setVisibility(View.GONE);

        youtubeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });

        button_PrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
                //   PaytmOrderInfo paytmOrderInfo= new PaytmOrderInfo();
                // paytmOrderInfo=VolleyApiManager.useData(HelpActivity.this,"RetDig03944840906164","4c45bb65-13a1-46c9-b6a8-889de6e6ad6a");

            }
        });

        mVersionTextView.setText("Version: "+String.format(String.valueOf(verCode)));

        mHelpLineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactExists(HelpActivity.this, "+919036770772")) {
                    sendwhatsapp();
                } else if (!contactExists(HelpActivity.this, "+919036770772")) {
                    showProgress();
                    addcontact();
                }
            }
        });
    }


    /**
     * This function sets up the actionbar for the screen
     */
    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("About Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    public void sendwhatsapp() {
        Uri uri = Uri.parse("smsto:" + "+919036770772");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    public boolean contactExists(Context context, String number) {
        // number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public void addcontact() {
        try {
            Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
            contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

            contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, "MyDukan")
                    .putExtra(ContactsContract.Intents.Insert.PHONE, "+919036770772");
            startActivityForResult(contactIntent, 1);

        } catch (Exception e) {
            dismissProgress();
            Log.i("Cannot add", " cannot add");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        dismissProgress();
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                showErrorToast(HelpActivity.this, "Contact Added Successfully");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                showErrorToast(HelpActivity.this, "Cancelled Add Contact");
            }
        }
    }

}
