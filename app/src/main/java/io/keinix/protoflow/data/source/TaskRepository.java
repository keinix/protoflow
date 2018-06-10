package io.keinix.protoflow.data.source;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

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
        new insertAsyncTask(mTaskDao).execute(task);
    }

    //INSERT ASYNC
    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao asyncDao;

        public insertAsyncTask(TaskDao asyncDao) {
            this.asyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Task... params) {
            asyncDao.insert(params[0]);
            return null;
        }
    }
}
