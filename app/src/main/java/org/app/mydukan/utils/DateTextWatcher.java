package org.app.mydukan.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.app.mydukan.R;

/**
 * Created by arpithadudi on 11/18/16.
 */

public class DateTextWatcher implements TextWatcher {

    private EditText mEditText;
    private Context mContext;
    private boolean mProcess;

    public DateTextWatcher(Context context, EditText editText) {
        super();
        mContext = context;
        mEditText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mProcess = (count == 0) ? false : true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String currDate = s.toString();
        int currLength = currDate.length();
        boolean dateError = false;
        boolean monthError = false;
        if (!mProcess) {
            return;
        }

        if (currLength == 2) {
            String date = currDate.substring(0, 2);
            try {
                int dateInt = Integer.parseInt(date);
                if ((dateInt < 1) || (dateInt > 31)) {
                    dateError = true;
                }
            } catch (NumberFormatException e) {
                dateError = true;
            }
            if (dateError) {
                mEditText.setError(mContext.getString(R.string.error_dateday));
            } else {
                s.append("/");
            }
        } else if (currLength == 5) {
            String month = currDate.substring(3, 5);
            try {
                int monthInt = Integer.parseInt(month);
                if ((monthInt < 1) || (monthInt > 12)) {
                    monthError = true;
                }
            } catch (NumberFormatException e) {
                monthError = true;
            }
            if (monthError) {
                mEditText.setError(mContext.getString(R.string.error_datemonth));
            } else {
                s.append("/");
            }
        }
    }
}
