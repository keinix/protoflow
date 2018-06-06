package io.keinix.protoflow.tasks;


import android.content.Context;
import android.support.v7.widget.RecyclerView;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;

@Module
public abstract class TasksModule {

    @ActivityScope
    @Provides static TasksAdapter tasksAdapter(Context context) {
        return  new TasksAdapter(context);
    }

}
