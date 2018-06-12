package io.keinix.protoflow.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.protoflow.R;

public class DurationPickerDialogFragment extends DialogFragment {
    @BindView(R.id.number_picker_min) NumberPicker minNumPicker;
    @BindView(R.id.number_picker_hr) NumberPicker hourNumPicker;

    private int mStartMinute;
    private int mStartHour;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public DurationPickerDialogFragment() {
        mStartMinute = 30;
        mStartHour = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_duration_picker, container, false);
        ButterKnife.bind(this, view);
        hourNumPicker.setMaxValue(30);
        hourNumPicker.setMinValue(0);
        minNumPicker.setMaxValue(59);
        minNumPicker.setMinValue(0);
        minNumPicker.setClickable(false);
        hourNumPicker.setClickable(false);
        minNumPicker.setValue(mStartMinute);
        hourNumPicker.setValue(mStartHour);
        return view;
    }

    public void setStartDuration(int hour, int min) {
        mStartHour = hour;
        mStartMinute = min;
    }
}
