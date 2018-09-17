package io.keinix.protoflow.tasks;


import android.app.Activity;
import android.content.Context;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.AddListItemDialogFragment;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.NewProjectDialogFragment;
import io.keinix.protoflow.dialogs.NewRoutineDialogFragment;
import io.keinix.protoflow.dialogs.ProjectPickerDialogFragment;

@Module
public abstract class TasksModule {

    @ActivityScope
    @Provides static TasksAdapter tasksAdapter(Context context, Activity activity) {
        return new TasksAdapter(context, activity);
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

    @ActivityScope
    @Binds abstract Activity bindActivity(TasksActivity tasksActivity);

    @ActivityScope
    @Provides static AddListItemDialogFragment AddListItemDialogFragment() {
        return new AddListItemDialogFragment();
    }

    @ActivityScope
    @Provides static ProjectPickerDialogFragment ProjectPickerDialogFragment() {
        return new ProjectPickerDialogFragment();
    }
}
