package io.keinix.protoflow.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import io.keinix.protoflow.R;
import io.keinix.protoflow.adapters.ProjectPickerAdapter;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class ProjectPickerDialogFragment extends DaggerFragment {

    @BindView(R.id.recycler_view_project_in_picker) RecyclerView mRecyclerView;

    @Inject ProjectPickerAdapter mAdapter;

    @Inject ProjectPickerAdapter.OnProjectSelectedListener mListener;

    private Unbinder mUnbinder;

    @Inject
    public ProjectPickerDialogFragment() {
        mAdapter = new ProjectPickerAdapter(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_duration_picker, container, false);
        mUnbinder = ButterKnife.bind(this, view);
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
}
