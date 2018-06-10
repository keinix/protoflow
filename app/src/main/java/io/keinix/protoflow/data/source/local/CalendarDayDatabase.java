package io.keinix.protoflow.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;

@Singleton
@Database(entities = {CalendarDay.class}, version = 1, exportSchema = false)
public abstract class CalendarDayDatabase extends RoomDatabase{

    public abstract CalendarDayDao calendarDayDao();
}
