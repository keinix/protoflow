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

import java.lang.reflect.UndeclaredThrowableException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.keinix.protoflow.R;
import io.keinix.protoflow.di.ActivityScope;

/**
 * Activities that show this Dialog must implement {@link onDurationSetListener}
 */
@ActivityScope
public class DurationPickerDialogFragment extends DialogFragment {
    @BindView(R.id.number_picker_min) NumberPicker minNumPicker;
    @BindView(R.id.number_picker_hr) NumberPicker hourNumPicker;

    private int mStartMinute;
    private int mStartHour;
    private int mSelectedMinute;
    private int mSelectedHour;
    private Unbinder mUnbinder;
    private onDurationSetListener mOnDurationSetListener;

    /**
     * used as a callback to handle two {@link NumberPicker}
     * the activity that implements this should call the getters
     * for mSelectedMinute and mSelectedHour
     */
    public interface onDurationSetListener {
        void onDurationSet();
    }

    @OnClick(R.id.button_set_duration)
    void setDuration() {
        mOnDurationSetListener.onDurationSet();
        dismiss();
    }

    @OnClick(R.id.button_cancel_duration)
    void cancelDuration() {
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public DurationPickerDialogFragment() {
        mStartHour = 0;
        mStartMinute = 30;
        mSelectedHour = 0;
        mSelectedMinute = 30;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_duration_picker, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mOnDurationSetListener = (onDurationSetListener) getActivity();
        initNumPicker();
        initNumPickerListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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

    private void initNumPickerListener() {
        minNumPicker.setOnValueChangedListener((numberPicker, oldMin, newMin) ->
                mSelectedMinute = newMin);
        hourNumPicker.setOnValueChangedListener((numberPicker, oldHr, newHr) ->
                mSelectedHour = newHr);
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
