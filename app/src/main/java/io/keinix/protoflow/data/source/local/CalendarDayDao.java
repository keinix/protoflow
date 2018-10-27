package io.keinix.protoflow.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.source.Repository;

@Dao
@Singleton
public interface CalendarDayDao {

    @Insert
    void insert(CalendarDay day);

    @Update
    void update(CalendarDay day);

    /**
     * used in {@link Repository} async to check if a CalendarDay exists
     * when creating a new task
     * @param date of the day
     * @return {@link CalendarDay} for that date
     */
    @Query("SELECT * from calendar_day_table WHERE date = :date LIMIT 1")
    CalendarDay getCalendarDay(long date);

    @Query("SELECT * from calendar_day_table WHERE date = :date LIMIT 1")
    LiveData<CalendarDay> getLiveCalendarDay(long date);

    @Query("SELECT * from calendar_day_table WHERE date IN (:dates)")
    LiveData<List<CalendarDay>> getNext7CalendarDays(List<Long> dates);
}
