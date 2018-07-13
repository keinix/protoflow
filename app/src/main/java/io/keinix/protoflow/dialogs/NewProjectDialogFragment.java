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
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.keinix.protoflow.R;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class NewProjectDialogFragment extends DialogFragment {

    @BindView(R.id.edit_text_new_project_name) EditText newProjectEditText;
    @BindString(R.string.new_project_no_title_warning) String noTitleString;
    @BindString(R.string.new_project_no_title_toast) String noTitleToastString;
    @BindColor(R.color.errorHintText) int red;

    private Unbinder mUnbinder;

    @OnClick(R.id.button_new_project_ok)
    void onNewProjectCreated() {
        String projectName = newProjectEditText.getText().toString();
        if (checkProjectNameIsNotBack(projectName)) {
            mListener.onProjectCreated(projectName);
            dismiss();
        }
    }

    @OnClick(R.id.button_new_project_cancel)
    void onNewProjectCanceled() {
        dismiss();
    }

    private OnNewProjectCreatedListener mListener;

    public interface OnNewProjectCreatedListener {
        void onProjectCreated(String projectName);
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

    private boolean checkProjectNameIsNotBack(String name) {
        if (name.length() > 0 ) {
            return true;
        } else {
            Toast.makeText(getActivity(), noTitleToastString, Toast.LENGTH_SHORT).show();
            newProjectEditText.setHintTextColor(red);
            newProjectEditText.setHint(noTitleString);
            return false;
        }
    }
}
