package io.keinix.protoflow.util;

import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.tasks.TasksAdapter;

/**
 * Used in {@link TasksAdapter} to populate a single list that
 * holds both {@link Task} and {@link Routine}
 */
public interface ListItem {

    int TYPE_TASK = 1;
    int TYPE_ROUTINE = 2;

    int getItemType();
}
