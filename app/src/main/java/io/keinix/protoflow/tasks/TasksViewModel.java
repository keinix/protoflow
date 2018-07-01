package io.keinix.protoflow.tasks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class TasksViewModel extends AndroidViewModel {

    // ----------Member variables------------
    private TaskRepository mTaskRepository;
    private LiveData<List<Task>> mAllTasks;

    @Inject
    public TasksViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
        mAllTasks = mTaskRepository.getAllTasks();
    }

    // -------public: model layer bridge--------
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public void insertTask(Task task) {
        mTaskRepository.insertTask(task);
    }

    public CalendarDay getCalendarDay(long date) {
        return mTaskRepository.getCalendarDay(date);
    }
}
