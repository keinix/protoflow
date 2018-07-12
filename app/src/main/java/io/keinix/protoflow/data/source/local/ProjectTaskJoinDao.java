package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.ProjectTaskJoin;
import io.keinix.protoflow.data.Task;

@Dao
public interface ProjectTaskJoinDao {

    @Insert
    void insert(ProjectTaskJoin projectTaskJoin);

    @Query("SELECT * from task_table INNER JOIN project_task_join_table ON task_table.id = task_id WHERE project_id = :projectId")
    LiveData<List<Task>> getTasksForProject(int projectId);

}
