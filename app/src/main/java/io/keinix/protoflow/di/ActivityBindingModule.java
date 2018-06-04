package io.keinix.protoflow.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.keinix.protoflow.addeddittask.AddEditTaskActivity;
import io.keinix.protoflow.addeddittask.AddEditTaskModule;
import io.keinix.protoflow.tasks.TasksActivity;
import io.keinix.protoflow.tasks.TasksModule;

@Module
public abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = TasksModule.class)
    abstract TasksActivity tasksActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = AddEditTaskModule.class)
    abstract AddEditTaskActivity addEditTaskActivity();
}
