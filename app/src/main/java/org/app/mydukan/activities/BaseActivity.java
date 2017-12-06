package org.app.mydukan.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.moe.pushlibrary.MoEHelper;

import org.app.mydukan.utils.ProgressSpinner;
import org.app.mydukan.R;

/**
 * Created by arpithadudi on 7/27/16.
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressSpinner mProgress;
    private MoEHelper mHelper;
    public void showProgress() {
        if (!isFinishing()) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = ProgressSpinner.show(this, "", "");
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
        }
    }

    public void dismissProgress() {
        try {
            if (!isFinishing() && mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = null;
        } catch (Exception e) {

        }
    }

    public void showErrorToast(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public void showNetworkConnectionError(Context context) {
        showErrorToast(context, getString(R.string.error_network));
    }

    public static void showOkAlert(Context context, String title, String message, String btnText){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = MoEHelper.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper.onResume(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHelper.onRestoreInstanceState(savedInstanceState);
    }
}
