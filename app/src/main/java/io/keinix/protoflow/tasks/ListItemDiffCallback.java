package io.keinix.protoflow.tasks;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.List;

import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.util.ListItem;

public class ListItemDiffCallback extends DiffUtil.Callback {

    private final List<ListItem> mOldListItems;
    private final List<ListItem> mNewListItems;

    public ListItemDiffCallback(List<ListItem> oldListItems, List<ListItem> newListItems) {
        mOldListItems = oldListItems;
        mNewListItems = newListItems;
    }

    @Override
    public int getOldListSize() {
        return mOldListItems != null ? mOldListItems.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewListItems != null ? mNewListItems.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        int oldItemType = mOldListItems.get(oldItemPosition).getItemType();
        int newItemType = mNewListItems.get(newItemPosition).getItemType();

        if (oldItemType != newItemType) return false;
        if (oldItemType == ListItem.TYPE_ROUTINE) {
            return checkRoutinesAreSame(oldItemPosition, newItemPosition);
        } else {
            return checkIfTasksAreSame(oldItemPosition, newItemPosition);
        }
    }

    private boolean checkRoutinesAreSame(int oldRoutinePosition, int newRoutinePosition) {
        int oldRoutineId =  ((Routine) mOldListItems.get(oldRoutinePosition)).getId();
        int newRoutineId =  ((Routine) mNewListItems.get(newRoutinePosition)).getId();
        return oldRoutineId == newRoutineId;
    }

    private boolean checkIfTasksAreSame(int oldTaskPosition, int newTaskPosition) {
        int oldTaskId = ((Task) mOldListItems.get(oldTaskPosition)).getId();
        int newTaskId = ((Task) mNewListItems.get(newTaskPosition)).getId();
        return oldTaskId == newTaskId;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d("DIFF", ((Task) mOldListItems.get(oldItemPosition)).getName() + "&&" +
                ((Task) mNewListItems.get(newItemPosition)).getName() + "= " +
                mOldListItems.get(oldItemPosition).equals(mNewListItems.get(newItemPosition)));
        return mOldListItems.get(oldItemPosition).equals(mNewListItems.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
