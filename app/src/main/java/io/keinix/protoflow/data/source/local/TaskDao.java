package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import javax.inject.Singleton;

import io.keinix.protoflow.data.Task;

@Singleton
@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Query("SELECT * from task_table")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * from task_table WHERE id = :id LIMIT 1")
    LiveData<Task> getTask(int id);

    @Query("SELECT * from Task_table WHERE id IN (:taskIds)")
    LiveData<List<Task>> getTasks(List<Integer> taskIds);

    @Update
    void update(Task task);
}
