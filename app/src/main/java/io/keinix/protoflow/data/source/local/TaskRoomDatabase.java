package io.keinix.protoflow.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import javax.inject.Singleton;

import io.keinix.protoflow.data.Task;

@Singleton
@Database(entities = {Task.class}, version = 3, exportSchema = false)
public abstract class TaskRoomDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
}
