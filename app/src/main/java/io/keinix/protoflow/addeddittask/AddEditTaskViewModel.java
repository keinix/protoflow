package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {


    private TaskRepository mTaskRepository;
    private SparseBooleanArray isDaySelectedArray;

    @Inject
    public AddEditTaskViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
    }

    void addTask(Task task) {
        mTaskRepository.insertTask(task);
    }

    /**
     * @param id the id of a day TextView
     * @return if day was selected before click
     */
    public Boolean isDaySelected(int id) {
        if (isDaySelectedArray.get(id)) {
            isDaySelectedArray.put(id, false);
            return true;
        } else {
            isDaySelectedArray.put(id, true);
            return false;
        }
    }

    public void initNewIsDaySelectedArray(List<TextView> days) {
        if (isDaySelectedArray == null) {
            isDaySelectedArray = new SparseBooleanArray();
            for (TextView day : days) {
                isDaySelectedArray.put(day.getId(), true);
            }
        }
    }
}
