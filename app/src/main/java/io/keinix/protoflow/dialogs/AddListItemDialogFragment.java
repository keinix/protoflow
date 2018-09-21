package io.keinix.protoflow.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.keinix.protoflow.R;
import io.keinix.protoflow.adapters.AddListItemAdapter;
import io.keinix.protoflow.util.ListItem;

public class AddListItemDialogFragment extends DialogFragment {


    public interface OnListItemSelectedListener {
        void onTaskSelected(int taskId);
        void onRoutineSelected(int routineId);
    }

    @BindView(R.id.text_view_dialog_recyclerview) TextView titleTextView;
    @BindView(R.id.recycler_view_project_in_picker) RecyclerView recyclerView;

    private Unbinder mUnbinder;
    private AddListItemAdapter mAdapter;
    private String mTitle;

    public AddListItemDialogFragment() {
        mAdapter = new AddListItemAdapter((OnListItemSelectedListener) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_picker, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        titleTextView.setText(mTitle);
        mAdapter.setListener((OnListItemSelectedListener) getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void setListItems(List<? extends ListItem> listItems) {
        mAdapter.setListItems(listItems);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
