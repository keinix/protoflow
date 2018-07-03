package io.keinix.protoflow.tasks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

import static io.keinix.protoflow.tasks.TasksAdapter.DATE_HEADING;

public class TasksViewModel extends AndroidViewModel {

    // ----------Member variables------------
    private TaskRepository mTaskRepository;
    private LiveData<List<Task>> mAllTasks;
    private List<Long> mNext7DaysUtc;

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

    public List<Task> format7DayTasks(List<Task> tasks) {
        tasks = addDateToRepeatedTasks(tasks);
        tasks = sortTasksByDate(tasks);
        //tasks = addDaySeparatorItems(tasks);
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
        mNext7DaysUtc = new ArrayList<>();
        mNext7DaysUtc.add(calendar.getTimeInMillis());
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DATE, 1);
            mNext7DaysUtc.add(calendar.getTimeInMillis());
        }
        Log.d(TAG, "next 7 Dates: " + mNext7DaysUtc);
        return mNext7DaysUtc;
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

    private List<Task> addDateToRepeatedTasks(List<Task> tasks) {
        for (int i = tasks.size() -1; i >= 0; i--) {
            if (tasks.get(i).isRepeatsOnADay()) {
                tasks.remove(i);
                tasks.addAll(convertRepeatedTaskToTasksWithDates(tasks.get(i)));
            }
        }
        return tasks;
    }

    private List<Task> convertRepeatedTaskToTasksWithDates(Task task) {
        boolean taskWasScheduled = false;
        List<Task> convertedTasks = new ArrayList<>();
        if (task.getScheduledDateUtc() > 0) {
            convertedTasks.add(task);
            taskWasScheduled = true;
        }
        orderNext7DaysDatesFromMondayToSunday();

        if (task.isRepeatsOnMonday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(0));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        } if (task.isRepeatsOnTuesday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(1));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        } if (task.isRepeatsOnWednesday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(2));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        } if (task.isRepeatsOnThursday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(3));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        } if (task.isRepeatsOnFriday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(4));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        } if (task.isRepeatsOnSaturday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(5));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        } if (task.isRepeatsOnSunday()) {
            task.setScheduledDateUtc(mNext7DaysUtc.get(6));
            if (!taskWasScheduled || !convertedTasks.contains(task)) convertedTasks.add(task);
        }

        return convertedTasks;
    }

    private void orderNext7DaysDatesFromMondayToSunday() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < mNext7DaysUtc.size(); i++) {
            calendar.setTimeInMillis(mNext7DaysUtc.get(i));
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Collections.rotate(mNext7DaysUtc, i - 1);
                break;
            }
        }
    }

    // This method is called when looking at the 7 day view
    // mTasks contains the task for all 7 days this method adds a new task before each new day
    // DATE_HEADING task name will trigger a ViewHolder that displays a date separator
    private List<Task> addDaySeparatorItems(List<Task> tasks) {
        for (int i = 0; i < tasks.size() - 1; i++) {
            long date1 = tasks.get(i).getScheduledDateUtc();
            long date2 = tasks.get(i + 1).getScheduledDateUtc();
            if (date1 != date2) {
                Task task = new Task(DATE_HEADING);
                task.setScheduledDateUtc(date2);
                tasks.add(i, task);
            }
        }
        return tasks;
    }

    private List<Task> sortTasksByDate(List<Task> tasks) {
        tasks.sort((t1, t2) -> {
            if (t1.getScheduledDateUtc() > t2.getScheduledDateUtc()) {
                return 1;
            } else if (t1.getScheduledDateUtc() == t2.getScheduledDateUtc()) {
                return 0;
            } else {
                return -1;
            }
        });
        return tasks;
    }
}
