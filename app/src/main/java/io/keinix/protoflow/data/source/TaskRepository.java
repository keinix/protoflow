package io.keinix.protoflow.data.source;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.local.CalendarDayDao;
import io.keinix.protoflow.data.source.local.ProjectDao;
import io.keinix.protoflow.data.source.local.RoutineDao;
import io.keinix.protoflow.data.source.local.TaskDao;

@Singleton
public class TaskRepository {

    private TaskDao mTaskDao;
    private CalendarDayDao mCalendarDayDao;
    private ProjectDao mProjectDao;
    private RoutineDao mRoutineDao;
    private LiveData<List<Task>> mAllTasks;

    @Inject
    public TaskRepository(TaskDao taskDao, CalendarDayDao calendarDayDao, ProjectDao projectDao,
                          RoutineDao routineDao) {
        mTaskDao = taskDao;
        mCalendarDayDao = calendarDayDao;
        mProjectDao = projectDao;
        mRoutineDao = routineDao;
        mAllTasks = mTaskDao.getAllTasks();
    }

    public void insertRoutine(Routine routine) {
        new insertRoutineAsync(mRoutineDao).execute(routine);
    }

    public void deleteRoutine(Routine routine) {
        new deleteRoutineAsyncTask(mRoutineDao).execute(routine);
    }


    public LiveData<List<Task>> getRoutineChildTasks(int routineId) {
        return mTaskDao.getRoutineChildTasks(routineId);
    }

    public void updateBatchTasks(Task task) {
        new updateBatchTasksAsync(mTaskDao).execute(task);
    }

    public void deleteTask(Task task) {
        new deleteAsyncTask(mTaskDao).execute(task);
    }

    public LiveData<List<Routine>> getAllRoutines() {
        return mRoutineDao.getAllRoutines();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<List<Project>> getAllProjects() {
        return mProjectDao.getAllProjects();
    }

    public LiveData<Task> getTask(int id) {
        return mTaskDao.getTask(id);
    }

    public CalendarDay getCalendarDay(long date) {
        return mCalendarDayDao.getCalendarDay(date);
    }

    public LiveData<List<Task>> getTasksInProject(int projectId) {
        return mTaskDao.getTaskInProject(projectId);
    }

    public LiveData<CalendarDay> getLiveCalendarDay(long date) {
        return mCalendarDayDao.getLiveCalendarDay(date);
    }

    public LiveData<List<Task>> getTasks(List<Integer> taskIds) {
        return mTaskDao.getTasks(taskIds);
    }

    public LiveData<List<CalendarDay>> getNext7CalendarDays(List<Long> dates) {
        return mCalendarDayDao.getNext7CalendarDays(dates);
    }

    public LiveData<List<Task>> getAllTasksFor7Days(List<Integer> taskIds) {
        return mTaskDao.getAllTasksFor7Days(taskIds);
    }

    public LiveData<Project> getProject(int id) {
        return mProjectDao.getProject(id);
    }

    public LiveData<List<Task>> getAllRepeatedTasks() {
        return mTaskDao.getAllRepeatedTasks();
    }

    public LiveData<List<Task>> getTasksInQuickList() {
        return mTaskDao.getTasksInQuickList();
    }

    public void deleteTaskInRoutine(int routineId) {
        new deleteAllAsyncTask(mTaskDao).execute(routineId);
    }

    /**
     * @param taskIds a list of ids for a given day. pass null if no events scheduled for day
     * @param repeatedDay day constant from {@link Calendar} used to get tasks that repeat on
     *                    that day
     * @return List of {@link Task} for a given date
     */
    public LiveData<List<Task>> getAllTasksOnDay(@Nullable List<Integer> taskIds, int repeatedDay) {
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



    //SQL UPDATE
    public void updateTask(Task task) {
        new updateAsyncTask(mTaskDao).execute(task);
    }

    //SQL INSERT
    public void insertTask(Task task) {
        new insertAsyncTask(mTaskDao, mCalendarDayDao).execute(task);
    }

    public void insertProject(Project project) {
        new insertAsyncTask.insertProjectAsync(mProjectDao).execute(project);
    }

    public void updateProject(Project project) {
        new updateProjectAsync(mProjectDao).execute(project);
    }

    public void insertCalendarDay(CalendarDay calendarDay) {
        new insertCalendarDayAsync(mCalendarDayDao).execute(calendarDay);
    }

    public void updateCalendarDay(CalendarDay calendarDay) {
        new updateCalendarDayAsync(mCalendarDayDao).execute(calendarDay);
    }

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
    private static class deleteRoutineAsyncTask extends AsyncTask<Routine, Void, Void> {

        private RoutineDao mAsyncDao;

        public deleteRoutineAsyncTask(RoutineDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Routine... routine) {
            mAsyncDao.deleteRooutine(routine[0]);
            return null;
        }
    }

    //DELETE ASYNC
    private static class deleteAllAsyncTask extends AsyncTask<Integer, Void, Void> {

        private TaskDao mAsyncDao;

        public deleteAllAsyncTask(TaskDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Integer... routineIds) {
            mAsyncDao.deleteTasksInRoutine(routineIds[0]);
            return null;
        }
    }

    //INSERT PROJECT ASYNC
    private static class updateProjectAsync extends AsyncTask<Project, Void, Void> {

        private ProjectDao asyncProjectDao;

        public updateProjectAsync(ProjectDao projectDao) {
            asyncProjectDao = projectDao;
        }

        @Override
        protected Void doInBackground(Project... projects) {
            asyncProjectDao.update(projects[0]);
            return null;
        }
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

        //INSERT PROJECT ASYNC
        private static class insertProjectAsync extends AsyncTask<Project, Void, Void> {

            private ProjectDao asyncProjectDao;

            public insertProjectAsync(ProjectDao projectDao) {
                asyncProjectDao = projectDao;
            }

            @Override
            protected Void doInBackground(Project... projects) {
                asyncProjectDao.insert(projects[0]);
                return null;
            }
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

    //INSERT ALL ASYNC
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

    //INSERT ROUTINE ASYNC
    private static class insertRoutineAsync extends AsyncTask<Routine, Void, Void> {

        private RoutineDao asyncRoutineDao;

        public insertRoutineAsync(RoutineDao routineDao) {
            asyncRoutineDao = routineDao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            asyncRoutineDao.insertRoutine(routines[0]);
            return null;
        }
    }


}
