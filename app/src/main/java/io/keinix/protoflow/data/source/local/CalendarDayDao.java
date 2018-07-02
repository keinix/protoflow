package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.util.RoomTypeConverters;

@Dao
@Singleton
public interface CalendarDayDao {

    @Insert
    void insert(CalendarDay day);

    @Query("SELECT * from calendar_day_table WHERE date = :date LIMIT 1")
    CalendarDay getCalendarDay(long date);

    @Query("SELECT * from calendar_day_table WHERE date = :date LIMIT 1")
    LiveData<CalendarDay> getLiveCalendarDay(long date);

    @Query("SELECT * from calendar_day_table WHERE date IN (:dates)")
    LiveData<List<CalendarDay>> getNext7CalendarDays(List<Long> dates);

    @Update
    void update(CalendarDay day);


}
