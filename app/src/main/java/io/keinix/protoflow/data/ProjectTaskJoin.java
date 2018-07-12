package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "project_task_join_table",
    primaryKeys = {"project_id", "task_id" },
    foreignKeys = {
            @ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "project_id"),
            @ForeignKey(entity = Task.class,
                parentColumns = "id",
                childColumns = "task_id")
    })
public class ProjectTaskJoin {

    @ColumnInfo(name = "project_id")
    private int projectId;

    @ColumnInfo(name = "task_id")
    private int taskId;

    public ProjectTaskJoin(int projectId, int taskId) {
        this.projectId = projectId;
        this.taskId = taskId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
