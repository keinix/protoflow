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
    private ProjectRepository mProjectRepository;
    private RoutineRepository mRoutineRepository;

    @Inject
    public Repository(TaskRepository taskRepository, CalendarDayRepository calendarDayRepository, ProjectRepository projectRepository,
                      RoutineRepository routineRepository) {
        mTaskRepository = taskRepository;
        mCalendarDayRepository = calendarDayRepository;
        mProjectRepository = projectRepository;
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
        return mProjectRepository.getProject(id);
    }

    public LiveData<List<Project>> getAllProjects() {
        return mProjectRepository.getAllProjects();
    }

    public void insertProject(Project project) {
        mProjectRepository.insertProject(project);
    }

    public void updateProject(Project project) {
        mProjectRepository.updateProject(project);
    }

    public void deleteProject(Project project) {
        mProjectRepository.deleteProject(project);
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

    public LiveData<List<Routine>> getAllRoutines() {
        return mRoutineRepository.getAllRoutines();
    }

    public void insertRoutine(Routine routine) {
        mRoutineRepository.insertRoutine(routine);
    }

    public void deleteRoutine(Routine routine) {
        mRoutineRepository.deleteRoutine(routine);
    }
}
