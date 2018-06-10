package io.keinix.protoflow.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;

@Dao
@Singleton
public interface CalendarDayDao {

    @Insert
    void insert(CalendarDay day);
}
