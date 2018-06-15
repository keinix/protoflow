package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "calendar_day_table")
public class CalendarDay {

    @PrimaryKey
    private long date;

    // This key is used to find a row when a new Task is created that
    // repeats on a specific day, that task is then added to the
    // tasks_scheduled String
    @ColumnInfo(name = "day_name")
    private String dayName;

    /**
     * converted using {@link io.keinix.protoflow.util.RoomTypeConverters}
     */
    @ColumnInfo(name = "tasks_scheduled")
    private ArrayList<Integer> tasksScheduled;

    public CalendarDay(long date) {
        this.date = date;
    }

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

    public ArrayList<Integer> getTasksScheduled() {
        return tasksScheduled;
    }

    public void setTasksScheduled(ArrayList<Integer> tasksScheduled) {
        this.tasksScheduled = tasksScheduled;
    }
}
