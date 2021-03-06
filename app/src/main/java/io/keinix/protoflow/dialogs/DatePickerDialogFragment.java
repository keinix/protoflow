package io.keinix.protoflow.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Unbinder;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class DatePickerDialogFragment extends DialogFragment {

    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;

    public DatePickerDialogFragment() {
        Calendar calendar = Calendar.getInstance();
        mStartYear = calendar.get(Calendar.YEAR);
        mStartMonth = calendar.get(Calendar.MONTH);
        mStartDay = calendar.get(Calendar.DAY_OF_MONTH);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(),
                mStartYear, mStartMonth, mStartDay);
    }

    public void setStartDate(int year, int month, int day) {
        mStartYear = year;
        mStartMonth = month;
        mStartDay = day;
    }

    public void setStartDate(long startDateUtc) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDateUtc);
        mStartYear = calendar.get(Calendar.YEAR);
        mStartMonth = calendar.get(Calendar.MONTH);
        mStartDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Call setStartDate() before calling this method
     * @return Formatted String for current Start Date
     */
    public String getStartDateTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mStartYear, mStartMonth, mStartDay, 0, 0, 0);
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
    }

    public String getStartDateTimeStampWithDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mStartYear, mStartMonth, mStartDay, 0, 0, 0);
        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    }

    public static String getStartDateTimeStampWithDay(long startDateUtx) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDateUtx);
        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    }

    /**
     * Used as the primary key in CalendarDayDatabase
     * @return current start Date in Millis
     */
    public long getStartDateUtc() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mStartYear, mStartMonth, mStartDay, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
