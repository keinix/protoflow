package io.keinix.protoflow.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.data.source.local.TaskDao;
import io.keinix.protoflow.data.source.local.TaskRoomDatabase;

@Module
public abstract class TaskRepositoryModule {

    @Singleton
    @Provides static TaskRoomDatabase provideDb(Application context) {
        return Room.databaseBuilder(context, TaskRoomDatabase.class, "task_database")
                .build();
    }

    @Singleton
    @Provides static TaskDao provideDao(TaskRoomDatabase db) {
        return db.taskDao();
    }

    @Singleton
    @Provides static TaskRepository taskRepository(TaskDao dao) {
        return new TaskRepository(dao);
    }
}
