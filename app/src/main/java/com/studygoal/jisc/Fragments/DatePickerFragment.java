package com.studygoal.jisc.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.studygoal.jisc.Utils.Utils;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

    public LogLogActivity fragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), null, year, month, day);
        dialog.getDatePicker().init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int y, int m, int d) {
                fragment.date.setText(Utils.formatDate(y, m, d));
                fragment.date.setTag(y + "-" + ((m+1)<10?"0"+(m+1):(m+1)) + "-" + (d<10?"0"+d:d));
            }
        });
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;
    }
}