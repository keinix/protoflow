package io.keinix.protoflow.addeddittask;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.data.source.TaskRepository;

public class AddEditTaskViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepository;

    // These vars are set in AddEditTaskActivity and are used to
    // create a new Task object
    private String mTaskName;
    @Nullable private String mTaskNotes;
    @Nullable private SparseBooleanArray mIsDaySelectedArray;
    private long mStartTimeUtc;
    private long mScheduledDateUtc;
    private int mTaskDurationInMinutes;


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

    //TODO: convert to unixTimeStamp
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

    public String getTaskName() {
        return mTaskName;
    }
    public void setTaskName(String taskName) {
        mTaskName = taskName;
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
}
