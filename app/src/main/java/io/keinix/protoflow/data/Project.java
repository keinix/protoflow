package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.keinix.protoflow.util.RoomTypeConverters;

@Entity(tableName = "project_table")
@TypeConverters({RoomTypeConverters.class})
public class Project implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String name;

    public Project(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(ArrayList<Integer> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public void addCompletedTasks(int id) {
        if (completedTasks == null) completedTasks = new ArrayList<>();
        completedTasks.add(id);
    }

    @ColumnInfo(name = "completed_tasks")
    public ArrayList<Integer> completedTasks;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    private Project(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Parcelable.Creator<Project> CREATOR
            = new Parcelable.Creator<Project>() {

        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
