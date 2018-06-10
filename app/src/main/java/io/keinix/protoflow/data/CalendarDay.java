package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class CalendarDay {

    @PrimaryKey
    private long date;

    // This key is used to find a row when a new Task is created that
    // repeats on a specific day, that task is then added to the
    // tasks_scheduled String
    @ColumnInfo(name = "day_name")
    private String dayName;

    // This contains a comma separated list of task IDs
    // The String is split on the comma and the resulting IDs
    // are used to search the TaskRoomDatabase
    @ColumnInfo(name = "tasks_scheduled")
    private String tasksScheduled;

    // getters and setters
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getTasksScheduled() {
        return tasksScheduled;
    }

    public void setTasksScheduled(String tasksScheduled) {
        this.tasksScheduled = tasksScheduled;
    }
}
