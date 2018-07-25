package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import javax.inject.Singleton;

import io.keinix.protoflow.data.Routine;

@Dao
@Singleton
public interface RoutineDao {

    @Query("SELECT * from routine_table")
    LiveData<List<Routine>> getAllRoutines();

    @Insert
    void insertRoutine(Routine routine);
}
