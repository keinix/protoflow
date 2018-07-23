package io.keinix.protoflow.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.util.RoomTypeConverters;

@Singleton
@Database(entities = {CalendarDay.class}, version = 2, exportSchema = false)
@TypeConverters({RoomTypeConverters.class})
public abstract class CalendarDayDatabase extends RoomDatabase {

    public abstract CalendarDayDao calendarDayDao();
}
