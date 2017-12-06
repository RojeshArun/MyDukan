package org.app.mydukan.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by arpithadudi on 11/18/16.
 */


public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;
    private  final int DEFAULT_DAY = 1;
    private  final int DEFAULT_MONTH = 0;
    private  final int DEFAULT_YEAR = 2016;

    public DatePickerFragment() {
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = DEFAULT_YEAR;
        int month = DEFAULT_MONTH;
        int day = DEFAULT_DAY;
        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
    }
}
