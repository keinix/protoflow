package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "time_created")
    private long timeCreated;

    public Task(@NonNull String name) {
        this.name = name;
        timeCreated = System.currentTimeMillis();
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

    @NonNull
    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(@NonNull long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
