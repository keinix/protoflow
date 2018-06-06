package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {


    private TaskRepository mTaskRepository;

    @Inject
    public AddEditTaskViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
    }

    void addTask(Task task) {
        mTaskRepository.insertTask(task);
    }
}
