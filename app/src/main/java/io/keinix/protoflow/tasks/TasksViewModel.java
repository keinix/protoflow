package io.keinix.protoflow.tasks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;
import io.keinix.protoflow.util.ListItem;

import static io.keinix.protoflow.tasks.TasksAdapter.DATE_HEADING;

public class TasksViewModel extends AndroidViewModel {

    // ----------Member variables------------
    private LiveData<List<Task>> mAllTasks;
    private TaskRepository mTaskRepository;
    private List<Long> mNext7DaysUtc;
    private Project mProject;
    private SparseBooleanArray mRoutineHasObserver;
    private List<Routine> mCachedRoutines;

    public static final String TAG = TasksViewModel.class.getSimpleName();
    @Inject
    public TasksViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
        mAllTasks = mTaskRepository.getAllTasks();
        mRoutineHasObserver = new SparseBooleanArray();
    }

    // -------public: model layer bridge--------
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public void deleteTask(Task task) {
        mTaskRepository.deleteTask(task);
    }

    public void insertTask(Task task) {
        mTaskRepository.insertTask(task);
    }

    public LiveData<CalendarDay> getLiveCalendarDay(long date) {
        return mTaskRepository.getLiveCalendarDay(date);
    }

    public LiveData<List<Task>> getTasks(List<Integer> taskIds) {
        return mTaskRepository.getTasks(taskIds);
    }

    public LiveData<List<Task>> getTasksInQuickList() {
        return mTaskRepository.getTasksInQuickList();
    }

    public LiveData<List<CalendarDay>> getNext7CalendarDays() {
        return mTaskRepository.getNext7CalendarDays(getDatesForNext7Days());
    }

    public LiveData<List<Task>> getAllRepeatedTasks() {
        return mTaskRepository.getAllRepeatedTasks();
    }

    public LiveData<List<Task>> getTasksInProject(int projectId) {
        return mTaskRepository.getTasksInProject(projectId);
    }

    public LiveData<List<Project>> getAllProjects() {
        return mTaskRepository.getAllProjects();
    }

    public void insertProject(Project project) {
        mTaskRepository.insertProject(project);
    }

    public LiveData<List<Routine>> getAllRoutines() {
        return mTaskRepository.getAllRoutines();
    }

    public void insertRoutine(Routine routine) {
        mTaskRepository.insertRoutine(routine);
    }

    public LiveData<List<Task>> getChildTasksForRoutine(int routineId) {
        return mTaskRepository.getRoutineChildTasks(routineId);
    }

    public void updateTask(Task task) {
        mTaskRepository.updateTask(task);
    }

    /**
     * gets tasks scheduled for the day + tasks that repeat on that day
     * @param calendarDay you want to get the tasks from
     * @return all task on CalendarDay as well as task that repeat on that day
     */
    public LiveData<List<Task>> getAllTasksOnDay(CalendarDay calendarDay) {
        List<Integer> taskIds = calendarDay.getScheduledTaskIds();
        int repeatedDay = getDayOfWeek(calendarDay);
        return mTaskRepository.getAllTasksOnDay(taskIds, repeatedDay);
    }

    /**
     * get the repeated tasks that appear on a day if there were
     * no tasks specifically scheduled for that day
     * @param dayInMillis is used to get day constant from the Calendar class
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

    public List<Task> format7DayTasks(@NonNull List<Task> tasks) {
        tasks = addDateToRepeatedTasks(tasks);
        tasks = sortTasksByDate(tasks);
        tasks = addDaySeparatorItems(tasks);
        return tasks;
    }


    // NEW ------------->

    public void updateCachedRoutines(List<Routine> newRoutines) {
        if (mCachedRoutines == null) mCachedRoutines = new ArrayList<>();

        if (mCachedRoutines.size() == newRoutines.size()) {
            updateACachedRoutine(newRoutines);

        } else if (mCachedRoutines.size() < newRoutines.size()) {
            addToCachedRoutines(newRoutines);

        } else {
            removeSomethingFromCachedRoutines(newRoutines);
        }
    }

    @Nullable
    public List<ListItem> getRoutineListItems() {
        List<ListItem> listItems = new ArrayList<>();
        for (Routine routine : mCachedRoutines) {
            listItems.add(routine);
            if (routine.isExpanded() && routine.getCachedChildren() != null) {
                listItems.addAll(routine.getCachedChildren());
            }
        }
        return listItems;
    }


    public void updateRoutineExpandedValue(Routine routine) {
        for (Routine cachedRoutine : mCachedRoutines) {
            if (cachedRoutine.getId() == routine.getId()) {
                cachedRoutine.setExpanded(routine.isExpanded());
                break;
            }
        }
    }

    public boolean routineHasCachedChildren(Routine routine) {
     Routine routineBeingChecked = null;
     for (Routine cachedRoutine : mCachedRoutines) {
         if (cachedRoutine.getId() == routine.getId()) routineBeingChecked = cachedRoutine;
     }
     return routineBeingChecked.getCachedChildren() != null;
    }

    public void setCachedRoutineChildren(Routine routine, List<Task> children) {
        for (Routine cachedRoutine : mCachedRoutines) {
            if (cachedRoutine.getId() == routine.getId()) {
                cachedRoutine.setCachedChildren(children);
                break;
            }
        }
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
        orderNext7DaysDatesFromMondayToSunday();
        Log.d(TAG, "next 7 Dates after sort: " + mNext7DaysUtc);
        List<Task> outputTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isRepeatsOnADay()) {
                outputTasks.addAll(convertRepeatedTaskToTasksWithDates(tasks.get(i)));
            } else {
                outputTasks.add(tasks.get(i));
            }
        }
        return outputTasks;
    }

    // new tasks are created for each day the task repeats on so they display properly
    // in the 7 day view
    private List<Task> convertRepeatedTaskToTasksWithDates(Task task) {
        // addedDate used to make sure a task does not appear twice in
        //  7 day view if it was scheduled for a day it also repeats on
        long addedDate = -1;
        List<Task> convertedTasks = new ArrayList<>();
        if (task.getScheduledDateUtc() > 0) {
            convertedTasks.add(task);
            addedDate = task.getScheduledDateUtc();
        }

        if (task.isRepeatsOnMonday()) {
            if (addedDate != mNext7DaysUtc.get(0)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(0)));
            }
        } if (task.isRepeatsOnTuesday()) {
            if (addedDate != mNext7DaysUtc.get(1)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(1)));
            }
        } if (task.isRepeatsOnWednesday()) {
            if (addedDate != mNext7DaysUtc.get(2)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(2)));
            }
        } if (task.isRepeatsOnThursday()) {
            if (addedDate != mNext7DaysUtc.get(3)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(3)));
            }
        } if (task.isRepeatsOnFriday()) {
            if (addedDate != mNext7DaysUtc.get(4)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(4)));
            }
        } if (task.isRepeatsOnSaturday()) {
            if (addedDate != mNext7DaysUtc.get(5)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(5)));
            }
        } if (task.isRepeatsOnSunday()) {
            if (addedDate != mNext7DaysUtc.get(6)) {
                convertedTasks.add(task.cloneWithNewDate(mNext7DaysUtc.get(6)));
            }
        }
        return convertedTasks;
    }

    // mNext7DaysUtc is sorted so repeated tasks that have no Date can be given one
    // so they appear under the correct day in the 7 day view
    private void orderNext7DaysDatesFromMondayToSunday() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < mNext7DaysUtc.size(); i++) {
            calendar.setTimeInMillis(mNext7DaysUtc.get(i));
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                int rotateDistance = mNext7DaysUtc.size() - i;
                Collections.rotate(mNext7DaysUtc, rotateDistance);
                break;
            }
        }
    }

    // This method is called when looking at the 7 day view
    //  this method adds a new task before each new day
    // DATE_HEADING task name will trigger a ViewHolder that displays a date separator
    private List<Task> addDaySeparatorItems(@NonNull List<Task> tasks) {
        List<Task> outputTasks = new ArrayList<>();
        Task firstDateSeparator = new Task(DATE_HEADING);
        firstDateSeparator.setScheduledDateUtc(tasks.get(0).getScheduledDateUtc());
        outputTasks.add(firstDateSeparator);
        outputTasks.add(tasks.get(0));

        for (int i = 0; i < tasks.size() - 1; i++) {
            long date1 = tasks.get(i).getScheduledDateUtc();
            long date2 = tasks.get(i + 1).getScheduledDateUtc();
            if (date1 != date2) {
                Task dateSeparator = new Task(DATE_HEADING);
                dateSeparator.setScheduledDateUtc(date2);
                outputTasks.add(dateSeparator);
            }
            outputTasks.add(tasks.get(i + 1));
        }
        return outputTasks;
    }

    private List<Task> sortTasksByDate(List<Task> tasks) {
        tasks.sort(Comparator.comparingLong(Task::getScheduledDateUtc));
        return tasks;
    }

    // used to create a List of items that will appear in the recycler view
    private List<? extends ListItem> convertRoutineMapToList() {
        return null;
    }

    private void removeSomethingFromCachedRoutines(List<Routine> newRoutines) {
        // routine deleted
        Routine routineToRemove = null;
        for (Routine routine : mCachedRoutines) {
            if (!newRoutines.contains(routine)) {
                routineToRemove = routine;
                break;
            }
        }
        if (routineToRemove !=null )mCachedRoutines.remove(routineToRemove);
    }

    private void addToCachedRoutines(List<Routine> newRoutines) {
        // routine added
        for (Routine routine : newRoutines) {
            if (!mCachedRoutines.contains(routine)) mCachedRoutines.add(routine);
        }
    }

    private void updateACachedRoutine(List<Routine> newRoutines) {
        // routine Updated
        for (int i = 0; i < mCachedRoutines.size(); i++) {
            mCachedRoutines.get(i).setName(newRoutines.get(i).getName());
        }
    }

    // -------getters and setters--------

    public Project getProject() {
        return mProject;
    }

    public void setProject(Project project) {
        mProject = project;
    }
}
