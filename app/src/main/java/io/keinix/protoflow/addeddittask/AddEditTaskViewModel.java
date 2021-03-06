package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.Repository;
import io.keinix.protoflow.scheduling.NotificationScheduler;

public class AddEditTaskViewModel extends AndroidViewModel {

    private Repository mRepository;

    // ------------create a new Task object------------
    // These vars are set in AddEditTaskActivity and are used to
    @Nullable private String mTaskNotes;
    // Maps the day TextView id to if its selected (boolean)
    @Nullable private SparseBooleanArray mIsDaySelectedArray;
    private long mStartDateUtc;
    private int mTaskDurationInMinutes;
    private long mStartTimeUtc;
    private boolean isInQuickList;
    private Project mProject;
    private Routine mRoutine;

    private static final int MILISECONDS_IN_HOUR = 3600000;
    private static final int MINISECONDS_IN_MINUTE = 60000;
    private boolean taskIsBeingEdited;
    private static final String TAG = AddEditTaskViewModel.class.getSimpleName();

    @Inject
    public AddEditTaskViewModel(@NonNull Application application, Repository repository) {
        super(application);
        mRepository = repository;
    }

    // -------public: model layer bridge--------

    void insertTask(Task task) {
        mRepository.insertTask(task);
    }

    void updateTask(Task task) {
        mRepository.updateTask(task);
    }

    LiveData<Task> getTaskToEdit(int id) {
        return mRepository.getTask(id);
    }

    LiveData<List<Project>> getAllProjects() {
        return mRepository.getAllProjects();
    }

    public LiveData<Project> getProject(int id) {
        return mRepository.getProject(id);
    }

    // -----------public: view layer------------

    /**
     * @param id the id of a day TextView
     * @return if day was selected before click
     */
    public Boolean isDaySelected(int id) {
        if (mIsDaySelectedArray.get(id)) {
            mIsDaySelectedArray.put(id, false);
            return true;
        } else {
            mIsDaySelectedArray.put(id, true);
            return false;
        }
    }

    /**
     * called in onCreate() of {@link AddEditTaskActivity}
     * @param days a list of textViews that represent the repeated days
     */
    public void initNewIsDaySelectedArray(List<TextView> days) {
        if (mIsDaySelectedArray == null) {
            mIsDaySelectedArray = new SparseBooleanArray();
            for (TextView day : days) {
                mIsDaySelectedArray.put(day.getId(), true);
            }
        }
    }

    /**
     * @param task that will be edited in {@link AddEditTaskActivity}
     */
    public void setRepeatedDaysInViewModelFromTask(Task task) {
        if (mIsDaySelectedArray != null) {
            if (!task.isRepeatsOnMonday()) {
                mIsDaySelectedArray.put(R.id.text_view_repeat_monday, false);
            } if (!task.isRepeatsOnTuesday()) {
                  mIsDaySelectedArray.put(R.id.text_view_repeat_tuesday, false);
            } if (!task.isRepeatsOnWednesday()) {
                  mIsDaySelectedArray.put(R.id.text_view_repeat_wednesday, false);
            } if (!task.isRepeatsOnThursday()) {
                  mIsDaySelectedArray.put(R.id.text_view_repeat_thursday, false);
            } if (!task.isRepeatsOnFriday()) {
                  mIsDaySelectedArray.put(R.id.text_view_repeat_friday, false);
            } if (!task.isRepeatsOnSaturday()) {
                  mIsDaySelectedArray.put(R.id.text_view_repeat_saturday, false);
            } if (!task.isRepeatsOnSunday()) {
                  mIsDaySelectedArray.put(R.id.text_view_repeat_sunday, false);
            }
        } else {
            throw new NullPointerException("call initNewISDaySelectedArray() before " +
                    "calling setRepeatedDaysInViewModelFromTask()");
        }
    }

    public String parseDurationForTimeStamp(int hours, int minutes) {
        String minutesString = minutes == 1 ? "Minute" : "Minutes";
        String hoursString = hours > 0 ?  hours + " Hours" : "";
        if (hours == 1) hoursString = hoursString.replace("s", "");
        return String.format("%s %s %s", hoursString, minutes, minutesString);
    }

    public String parseStartTimeForTimeStamp(int hour, int minute, boolean is24HourClock) {
        String timeSuffix = "";
        if (!is24HourClock) {
            timeSuffix = hour < 12 ? "AM" : "PM";
            if (hour > 12) {
                hour -= 12;
            } else if (hour == 0) {
                hour = 12;
            }
        }
        return String.format("%s:%02d %s", hour, minute, timeSuffix);
    }

