package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepository;

    // These vars are set in AddEditTaskActivity and are used to
    // create a new Task object
    @Nullable private String mTaskNotes;
    @Nullable private SparseBooleanArray mIsDaySelectedArray;
    private long mStartTimeUtc;
    private long mScheduledDateUtc;
    private int mTaskDurationInMinutes;
    private int mStartTimeHours;
    private int mStartTimeMinutes;

    public static final int MILISECONDS_IN_HOUR = 3600000;
    public static final int MINISECONDS_IN_MINUTE = 60000;


    @Inject
    public AddEditTaskViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
    }

    void addTask(Task task) {
        mTaskRepository.insertTask(task);
    }

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
        setStartTime(hour, minute);
        return String.format("%s:%02d %s", hour, minute, timeSuffix);
    }

    public String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        setScheduledDateUtc(calendar.getTimeInMillis());
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
    }

    private void parseUnixStartTime() {
        if (mScheduledDateUtc == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, mStartTimeHours);
            calendar.set(Calendar.MINUTE, mStartTimeMinutes);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            mStartTimeUtc = calendar.getTimeInMillis();
        } else {
            long timeOffSet = (mStartTimeHours * MILISECONDS_IN_HOUR) +
                    (mStartTimeMinutes * MINISECONDS_IN_MINUTE);
            mStartTimeUtc = mScheduledDateUtc + timeOffSet;
        }

    }

    public void createTask(@NonNull String taskName) {
        Task task = new Task(taskName);
    }


    public int getTaskDurationInMinutes() {
        return mTaskDurationInMinutes;
    }

    public void setTaskDurationInMinutes(int taskDurationInMinutes) {
        mTaskDurationInMinutes = taskDurationInMinutes;
    }

    public long getStartTimeUtc() {
        return mStartTimeUtc;
    }

    public void setStartTimeUtc(long startTimeUtc) {
        mStartTimeUtc = startTimeUtc;
    }

    public long getScheduledDateUtc() {
        return mScheduledDateUtc;
    }

    public void setScheduledDateUtc(long scheduledDateUtc) {
        mScheduledDateUtc = scheduledDateUtc;
    }

    /**
     * When the Task is finished being edited this will be converted to
     * mStartTimeUtc in parseUnixStarTime()
     * @param hours for the start time
     * @param minutes for the start time
     *
     */
    public void setStartTime(int hours, int minutes) {
        mStartTimeHours = hours;
        mStartTimeMinutes = minutes;
    }
}
