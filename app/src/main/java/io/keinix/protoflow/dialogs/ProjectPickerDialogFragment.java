package io.keinix.protoflow.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.keinix.protoflow.R;
import io.keinix.protoflow.adapters.ProjectPickerAdapter;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class ProjectPickerDialogFragment  extends DialogFragment {

    @Inject ProjectPickerAdapter mAdapter;

    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_duration_picker, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
