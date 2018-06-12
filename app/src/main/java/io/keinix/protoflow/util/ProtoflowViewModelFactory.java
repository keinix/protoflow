package io.keinix.protoflow.util;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.keinix.protoflow.addeddittask.AddEditTaskViewModel;
import io.keinix.protoflow.data.source.TaskRepository;
import io.keinix.protoflow.tasks.TasksViewModel;


@Singleton
public class ProtoflowViewModelFactory implements ViewModelProvider.Factory {

    private final TaskRepository mTaskRepository;
    private final Application mApplication;

    @Inject
    public ProtoflowViewModelFactory(Application application, TaskRepository taskRepository) {
        mTaskRepository = taskRepository;
        mApplication = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TasksViewModel.class)) {
            return (T) new TasksViewModel(mApplication, mTaskRepository);
        } else if (modelClass.isAssignableFrom(AddEditTaskViewModel.class)) {
            return (T) new AddEditTaskViewModel(mApplication, mTaskRepository);
        } else {
            throw new IllegalArgumentException("ViewModel not found in ProtoflowViewModelFactory");
        }
    }
}
