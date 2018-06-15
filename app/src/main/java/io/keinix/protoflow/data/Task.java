package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "task_table")
public class Task {

    //TODO:might need to add column info
    @PrimaryKey (autoGenerate = true)
    private int id = 0;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "routines")
    private String routines;

    @ColumnInfo(name = "duration")
    private int durationInMinutes;

    @ColumnInfo(name = "scheduled_date")
    private long scheduledDateUtc;

    @ColumnInfo(name = "start_time")
    private long startTimeUtc;

    @Nullable
    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "repeats_on_monday")
    private boolean repeatsOnMonday;

    @ColumnInfo(name = "repeats_on_tuesday")
    private boolean repeatsOnTuesday;

    @ColumnInfo(name = "repeats_on_wednesday")
    private boolean repeatsOnWednesday;

    @ColumnInfo(name = "repeats_on_thursday")
    private boolean repeatsOnThursday;

    @ColumnInfo(name = "repeats_on_friday")
    private boolean repeatsOnFriday;

    @ColumnInfo(name = "repeats_on_saturday")
    private boolean repeatsOnSaturday;

    @ColumnInfo(name = "repeats_on_sunday")
    private boolean repeatsOnSunday;


    public Task(@NonNull String name) {
        this.name = name;
    }


    // Getters and Setters
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getRoutines() {
        return routines;
    }

    public void setRoutines(@Nullable String routines) {
        this.routines = routines;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public long getScheduledDateUtc() {
        return scheduledDateUtc;
    }

    public void setScheduledDateUtc(long scheduledDateUtc) {
        this.scheduledDateUtc = scheduledDateUtc;
    }

    public long getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(long startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public boolean isRepeatsOnMonday() {
        return repeatsOnMonday;
    }

    public void setRepeatsOnMonday(boolean repeatsOnMonday) {
        this.repeatsOnMonday = repeatsOnMonday;
    }

    public boolean isRepeatsOnTuesday() {
        return repeatsOnTuesday;
    }

    public void setRepeatsOnTuesday(boolean repeatsOnTuesday) {
        this.repeatsOnTuesday = repeatsOnTuesday;
    }

    public boolean isRepeatsOnWednesday() {
        return repeatsOnWednesday;
    }

    public void setRepeatsOnWednesday(boolean repeatsOnWednesday) {
        this.repeatsOnWednesday = repeatsOnWednesday;
    }

    public boolean isRepeatsOnThursday() {
        return repeatsOnThursday;
    }

    public void setRepeatsOnThursday(boolean repeatsOnThursday) {
        this.repeatsOnThursday = repeatsOnThursday;
    }

    public boolean isRepeatsOnFriday() {
        return repeatsOnFriday;
    }

    public void setRepeatsOnFriday(boolean repeatsOnFriday) {
        this.repeatsOnFriday = repeatsOnFriday;
    }

    public boolean isRepeatsOnSaturday() {
        return repeatsOnSaturday;
    }

    public void setRepeatsOnSaturday(boolean repeatsOnSaturday) {
        this.repeatsOnSaturday = repeatsOnSaturday;
    }

    public boolean isRepeatsOnSunday() {
        return repeatsOnSunday;
    }

    public void setRepeatsOnSunday(boolean repeatsOnSunday) {
        this.repeatsOnSunday = repeatsOnSunday;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", routines='" + routines + '\'' +
                ", durationInMinutes=" + durationInMinutes +
                ", scheduledDateUtc=" + scheduledDateUtc +
                ", startTimeUtc=" + startTimeUtc +
                ", notes='" + notes + '\'' +
                ", repeatsOnMonday=" + repeatsOnMonday +
                ", repeatsOnTuesday=" + repeatsOnTuesday +
                ", repeatsOnWednesday=" + repeatsOnWednesday +
                ", repeatsOnThursday=" + repeatsOnThursday +
                ", repeatsOnFriday=" + repeatsOnFriday +
                ", repeatsOnSaturday=" + repeatsOnSaturday +
                ", repeatsOnSunday=" + repeatsOnSunday +
                '}';
    }
}
