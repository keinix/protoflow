package io.keinix.protoflow.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import javax.inject.Singleton;

import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;

@Singleton
@Database(entities = {Task.class, Project.class, Routine.class}, version = 11, exportSchema = false)
public abstract class TaskRoomDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();
    public abstract RoutineDao routineDao();
}
