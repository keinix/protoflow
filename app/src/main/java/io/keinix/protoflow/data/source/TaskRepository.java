package io.keinix.protoflow.data.source;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.local.CalendarDayDao;
import io.keinix.protoflow.data.source.local.TaskDao;
import io.keinix.protoflow.data.source.local.TaskRoomDatabase;

@Singleton
public class TaskRepository {

    private TaskDao mTaskDao;
    private CalendarDayDao mCalendarDayDao;
    private LiveData<List<Task>> mAllTasks;

    @Inject
    public TaskRepository(TaskDao taskDao, CalendarDayDao calendarDayDao) {
        mTaskDao = taskDao;
        mCalendarDayDao = calendarDayDao;
        mAllTasks = mTaskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }




    //SQL INSERT
    public void insertTask(Task task) {
        new insertAsyncTask(mTaskDao, mCalendarDayDao).execute(task);
    }

    //INSERT ASYNC
    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao asyncDao;
        private CalendarDayDao calendarDayDao;

        public insertAsyncTask(TaskDao asyncTaskDao, CalendarDayDao asyncCalendarDao) {
            this.asyncDao = asyncTaskDao;
            this.calendarDayDao = asyncCalendarDao;
        }

        @Override
        protected Void doInBackground(Task... params) {
            long taskId = asyncDao.insert(params[0]);
            if (params[0].getScheduledDateUtc() > 0) {
                insertTaskIdIntoDay(taskId, params[0].getScheduledDateUtc());
            }
            return null;
        }

        private void insertTaskIdIntoDay(long id, long dayInMillis) {
            CalendarDay calendarDay = calendarDayDao.getDay(dayInMillis);
            if (calendarDay == null) {
                calendarDay = new CalendarDay(dayInMillis);
                ArrayList<Integer> taskIds = new ArrayList<>();
                taskIds.add((int) id);
                calendarDay.setScheduledTaskIds(taskIds);
                calendarDayDao.insert(calendarDay);
            } else {
                calendarDay.addTaskId((int) id);
                calendarDayDao.update(calendarDay);
            }
        }
    }
}
