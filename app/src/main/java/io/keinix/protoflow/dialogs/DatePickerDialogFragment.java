package io.keinix.protoflow.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class DatePickerDialogFragment extends DialogFragment {

    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        mStartYear = calendar.get(Calendar.YEAR);
        mStartMonth = calendar.get(Calendar.MONTH);
        mStartDay = calendar.get(Calendar.DAY_OF_MONTH);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener) getActivity(),
                mStartYear, mStartMonth, mStartDay);
    }

    public void setStartDate(int year, int month, int day) {
        mStartYear = year;
        mStartMonth = month;
        mStartDay = day;
    }

}
