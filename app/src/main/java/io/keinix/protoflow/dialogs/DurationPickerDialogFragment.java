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
import butterknife.OnClick;
import io.keinix.protoflow.R;

public class DurationPickerDialogFragment extends DialogFragment {
    @BindView(R.id.number_picker_min) NumberPicker minNumPicker;
    @BindView(R.id.number_picker_hr) NumberPicker hourNumPicker;

    private int mStartMinute;
    private int mStartHour;
    private int mSelectedMinute;
    private int mSelectedHour;
    private DurationPickerInterface mDurationPickerInterface;

    /**
     * used as a callback to handle two {@link NumberPicker}
     * the activity that implements this should call the getters
     * for mSelectedMinute and mSelectedHour
     */
    public interface DurationPickerInterface {
        void durationSet();
    }

    @OnClick(R.id.button_set_duration)
    void setDuration() {
        mDurationPickerInterface.durationSet();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public DurationPickerDialogFragment() {
        mStartMinute = 30;
        mStartHour = 0;
        mDurationPickerInterface = (DurationPickerInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_duration_picker, container, false);
        ButterKnife.bind(this, view);
        initNumPicker();
        initNumPickerListener();
        return view;
    }

    private void initNumPickerListener() {
        minNumPicker.setOnValueChangedListener((numberPicker, oldMin, newMin) ->
                mSelectedMinute = newMin);
        hourNumPicker.setOnValueChangedListener((numberPicker, oldHr, newHr) ->
            mSelectedHour = newHr);
    }

    private void initNumPicker() {
        hourNumPicker.setMaxValue(30);
        hourNumPicker.setMinValue(0);
        minNumPicker.setMaxValue(59);
        minNumPicker.setMinValue(0);
        minNumPicker.setClickable(false);
        hourNumPicker.setClickable(false);
        minNumPicker.setValue(mStartMinute);
        hourNumPicker.setValue(mStartHour);
    }

    public void setStartDuration(int hour, int min) {
        mStartHour = hour;
        mStartMinute = min;
    }

    public int getSelectedMinute() {
        return mSelectedMinute;
    }

    public int getSelectedHour() {
        return mSelectedHour;
    }
}
