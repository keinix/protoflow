package io.keinix.protoflow.tasks;


import android.content.Context;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.NewProjectDialogFragment;
import io.keinix.protoflow.dialogs.NewRoutineDialogFragment;

@Module
public abstract class TasksModule {

    @ActivityScope
    @Provides static TasksAdapter tasksAdapter(Context context) {
        return new TasksAdapter(context);
    }

    @ActivityScope
    @Provides static DatePickerDialogFragment provideDatePicker() {
        return new DatePickerDialogFragment();
    }

    @ActivityScope
    @Provides static NewProjectDialogFragment provideNewProjectDialog() {
        return new NewProjectDialogFragment();
    }
    @ActivityScope
    @Provides static NewRoutineDialogFragment provideNewRoutineDialog() {
        return new NewRoutineDialogFragment();
    }

}
