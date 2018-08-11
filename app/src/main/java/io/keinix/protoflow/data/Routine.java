package io.keinix.protoflow.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import io.keinix.protoflow.util.ListItem;

@Entity (tableName = "routine_table")
public class Routine implements ListItem, Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @Ignore
    private boolean isExpanded;

    @Ignore
    private int childTaskCount;

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

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public int getChildTaskCount() {
        return childTaskCount;
    }

    public void setChildTaskCount(int childTaskCount) {
        this.childTaskCount = childTaskCount;
    }

    @Override
    public String toString() {
        return "Routine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    private Routine(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        childTaskCount = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(childTaskCount);
    }

    public final static Creator<Routine>  CREATOR = new Creator<Routine>() {

        @Override
        public Routine createFromParcel(Parcel parcel) {
            return new Routine(parcel);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };
}
