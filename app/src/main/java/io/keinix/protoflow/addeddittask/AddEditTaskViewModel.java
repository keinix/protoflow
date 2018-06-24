package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import java.text.DateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepository;

    // ------------create a new Task object------------
    // These vars are set in AddEditTaskActivity and are used to
    @Nullable private String mTaskNotes;
    // Maps the day TextView id to if its selected (boolean)
    @Nullable private SparseBooleanArray mIsDaySelectedArray;
    private long mScheduledDateUtc;
    private int mTaskDurationInMinutes;
    private long mStartTimeUtc;

    //used to persists the UI state

    private static final int MILISECONDS_IN_HOUR = 3600000;
    private static final int MINISECONDS_IN_MINUTE = 60000;
    private static final String TAG = AddEditTaskViewModel.class.getSimpleName();


    @Inject
    public AddEditTaskViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
    }

    // -------public: model layer bridge--------

    void insertTask(Task task) {
        mTaskRepository.insertTask(task);
        Log.d(TAG, task.toString());
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

    public String parseDurationForTimeStamp(int hours, int minutes) {
        String minutesString = minutes == 1 ? "Minute" : "Minutes";
        String hoursString = hours > 0 ?  hours + " Hours" : "";
        if (hours == 1) hoursString = hoursString.replace("s", "");
        setTaskDurationInMinutes((hours * 60) + minutes);
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
        // this will be parsed into unix timer later when the Add/Edit is complete
        mStartTimeUtc = parseUnixStartTime(hour, minute);
        return String.format("%s:%02d %s", hour, minute, timeSuffix);
    }

    public String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        Log.d(TAG, "calendar date: " + calendar.getTimeInMillis());
        setScheduledDateUtc(calendar.getTimeInMillis());
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
    }

    public void createTask(@NonNull String taskName) {
        Task task = new Task(taskName);
        if (mTaskNotes != null) {
            task.setNotes(mTaskNotes);
        } else if (mIsDaySelectedArray != null) {
            setRepeatedDaysInTask(task);
        }
        task.setScheduledDateUtc(mScheduledDateUtc);
        task.setStartTimeUtc(mStartTimeUtc);
        task.setDurationInMinutes(mTaskDurationInMinutes);
        insertTask(task);
    }


    // --------------private-----------------

    private long parseUnixStartTime(int hours, int minutes) {
        if ((minutes + hours) == 0) return 0;
        if (mScheduledDateUtc == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        } else {
            long timeOffSet = (hours * MILISECONDS_IN_HOUR) +
                    (minutes * MINISECONDS_IN_MINUTE);
            return mScheduledDateUtc + timeOffSet;
        }
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
    @Nullable
    public String getTaskDurationTimeStamp() {
        int hours = mTaskDurationInMinutes /60;
        int minutes = mTaskDurationInMinutes % 60;
        return parseDurationForTimeStamp(hours, minutes);
    }

    @Nullable
    public String getTaskStartTimeStamp(boolean is24Hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mStartTimeUtc);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return parseStartTimeForTimeStamp(hour, minute, is24Hour);
    }

    @Nullable
    public String getTaskStartDateTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mScheduledDateUtc);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        return formatDate(year, month, day);
    }

    public long getScheduledDateUtc() {
        return mScheduledDateUtc;
    }

    public void setStartTimeUtc(long startTimeUtc) {
        mStartTimeUtc = startTimeUtc;
    }

    public long getStartTimeUtc() {
        return mStartTimeUtc;
    }

    public int getTaskDurationInMinutes() {
        return mTaskDurationInMinutes;
    }

    public void setTaskDurationInMinutes(int taskDurationInMinutes) {
        mTaskDurationInMinutes = taskDurationInMinutes;
    }

    public void setScheduledDateUtc(long scheduledDateUtc) {
        mScheduledDateUtc = scheduledDateUtc;
    }

    public void setTaskNotes(@Nullable String taskNotes) {
        mTaskNotes = taskNotes;
    }

    public void setIsDaySelectedArray(@Nullable SparseBooleanArray isDaySelectedArray) {
        mIsDaySelectedArray = isDaySelectedArray;
    }

    @Nullable
    public SparseBooleanArray getIsDaySelectedArray() {
        return mIsDaySelectedArray;
    }
}
