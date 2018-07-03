package io.keinix.protoflow.tasks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class TasksViewModel extends AndroidViewModel {

    // ----------Member variables------------
    private TaskRepository mTaskRepository;
    private LiveData<List<Task>> mAllTasks;
    private List<Long> next7DaysUtc;

    public static final String TAG = TasksViewModel.class.getSimpleName();
    @Inject
    public TasksViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
        mAllTasks = mTaskRepository.getAllTasks();
    }

    // -------public: model layer bridge--------
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<CalendarDay> getLiveCalendarDay(long date) {
        return mTaskRepository.getLiveCalendarDay(date);
    }

    public LiveData<List<Task>> getTasks(List<Integer> taskIds) {
        return mTaskRepository.getTasks(taskIds);
    }

    public LiveData<List<CalendarDay>> getNext7CalendarDays() {
        return mTaskRepository.getNext7CalendarDays(getDatesForNext7Days());
    }

    /**
     * @param calendarDay you want to get the tasks from
     * @return all task on CalendarDay as well as task that repeat on that day
     */
    public LiveData<List<Task>> getAllTasksOnDay(CalendarDay calendarDay) {
        List<Integer> taskIds = calendarDay.getScheduledTaskIds();
        int repeatedDay = getDayOfWeek(calendarDay);
        return mTaskRepository.getAllTasksOnDay(taskIds, repeatedDay);
    }

    /**
     * @param dayInMillis is a day constant from the Calendar class
     * @return all tasks that repeat on the given day
     */
    public LiveData<List<Task>> getAllTasksOnDay(long dayInMillis) {
        int repeatedDay = getDayOfWeek(dayInMillis);
        return mTaskRepository.getAllTasksOnDay(null, repeatedDay);
    }

    public LiveData<List<Task>> getAllTasksFor7Days(List<CalendarDay> calendarDays) {
        List<Integer> taskIds = new ArrayList<>();
        for (CalendarDay calendarDay : calendarDays) {
            taskIds.addAll(calendarDay.getScheduledTaskIds());
        }
        return mTaskRepository.getAllTasksFor7Days(taskIds);
    }

    // -----------------public------------------

    public List<Task> sort7DayTasksByDay(List<Task> tasks) {
        return tasks;
    }


    // ----------------private-----------------

    private int getDayOfWeek(CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarDay.getDate());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private int getDayOfWeek(long dayInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dayInMillis);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private List<Long> getDatesForNext7Days() {
        Calendar calendar = getCalendarForFirstOf7Days();
        next7DaysUtc = new ArrayList<>();
        next7DaysUtc.add(calendar.getTimeInMillis());
        for (int i = 0; i <= 7; i++) {
            calendar.add(Calendar.DATE, 1);
            next7DaysUtc.add(calendar.getTimeInMillis());
        }
        Log.d(TAG, "next 7 Dates: " + next7DaysUtc);
        return next7DaysUtc;
    }

    private Calendar getCalendarForFirstOf7Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
