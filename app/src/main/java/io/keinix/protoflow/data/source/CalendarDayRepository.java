package io.keinix.protoflow.data.source;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.source.local.CalendarDayDao;

@Singleton
public class CalendarDayRepository {

    private CalendarDayDao mCalendarDayDao;

    @Inject
    public CalendarDayRepository(CalendarDayDao calendarDayDao) {
        mCalendarDayDao = calendarDayDao;
    }

    LiveData<CalendarDay> getLiveCalendarDay(long date) {
        return mCalendarDayDao.getLiveCalendarDay(date);
    }

    LiveData<List<CalendarDay>> getNext7CalendarDays(List<Long> dates) {
        return mCalendarDayDao.getNext7CalendarDays(dates);
    }


    void insertCalendarDay(CalendarDay calendarDay) {
        new insertCalendarDayAsync(mCalendarDayDao).execute(calendarDay);
    }

    void updateCalendarDay(CalendarDay calendarDay) {
        new updateCalendarDayAsync(mCalendarDayDao).execute(calendarDay);
    }

    //---------------AsyncTasks---------------

    // INSERT ASYNC
    private static class insertCalendarDayAsync extends AsyncTask<CalendarDay, Void, Void> {
        CalendarDayDao mAsyncDao;

        public insertCalendarDayAsync(CalendarDayDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(CalendarDay... calendarDays) {
            mAsyncDao.insert(calendarDays[0]);
            return null;
        }
    }

    // INSERT ASYNC
    private static class updateCalendarDayAsync extends AsyncTask<CalendarDay, Void, Void> {
        CalendarDayDao mAsyncDao;

        public updateCalendarDayAsync(CalendarDayDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(CalendarDay... calendarDays) {
            mAsyncDao.update(calendarDays[0]);
            return null;
        }
    }
}
