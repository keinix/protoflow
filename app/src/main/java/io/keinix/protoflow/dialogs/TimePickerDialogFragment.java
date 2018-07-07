package io.keinix.protoflow.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

import butterknife.Unbinder;

public class TimePickerDialogFragment extends DialogFragment {

    private int mStartHour;
    private int mStartMinute;

    public TimePickerDialogFragment() {
        Calendar calendar = Calendar.getInstance();
        mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
        mStartMinute = calendar.get(Calendar.MINUTE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                (TimePickerDialog.OnTimeSetListener) getActivity(),
                mStartHour, mStartMinute, DateFormat.is24HourFormat(getActivity()));
    }

    public void setStartTime(int hour, int minute) {
        mStartHour = hour;
        mStartMinute = minute;
    }
}
