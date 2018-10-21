package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import javax.inject.Singleton;

import io.keinix.protoflow.data.Project;

@Dao
@Singleton
public interface ProjectDao {

    @Insert
    long insert(Project project);

    @Query("SELECT * from project_table")
    LiveData<List<Project>> getAllProjects();

    @Query("SELECT * from project_table WHERE id = :id LIMIT 1")
    LiveData<Project> getProject(int id);

    @Update
    void update(Project project);
}
