package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {

    private TaskRepository mRepository;

    public AddEditTaskViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TaskRepository(application);
    }

    void addTask(Task task) {
        mRepository.insertTask(task);
    }
}
