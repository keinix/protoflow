package io.keinix.protoflow.data.source;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.local.TaskDao;
import io.keinix.protoflow.data.source.local.TaskRoomDatabase;

public class TaskRepository {

    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;

    public TaskRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
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
