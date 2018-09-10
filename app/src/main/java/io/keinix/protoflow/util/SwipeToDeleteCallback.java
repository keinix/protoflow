package io.keinix.protoflow.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import io.keinix.protoflow.R;
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

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int cornerOffset = 20; //so background is behind the rounded corners of itemView
        final ColorDrawable background = new ColorDrawable(Color.RED);

        if (dX > 0) { // Swiping to the right
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + cornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            background.setBounds(itemView.getRight() + ((int) dX) - cornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } // no else so the background will not be drawn at 0

        background.draw(c);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }
}
