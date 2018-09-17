package io.keinix.protoflow.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.keinix.protoflow.R;
import io.keinix.protoflow.adapters.ProjectPickerAdapter;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class ProjectPickerDialogFragment extends DialogFragment {

    @BindView(R.id.recycler_view_project_in_picker) RecyclerView mRecyclerView;
    @BindView(R.id.text_view_dialog_recyclerview) TextView mTitleTextView;
    @BindString(R.string.new_project_title) String newProjectString;

    private ProjectPickerAdapter mAdapter;

    private ProjectPickerAdapter.OnProjectSelectedListener mListener;

    private Unbinder mUnbinder;

    private String mTitle;

    public ProjectPickerDialogFragment() {
        mTitle = newProjectString;
        mAdapter = new ProjectPickerAdapter(mListener);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_picker, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mTitleTextView.setText(mTitle);
        mListener = (ProjectPickerAdapter.OnProjectSelectedListener) getActivity();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void setProjects(List<Project> projects) {
        mAdapter.setProjects(projects);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
