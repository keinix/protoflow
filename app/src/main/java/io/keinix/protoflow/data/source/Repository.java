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
public class Repository {

    private TaskRepository mTaskRepository;
    private CalendarDayRepository mCalendarDayRepository;
    private ProjectDao mProjectDao;
    private RoutineRepository mRoutineRepository;

    @Inject
    public Repository(TaskRepository taskRepository, CalendarDayRepository calendarDayRepository, ProjectDao projectDao,
                      RoutineRepository routineRepository) {
        mTaskRepository = taskRepository;
        mCalendarDayRepository = calendarDayRepository;
        mProjectDao = projectDao;
        mRoutineRepository = routineRepository;
    }

    // -----------------------Task-----------------------

    public LiveData<List<Task>> getAllTasks() {
        return null;
    }

    public LiveData<Task> getTask(int id) {
        return mTaskRepository.getTask(id);
    }

    public LiveData<List<Task>> getTasks(List<Integer> taskIds) {
        return mTaskRepository.getTasks(taskIds);
    }

    public LiveData<List<Task>> getAllTasksFor7Days(List<Integer> taskIds) {
        return mTaskRepository.getAllTasksFor7Days(taskIds);
    }

    public LiveData<List<Task>> getAllRepeatedTasks() {
        return mTaskRepository.getAllRepeatedTasks();
    }


    public LiveData<List<Task>> getAllTasksOnDay(@Nullable List<Integer> taskIds, int repeatedDay) {
        return mTaskRepository.getAllTasksOnDay(taskIds, repeatedDay);
    }

    public LiveData<List<Task>> getTasksInQuickList() {
        return mTaskRepository.getTasksInQuickList();
    }

    public LiveData<List<Task>> getTasksInProject(int projectId) {
        return mTaskRepository.getTasksInProject(projectId);
    }

    public LiveData<List<Task>> getRoutineChildTasks(int routineId) {
        return mTaskRepository.getRoutineChildTasks(routineId);
    }

    public void insertTask(Task task) {
        mTaskRepository.insertTask(task);
    }

    public void updateTask(Task task) {
        mTaskRepository.updateTask(task);
    }

    public void updateBatchTasks(Task task) {
        mTaskRepository.updateBatchTasks(task);
    }

    public void deleteTask(Task task) {
        mTaskRepository.deleteTask(task);
    }

    public void deleteTasksInProject(int projectId) {
        mTaskRepository.deleteTasksInProject(projectId);
    }

    public void deleteTaskInRoutine(int routineId) {
        mTaskRepository.deleteTaskInRoutine(routineId);
    }

    // ----------------------Project----------------------

    public LiveData<Project> getProject(int id) {
        return mProjectDao.getProject(id);
    }

    public LiveData<List<Project>> getAllProjects() {
        return mProjectDao.getAllProjects();
    }

    public void insertProject(Project project) {
        new insertProjectAsync(mProjectDao).execute(project);
    }

    public void updateProject(Project project) {
        new updateProjectAsync(mProjectDao).execute(project);
    }

    public void deleteProject(Project project) {
        new deleteProjectAsyncTask(mProjectDao).execute(project);
    }

    // --------------------CalendarDay--------------------

    public LiveData<CalendarDay> getLiveCalendarDay(long date) {
        return mCalendarDayRepository.getLiveCalendarDay(date);
    }

    public LiveData<List<CalendarDay>> getNext7CalendarDays(List<Long> dates) {
        return mCalendarDayRepository.getNext7CalendarDays(dates);
    }


    public void insertCalendarDay(CalendarDay calendarDay) {
        mCalendarDayRepository.insertCalendarDay(calendarDay);
    }

    public void updateCalendarDay(CalendarDay calendarDay) {
        mCalendarDayRepository.updateCalendarDay(calendarDay);
    }

    // ---------------------Routine---------------------

    public void insertRoutine(Routine routine) {
        mRoutineRepository.insertRoutine(routine);
    }

    public void deleteRoutine(Routine routine) {
        mRoutineRepository.deleteRoutine(routine);
    }

    public LiveData<List<Routine>> getAllRoutines() {
        return mRoutineRepository.getAllRoutines();
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

    //DELETE PROJECT ASYNC
    private static class deleteProjectAsyncTask extends AsyncTask<Project, Void, Void> {

        private ProjectDao mAsyncDao;

        public deleteProjectAsyncTask(ProjectDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Project... projects) {
            mAsyncDao.deleteProject(projects[0]);
            return null;
        }
    }

    //DELETE TASKS IN PROJECT ASYNC
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

}
