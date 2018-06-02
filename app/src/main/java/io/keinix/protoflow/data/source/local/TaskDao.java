package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.keinix.protoflow.data.Task;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("SELECT * from task_table")
    LiveData<List<Task>> getAllTasks();
}
