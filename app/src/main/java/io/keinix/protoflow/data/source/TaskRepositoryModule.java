package io.keinix.protoflow.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.data.source.local.CalendarDayDao;
import io.keinix.protoflow.data.source.local.CalendarDayDatabase;
import io.keinix.protoflow.data.source.local.ProjectDao;
import io.keinix.protoflow.data.source.local.RoutineDao;
import io.keinix.protoflow.data.source.local.TaskDao;
import io.keinix.protoflow.data.source.local.TaskRoomDatabase;

@Module
public abstract class TaskRepositoryModule {

    @Singleton
    @Provides static TaskRoomDatabase provideDb(Application context) {
        return Room.databaseBuilder(context, TaskRoomDatabase.class, "task_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides static TaskDao provideDao(TaskRoomDatabase db) {
        return db.taskDao();
    }

    @Singleton
    @Provides static CalendarDayDatabase provideCalendarDb(Application context) {
        return Room.databaseBuilder(context, CalendarDayDatabase.class, "calendar_day_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides static CalendarDayDao provideCalendarDao(CalendarDayDatabase db) {
        return db.calendarDayDao();
    }

    @Singleton
    @Provides static RoutineDao provideRoutineDao(TaskRoomDatabase db) {
        return db.routineDao();
    }

    @Singleton
    @Provides static ProjectDao projectDao(TaskRoomDatabase db) {
        return db.projectDao();
    }

    @Singleton
    @Provides static Repository taskRepository(TaskDao dao, CalendarDayDao cDao, ProjectDao pDao, RoutineRepository rRepo) {
        return new Repository(dao, cDao, pDao, rRepo);
    }

    @Singleton
    @Provides static RoutineRepository proveRoutineRepository(RoutineDao dao) {
        return new RoutineRepository(dao);
    }

}
