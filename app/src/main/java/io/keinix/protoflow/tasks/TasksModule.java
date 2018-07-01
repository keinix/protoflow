package io.keinix.protoflow.tasks;


import android.content.Context;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;

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

}
