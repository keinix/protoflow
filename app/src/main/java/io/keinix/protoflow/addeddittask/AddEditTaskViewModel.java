package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
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
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepository;

    // These vars are set in AddEditTaskActivity and are used to
    // create a new Task object
    @Nullable private String mTaskNotes;
    // Maps the day TextView id to if its selected (boolean)
    @Nullable private SparseBooleanArray mIsDaySelectedArray;
    private long mScheduledDateUtc;
    private int mStartTimeHours;
    private int mStartTimeMinutes;
    private int mTaskDurationInMinutes;

    public static final int MILISECONDS_IN_HOUR = 3600000;
    public static final int MINISECONDS_IN_MINUTE = 60000;
    public static final String TAG = AddEditTaskViewModel.class.getSimpleName();


    @Inject
    public AddEditTaskViewModel(@NonNull Application application, TaskRepository taskRepository) {
        super(application);
        mTaskRepository = taskRepository;
    }


    void insertTask(Task task) {
        mTaskRepository.insertTask(task);
        Log.d(TAG, task.toString());
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
        task.setStartTimeUtc(parseUnixStartTime());
        task.setDurationInMinutes(mTaskDurationInMinutes);
        insertTask(task);
    }

    private long parseUnixStartTime() {
        if (mScheduledDateUtc == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, mStartTimeHours);
            calendar.set(Calendar.MINUTE, mStartTimeMinutes);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        } else {
            long timeOffSet = (mStartTimeHours * MILISECONDS_IN_HOUR) +
                    (mStartTimeMinutes * MINISECONDS_IN_MINUTE);
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


    public int getTaskDurationInMinutes() {
        return mTaskDurationInMinutes;
    }

    public void setTaskDurationInMinutes(int taskDurationInMinutes) {
        mTaskDurationInMinutes = taskDurationInMinutes;
    }


    public long getScheduledDateUtc() {
        return mScheduledDateUtc;
    }

    public void setScheduledDateUtc(long scheduledDateUtc) {
        mScheduledDateUtc = scheduledDateUtc;
    }

    @Nullable
    public String getTaskNotes() {
        return mTaskNotes;
    }

    public void setTaskNotes(@Nullable String taskNotes) {
        mTaskNotes = taskNotes;
    }

    @Nullable
    public SparseBooleanArray getIsDaySelectedArray() {
        return mIsDaySelectedArray;
    }

    public void setIsDaySelectedArray(@Nullable SparseBooleanArray isDaySelectedArray) {
        mIsDaySelectedArray = isDaySelectedArray;
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
