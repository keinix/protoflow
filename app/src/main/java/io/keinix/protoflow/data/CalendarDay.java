package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import java.util.ArrayList;


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
    private ArrayList<Integer> scheduledTaskIds;

    @ColumnInfo(name = "completed_tasks")
    private ArrayList<Integer> completedTasks;

    public CalendarDay(long date) {
        this.date = date;
        completedTasks = new ArrayList<>();
        scheduledTaskIds = new ArrayList<>();
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

    public ArrayList<Integer> getScheduledTaskIds() {
        return scheduledTaskIds;
    }

    public void setScheduledTaskIds(ArrayList<Integer> scheduledTaskIds) {
        this.scheduledTaskIds = scheduledTaskIds;
    }

    public void addScheduledTaskIds(ArrayList<Integer> taskIds) {
        if (scheduledTaskIds == null) scheduledTaskIds = new ArrayList<>();
        scheduledTaskIds.addAll(taskIds);
    }

    public void addScheduledTaskIds(int taskId) {
        if (scheduledTaskIds == null) scheduledTaskIds = new ArrayList<>();
        scheduledTaskIds.add(taskId);
    }

    public void addTaskId(int id) {
        if (scheduledTaskIds == null) scheduledTaskIds = new ArrayList<>();
        scheduledTaskIds.add(id);
    }

    @Nullable
    public ArrayList<Integer> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(ArrayList<Integer> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public void addCompletedTasks(int taskId) {
        if (completedTasks == null) completedTasks = new ArrayList<>();
        completedTasks.add(taskId);
    }


    @Override
    public String toString() {
        return "CalendarDay{" +
                "date=" + date +
                ", dayName='" + dayName + '\'' +
                ", scheduledTaskIds=" + scheduledTaskIds +
                '}';
    }
}
