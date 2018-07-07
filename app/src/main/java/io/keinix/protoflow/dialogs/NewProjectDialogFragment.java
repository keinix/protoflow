package io.keinix.protoflow.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.keinix.protoflow.R;

public class NewProjectDialogFragment extends DialogFragment {

    private Unbinder mUnbinder;

    @OnClick(R.id.button_new_project_ok)
    void onNewProjectCreated() {
        mListener.onProjectCreated();
        dismiss();
    }

    @OnClick(R.id.button_new_project_cancel)
    void onNewProjectCanceled() {
        dismiss();
    }

    private OnNewProjectCreatedListener mListener;

    public interface OnNewProjectCreatedListener {
        void onProjectCreated();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_project, container, false);
        mUnbinder =  ButterKnife.bind(this, view);
        mListener = (OnNewProjectCreatedListener) getActivity();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
