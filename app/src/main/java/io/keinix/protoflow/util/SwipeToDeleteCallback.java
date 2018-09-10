package io.keinix.protoflow.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import javax.inject.Inject;

import io.keinix.protoflow.tasks.TasksAdapter;

public  class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    TasksAdapter mAdapter;

    public SwipeToDeleteCallback(TasksAdapter adapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteTask(position);
    }
}
