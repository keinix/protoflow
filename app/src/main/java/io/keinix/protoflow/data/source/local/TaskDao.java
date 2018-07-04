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

    @Query("SELECT * from task_table WHERE id IN (:taskIds)")
    LiveData<List<Task>> getTasks(List<Integer> taskIds);

    // Returns Tasks that repeat on a given day AND tasks scheduled for that specific date
    // whose ids are specified in a CalendarDay object
    @Query("SELECT * from task_table WHERE repeats_on_monday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateMonday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_tuesday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateTuesday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_wednesday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateWednesday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_thursday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateThursday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_friday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateFriday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_saturday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateSaturday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_sunday = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksForDateSunday(List<Integer> taskIds);

    @Query("SELECT * from task_table WHERE repeats_on_a_day = 1 OR id IN (:taskIds)")
    LiveData<List<Task>> getAllTasksFor7Days(List<Integer> taskIds);

    // Returns Tasks that repeat on a given day. These methods are used if no task was
    // specifically scheduled for that date so no CalendarDay object exists for the day
    @Query("SELECT * from task_table WHERE repeats_on_monday = 1")
    LiveData<List<Task>> getAllTasksForDateMonday();

    @Query("SELECT * from task_table WHERE repeats_on_tuesday = 1")
    LiveData<List<Task>> getAllTasksForDateTuesday();

    @Query("SELECT * from task_table WHERE repeats_on_wednesday = 1")
    LiveData<List<Task>> getAllTasksForDateWednesday();

    @Query("SELECT * from task_table WHERE repeats_on_thursday = 1")
    LiveData<List<Task>> getAllTasksForDateThursday();

    @Query("SELECT * from task_table WHERE repeats_on_friday = 1")
    LiveData<List<Task>> getAllTasksForDateFriday();

    @Query("SELECT * from task_table WHERE repeats_on_saturday = 1")
    LiveData<List<Task>> getAllTasksForDateSaturday();

    @Query("SELECT * from task_table WHERE repeats_on_sunday = 1")
    LiveData<List<Task>> getAllTasksForDateSunday();

    @Query("SELECT * from task_table WHERE repeats_on_a_day = 1")
    LiveData<List<Task>> getAllRepeatedTasks();



    @Update
    void update(Task task);
}