    public String parseStartDateForTimeStamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
    }

    public void createNewTask(@NonNull String taskName) {
        Task task = initNewTask(taskName);
        insertTask(task);
    }

    public void updateExistingTask(@NonNull String taskName, int id) {
        Task task = initNewTask(taskName);
        task.setId(id);
        updateTask(task);
    }

    public void setViewModelVariablesFromTask(Task task) {
        taskIsBeingEdited = true;
        setStartTimeUtc(task.getStartTimeUtc());
        setTaskDurationInMinutes(task.getDurationInMinutes());
        setStartDateUtc(task.getScheduledDateUtc());
        if (task.isRepeatsOnADay()) setRepeatedDaysInViewModelFromTask(task);
    }


    // --------------private-----------------

    private Task initNewTask(String taskName) {
        Task task = new Task(taskName);
        if (mTaskNotes != null) {
            task.setNotes(mTaskNotes);
        }
        if (mIsDaySelectedArray != null) {
            setRepeatedDaysInTask(task);
        }
        if (mProject != null) {
            task.setProjectId(mProject.getId());
        }
        task.setInQuickList(isInQuickList);
        task.setScheduledDateUtc(mStartDateUtc);
        Log.d(TAG, "Start Date that is being set in AddEditTask: " + mStartDateUtc);
        if (mStartTimeUtc > 0) {
            Log.d("FINDME", "start Time in AddEditViewModel: " + mStartTimeUtc);
            task.setStartTimeUtc(mStartTimeUtc);
            scheduleTaskNotification(task);
        }
        task.setDurationInMinutes(mTaskDurationInMinutes);
        if (mRoutine != null) task.setRoutineId(mRoutine.getId());
        return task;
    }

    private void scheduleTaskNotification(Task task) {
        NotificationScheduler.scheduleNotification(task);
    }

    private long parseUnixStartTime(int hours, int minutes) {
        if ((minutes + hours) == 0) return 0;
        if (mStartDateUtc == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        } else {
            long timeOffSet = (hours * MILISECONDS_IN_HOUR) +
                    (minutes * MINISECONDS_IN_MINUTE);
            return mStartDateUtc + timeOffSet;
        }
    }

    private void updateStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mStartTimeUtc);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        long newStartTime = mStartDateUtc + (long) hours + (long) minutes;
        setStartTimeUtc(newStartTime);
    }

    private void setRepeatedDaysInTask(Task task) {
        for (int i = 0; i < mIsDaySelectedArray.size(); i++) {
            boolean dayIsRepeated = mIsDaySelectedArray.valueAt(i);
            if (dayIsRepeated) {
                setRepeatedDayByViewId(task, mIsDaySelectedArray.keyAt(i));
            }
        }
    }

    private void setRepeatedDayByViewId(Task task, int id) {
        task.setRepeatsOnADay(true);
        switch (id) {
            case R.id.text_view_repeat_monday:
                task.setRepeatsOnMonday(true);
                break;
            case R.id.text_view_repeat_tuesday:
                task.setRepeatsOnTuesday(true);
                break;
            case R.id.text_view_repeat_wednesday:
                task.setRepeatsOnWednesday(true);
                break;
            case R.id.text_view_repeat_thursday:
                task.setRepeatsOnThursday(true);
                break;
            case R.id.text_view_repeat_friday:
                task.setRepeatsOnFriday(true);
                break;
            case R.id.text_view_repeat_saturday:
                task.setRepeatsOnSaturday(true);
                break;
            case R.id.text_view_repeat_sunday:
                task.setRepeatsOnSunday(true);
                break;
        }
    }


    // -----------getters & setters--------------

    /**
     * @return duration string for UI parsed from total time in minutes
     * call this method when config changes in {@link AddEditTaskActivity}
     * or where the activity is loaded to Edit a task
     */
    public String getTaskDurationTimeStamp() {
        int hours = mTaskDurationInMinutes / 60;
        int minutes = mTaskDurationInMinutes % 60;
        return parseDurationForTimeStamp(hours, minutes);
    }

    public String getTaskStartTimeStamp(boolean is24Hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mStartTimeUtc);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return parseStartTimeForTimeStamp(hour, minute, is24Hour);
    }

    public String getTaskStartDateTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mStartDateUtc);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        return parseStartDateForTimeStamp(year, month, day);
    }

    //overloaded
    public void setStartTimeUtc(long startTimeUtc) {
        mStartTimeUtc = startTimeUtc;
    }


    //overloaded
    public void setStartTimeUtc(int hour, int minute) {
//        if (hour > 12) {
//            hour -= 12;
//        } else if (hour == 0) {
//            hour = 12;
//        }
        setStartTimeUtc(parseUnixStartTime(hour, minute));
    }

    //overloaded
    public void setStartDateUtc(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        setStartDateUtc(calendar.getTimeInMillis());
        if (mStartTimeUtc > 0) updateStartTime();
    }


    //overloaded
    public void setStartDateUtc(long startDateUtc) {
        mStartDateUtc = startDateUtc;
    }

    //overloaded
    public void setTaskDurationInMinutes(int taskDurationInMinutes) {
        mTaskDurationInMinutes = taskDurationInMinutes;
    }

    //overloaded
    public void setTaskDurationInMinutes(int hours, int minutes) {
        setTaskDurationInMinutes((hours * 60) + minutes);
    }
    public void setIsDaySelectedArray(@Nullable SparseBooleanArray isDaySelectedArray) {
        mIsDaySelectedArray = isDaySelectedArray;
    }

    public void setTaskNotes(@Nullable String taskNotes) {
        mTaskNotes = taskNotes;
    }

    public long getStartTimeUtc() {
        return mStartTimeUtc;
    }

    public int getTaskDurationInMinutes() {
        return mTaskDurationInMinutes;
    }

    public long getStartDateUtc() {
        return mStartDateUtc;
    }

    @Nullable
    public SparseBooleanArray getIsDaySelectedArray() {
        return mIsDaySelectedArray;
    }

    public Project getProject() {
        return mProject;
    }

    public void setProject(Project project) {
        mProject = project;
    }

    public Routine getRoutine() {
        return mRoutine;
    }

    public void setRoutine(Routine routine) {
        mRoutine = routine;
    }

    public boolean isInQuickList() {
        return isInQuickList;
    }

    public void setInQuickList(boolean inQuickList) {
        isInQuickList = inQuickList;
    }
}
