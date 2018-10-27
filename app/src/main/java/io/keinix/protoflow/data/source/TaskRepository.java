package io.keinix.protoflow.data.source;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.local.CalendarDayDao;
import io.keinix.protoflow.data.source.local.TaskDao;

public class TaskRepository {

    private CalendarDayDao mCalendarDayDao;
    private TaskDao mTaskDao;

    TaskRepository(TaskDao taskDao, CalendarDayDao calendarDayDao) {
        mTaskDao = taskDao;
        mCalendarDayDao = calendarDayDao;
    }

    LiveData<Task> getTask(int id) {
        return mTaskDao.getTask(id);
    }

    LiveData<List<Task>> getTasks(List<Integer> taskIds) {
        return mTaskDao.getTasks(taskIds);
    }

    LiveData<List<Task>> getAllTasksFor7Days(List<Integer> taskIds) {
        return mTaskDao.getAllTasksFor7Days(taskIds);
    }

    LiveData<List<Task>> getAllRepeatedTasks() {
        return mTaskDao.getAllRepeatedTasks();
    }

    /**
     * @param taskIds a list of ids for a given day. pass null if no events scheduled for day
     * @param repeatedDay day constant from {@link Calendar} used to get tasks that repeat on
     *                    that day
     * @return List of {@link Task} for a given date
     */
    LiveData<List<Task>> getAllTasksOnDay(@Nullable List<Integer> taskIds, int repeatedDay) {
        switch (repeatedDay) {
            case Calendar.MONDAY:
                if (taskIds == null) return mTaskDao.getAllTasksForDateMonday();
                return mTaskDao.getAllTasksForDateMonday(taskIds);
            case Calendar.TUESDAY:
                if (taskIds == null) return mTaskDao.getAllTasksForDateTuesday();
                return mTaskDao.getAllTasksForDateTuesday(taskIds);
            case Calendar.WEDNESDAY:
                if (taskIds == null) return mTaskDao.getAllTasksForDateWednesday();
                return mTaskDao.getAllTasksForDateWednesday(taskIds);
            case Calendar.THURSDAY:
                if (taskIds == null) return mTaskDao.getAllTasksForDateThursday();
                return mTaskDao.getAllTasksForDateThursday(taskIds);
            case Calendar.FRIDAY:
                if (taskIds == null) return mTaskDao.getAllTasksForDateFriday();
                return mTaskDao.getAllTasksForDateFriday(taskIds);
            case Calendar.SATURDAY:
                if (taskIds == null) return mTaskDao.getAllTasksForDateSaturday();
                return mTaskDao.getAllTasksForDateSaturday(taskIds);
            default:
                if (taskIds == null) return mTaskDao.getAllTasksForDateSunday();
                return mTaskDao.getAllTasksForDateSunday(taskIds);
        }
    }

    LiveData<List<Task>> getTasksInQuickList() {
        return mTaskDao.getTasksInQuickList();
    }

    LiveData<List<Task>> getTasksInProject(int projectId) {
        return mTaskDao.getTaskInProject(projectId);
    }

    LiveData<List<Task>> getRoutineChildTasks(int routineId) {
        return mTaskDao.getRoutineChildTasks(routineId);
    }

    void insertTask(Task task) {
        new insertAsyncTask(mTaskDao, mCalendarDayDao).execute(task);
    }

    void updateTask(Task task) {
        new updateAsyncTask(mTaskDao).execute(task);
    }

    void updateBatchTasks(Task task) {
        new updateBatchTasksAsync(mTaskDao).execute(task);
    }

    void deleteTask(Task task) {
        new deleteAsyncTask(mTaskDao).execute(task);
    }

    void deleteTasksInProject(int projectId) {
        new deleteTasksInProjectAsync(mTaskDao).execute(projectId);
    }

    void deleteTaskInRoutine(int routineId) {
        new deleteTasksInRoutineAsync(mTaskDao).execute(routineId);
    }



    //INSERT ASYNC
    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao asyncTaskDao;
        private CalendarDayDao calendarDayDao;

        public insertAsyncTask(TaskDao asyncTaskDao, CalendarDayDao asyncCalendarDao) {
            this.asyncTaskDao = asyncTaskDao;
            this.calendarDayDao = asyncCalendarDao;
        }

        @Override
        protected Void doInBackground(Task... params) {
            long taskId = asyncTaskDao.insert(params[0]);
            if (params[0].getScheduledDateUtc() > 0) {
                insertTaskIdIntoDay(taskId, params[0].getScheduledDateUtc());
            }
            return null;
        }

        private void insertTaskIdIntoDay(long id, long dayInMillis) {
            CalendarDay calendarDay = calendarDayDao.getCalendarDay(dayInMillis);
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

    //UPDATE ASYNC
    private static class updateAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao asyncTaskDao;

        public updateAsyncTask(TaskDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            asyncTaskDao.update(tasks[0]);
            return null;
        }
    }

    //UPDATE ASYNC
    private static class updateBatchTasksAsync extends AsyncTask<Task, Void, Void> {

        private TaskDao asyncTaskDao;

        public updateBatchTasksAsync(TaskDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            asyncTaskDao.updateBatch(tasks[0]);
            return null;
        }
    }

    //DELETE ASYNC
    private static class deleteAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao mAsyncDao;

        public deleteAsyncTask(TaskDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mAsyncDao.delete(tasks[0]);
            return null;
        }
    }

    //DELETE ASYNC
    private static class deleteTasksInProjectAsync extends AsyncTask<Integer, Void, Void> {

        private TaskDao mAsyncDao;

        public deleteTasksInProjectAsync(TaskDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Integer... projectIds) {
            mAsyncDao.deleteTasksInProject(projectIds[0]);
            return null;
        }
    }

    //DELETE ASYNC
    private static class deleteTasksInRoutineAsync extends AsyncTask<Integer, Void, Void> {

        private TaskDao mAsyncDao;

        public deleteTasksInRoutineAsync(TaskDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Integer... routineIds) {
            mAsyncDao.deleteTasksInRoutine(routineIds[0]);
            return null;
        }
    }
}
