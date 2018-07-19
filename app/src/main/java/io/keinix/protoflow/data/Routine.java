package io.keinix.protoflow.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import io.keinix.protoflow.util.ListItem;

@Entity (tableName = "routine_table")
public class Routine implements ListItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @Override
    public int getItemType() {
        return ListItem.TYPE_ROUTINE;
    }

    public Routine(@NonNull String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Routine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
