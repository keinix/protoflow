package io.keinix.protoflow.tasks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class TasksViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepository;
    private LiveData<List<Task>> mAllTasks;

    public TasksViewModel(@NonNull Application application) {
        super(application);
        mTaskRepository = new TaskRepository(application);
        mAllTasks = mTaskRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public void insertTask(Task task) {
        mTaskRepository.insertTask(task);
    }
}
