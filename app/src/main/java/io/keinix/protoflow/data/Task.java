package io.keinix.protoflow.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ohoussein.playpause.PlayPauseView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.keinix.protoflow.tasks.TaskCountDownTimer;
import io.keinix.protoflow.util.ListItem;
import io.keinix.protoflow.util.RoomTypeConverters;

@TypeConverters({RoomTypeConverters.class})
@Entity(tableName = "task_table"
//    foreignKeys = {
//        @ForeignKey(entity = Project.class,
//                parentColumns = "id",
//                childColumns = "project_id",
//                onDelete = CASCADE)}
//
//        @ForeignKey(entity = Routine.class,
//                parentColumns = "id",
//                childColumns = "routine_id",
//                onDelete = CASCADE)
//    }
    )
public class Task implements ListItem {


    //TODO:might need to add column info
    @PrimaryKey (autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "project_id")
    private int projectId;

    @ColumnInfo( name = "routine_id")
    private int routineId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "routines")
    private String routines;

    @ColumnInfo(name = "duration")
    private int durationInMinutes;

    @ColumnInfo(name = "scheduled_date")
    private long scheduledDateUtc;

    @ColumnInfo(name = "start_time")
    private long startTimeUtc;

    @Nullable
    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "repeats_on_a_day")
    private boolean repeatsOnADay;

    @ColumnInfo(name = "repeats_on_monday")
    private boolean repeatsOnMonday;

    @ColumnInfo(name = "repeats_on_tuesday")
    private boolean repeatsOnTuesday;

    @ColumnInfo(name = "repeats_on_wednesday")
    private boolean repeatsOnWednesday;

    @ColumnInfo(name = "repeats_on_thursday")
    private boolean repeatsOnThursday;

    @ColumnInfo(name = "repeats_on_friday")
    private boolean repeatsOnFriday;

    @ColumnInfo(name = "repeats_on_saturday")
    private boolean repeatsOnSaturday;

    @ColumnInfo(name = "repeats_on_sunday")
    private boolean repeatsOnSunday;

    @ColumnInfo(name = "is_in_quick_list")
    private boolean isInQuickList;

    @ColumnInfo(name = "is_task_complete")
    private boolean isTaskComplete;

    @ColumnInfo(name = "is_task_complete_in_quick_list")
    private boolean isTaskCompleteInQuickList;

    // Since the state of a complete Task is saved in the corresponding CalendarDay
    // or project Object when a task is completed the Task object itself does not change
    // so it is not flagged for rebinding by DiffUtitls This counter is used to make a change in
    // the task so the view will be rebound in the RecyclerView.Adapter
    @Ignore
    private boolean completionStatusChange;

    @Ignore
    @Nullable
    private TaskCountDownTimer mCountDownTimer;


    // A task can repeat on a given day. This is a map of the 7 days to
    // a list of exact dates that a repeated task was completed on
    @ColumnInfo(name = "repeated_task_completion_dates")
    private HashMap<Integer, List<Long>> repeatedTaskCompletionDate;

    public Task(@NonNull String name) {
        this.name = name;
        if (repeatedTaskCompletionDate == null) repeatedTaskCompletionDate = new HashMap<>();
        // -1 is set b/c the 0 value will cause them to be pulled for
        // projects/routines that have have a 0 id
        projectId = -1;
        routineId = -1;
    }

    @Override
    public int getItemType() {
        return ListItem.TYPE_TASK;
    }

    /**
     * this methods is used in the 7 days view to take a single task that repeats
     * on multiple days and transform it into individual Task objects to be
     * displayed in the recycler view.
     * @param date
     * @return a cloned task with the date changed
     */
    public Task cloneWithNewDate(long date) {
        Task newTask = new Task(getName());
        newTask.setId(getId());
        newTask.setRoutines(getRoutines());
        newTask.setDurationInMinutes(getDurationInMinutes());
        newTask.setScheduledDateUtc(date);
        newTask.setStartTimeUtc(getStartTimeUtc());
        newTask.setNotes(getNotes());
        newTask.setRepeatsOnADay(isRepeatsOnADay());
        newTask.setRepeatsOnMonday(isRepeatsOnMonday());
        newTask.setRepeatsOnTuesday(isRepeatsOnTuesday());
        newTask.setRepeatsOnWednesday(isRepeatsOnWednesday());
        newTask.setRepeatsOnThursday(isRepeatsOnThursday());
        newTask.setRepeatsOnFriday(isRepeatsOnFriday());
        newTask.setRepeatsOnSaturday(isRepeatsOnSaturday());
        newTask.setRepeatsOnSunday(isRepeatsOnSunday());
        newTask.setTaskComplete(isTaskComplete());
        newTask.setRepeatedTaskCompletionDate(getRepeatedTaskCompletionDate());
        return newTask;
    }


    // Getters and Setters
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(@Nullable int projectId) {
        this.projectId = projectId;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getRoutines() {
        return routines;
    }

    public void setRoutines(@Nullable String routines) {
        this.routines = routines;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public long getScheduledDateUtc() {
        return scheduledDateUtc;
    }

    public void setScheduledDateUtc(long scheduledDateUtc) {
        this.scheduledDateUtc = scheduledDateUtc;
    }

    public long getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(long startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public boolean isRepeatsOnMonday() {
        return repeatsOnMonday;
    }

    public void setRepeatsOnMonday(boolean repeatsOnMonday) {
        this.repeatsOnMonday = repeatsOnMonday;
    }

    public boolean isRepeatsOnTuesday() {
        return repeatsOnTuesday;
    }

    public void setRepeatsOnTuesday(boolean repeatsOnTuesday) {
        this.repeatsOnTuesday = repeatsOnTuesday;
    }

    public boolean isRepeatsOnWednesday() {
        return repeatsOnWednesday;
    }

    public void setRepeatsOnWednesday(boolean repeatsOnWednesday) {
        this.repeatsOnWednesday = repeatsOnWednesday;
    }

    public boolean isRepeatsOnThursday() {
        return repeatsOnThursday;
    }

    public void setRepeatsOnThursday(boolean repeatsOnThursday) {
        this.repeatsOnThursday = repeatsOnThursday;
    }

    public boolean isRepeatsOnFriday() {
        return repeatsOnFriday;
    }

    public void setRepeatsOnFriday(boolean repeatsOnFriday) {
        this.repeatsOnFriday = repeatsOnFriday;
    }

    public boolean isRepeatsOnSaturday() {
        return repeatsOnSaturday;
    }

    public void setRepeatsOnSaturday(boolean repeatsOnSaturday) {
        this.repeatsOnSaturday = repeatsOnSaturday;
    }

    public boolean isRepeatsOnSunday() {
        return repeatsOnSunday;
    }

    public void setRepeatsOnSunday(boolean repeatsOnSunday) {
        this.repeatsOnSunday = repeatsOnSunday;
    }

    public boolean isRepeatsOnADay() {
        return repeatsOnADay;
    }

    public void setRepeatsOnADay(boolean repeatsOnADay) {
        this.repeatsOnADay = repeatsOnADay;
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public boolean isInQuickList() {
        return isInQuickList;
    }

    public void setInQuickList(boolean inQuickList) {
        isInQuickList = inQuickList;
    }

    public void setTaskComplete(boolean taskComplete) {
        isTaskComplete = taskComplete;
    }

    public HashMap<Integer, List<Long>> getRepeatedTaskCompletionDate() {
        return repeatedTaskCompletionDate;
    }

    public void setRepeatedTaskCompletionDate(@Nullable HashMap<Integer, List<Long>> repeatedTaskCompletionDate) {
        this.repeatedTaskCompletionDate = repeatedTaskCompletionDate;
    }

    public boolean isTaskCompleteInQuickList() {
        return isTaskCompleteInQuickList;
    }

    public void setTaskCompleteInQuickList(boolean taskCompleteInQuickList) {
        isTaskCompleteInQuickList = taskCompleteInQuickList;
    }



    // ------- TaskCountDown State --------
    // TaskCountDownTimer is not persisted through process death
    // the current state of a Task's timer is persisted in TaskViewModel on
    // config changes

    // create a new timer
    public void setCountdownTimer(PlayPauseView playButton, ProgressBar progressBar, TextView durationTextView) {
       mCountDownTimer = new TaskCountDownTimer(this, playButton, progressBar, durationTextView);
    }

    // restore a timer in progress on config change
    public void restoreCountDownTimer(Bundle bundle, PlayPauseView playButton, ProgressBar progressBar, TextView durationTextView) {
        mCountDownTimer = new TaskCountDownTimer(this, playButton, progressBar, durationTextView);
        mCountDownTimer.restoreTimerValues(bundle);
    }

    public void toggleCountdown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.toggleCountDown();
        }
    }

    public boolean isCompletionStatusChange() {
        return completionStatusChange;
    }

    public void setCompletionStatusChange(boolean completionStatusChange) {
        this.completionStatusChange = completionStatusChange;
    }

    /**
     * Used to persist a Task's {@link TaskCountDownTimer}
     * @return A bundle representing the timer's current state
     */
    @Nullable
    public Bundle getCountdownTimerValues() {
        return mCountDownTimer.getTimerValues();
    }

    public void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancelTimer();
        }
    }

    // call to check if a timer has been started
    public long getElapsedMillis() {
        if (mCountDownTimer == null) return 0;
        return mCountDownTimer.getMillisElapsed();
    }

    public void toggleTaskComplete() {
        if (repeatsOnADay) throw  new IllegalArgumentException("use ToggleRepeatedTaskComplete() " +
                "instead if the Task repeats on a day.");
        isTaskComplete = !isTaskComplete;
    }

    public void toggleRepeatedTaskComplete(long date) {
        if (!repeatsOnADay) {
            toggleTaskComplete();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (repeatedTaskCompletionDate.get(day) == null ||
                !repeatedTaskCompletionDate.get(day).contains(date)) {
            markRepeatedTaskComplete(day, date);
        } else {
            markRepeatedTaskIncomplete(day, date);
        }
    }

    private void markRepeatedTaskIncomplete(int day, long date) {
        repeatedTaskCompletionDate.get(day).remove(date);
    }

    private void markRepeatedTaskComplete(int day, long date) {
        if (repeatedTaskCompletionDate.get(day) == null) {
            List<Long> list = new ArrayList<>();
            list.add(date);
            repeatedTaskCompletionDate.put(day, list);
        } else {
            repeatedTaskCompletionDate.get(day).add(date);
        }
    }

    public boolean isTaskComplete() {
//        if (isRepeatsOnADay()) throw new IllegalArgumentException("use isRepeatedTaskComplete() " +
//                "instead if the Task repeats on a day.");
        return isTaskComplete;
    }

    public boolean isRepeatedTaskComplete(long date) {
        if (!repeatsOnADay) return isTaskComplete;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        boolean test = repeatedTaskCompletionDate.get(day) != null && repeatedTaskCompletionDate.get(day).contains(date);
        Log.d("REPETED COMPLETE: ",  "isRepeatedTaskComplete: " + test);
        return repeatedTaskCompletionDate.get(day) != null && repeatedTaskCompletionDate.get(day).contains(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id &&
                projectId == task.projectId &&
                routineId == task.routineId &&
                durationInMinutes == task.durationInMinutes &&
                scheduledDateUtc == task.scheduledDateUtc &&
                startTimeUtc == task.startTimeUtc &&
                repeatsOnADay == task.repeatsOnADay &&
                repeatsOnMonday == task.repeatsOnMonday &&
                repeatsOnTuesday == task.repeatsOnTuesday &&
                repeatsOnWednesday == task.repeatsOnWednesday &&
                repeatsOnThursday == task.repeatsOnThursday &&
                repeatsOnFriday == task.repeatsOnFriday &&
                repeatsOnSaturday == task.repeatsOnSaturday &&
                repeatsOnSunday == task.repeatsOnSunday &&
                isInQuickList == task.isInQuickList &&
                isTaskComplete == task.isTaskComplete &&
                completionStatusChange == task.isCompletionStatusChange() &&
                isTaskCompleteInQuickList == task.isTaskCompleteInQuickList &&
                Objects.equals(name, task.name) &&
                Objects.equals(routines, task.routines) &&
                Objects.equals(notes, task.notes) &&
                repeatedTasksCompleteAreEqual(task);
    }

    private boolean repeatedTasksCompleteAreEqual(Task task) {
        if (repeatedTaskCompletionDate.size() != task.repeatedTaskCompletionDate.size()) return false;

        // can keyStet() return null?
        for (int i : repeatedTaskCompletionDate.keySet()) {
            if (task.repeatedTaskCompletionDate.get(i) == null) return false;
            if (!listsAreEqual(repeatedTaskCompletionDate.get(i),
                    task.repeatedTaskCompletionDate.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean listsAreEqual(List<Long> l1, List<Long> l2) {
        Collections.sort(l1);
        Collections.sort(l2);
        return l1.equals(l2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, projectId, routineId, name, routines, durationInMinutes, scheduledDateUtc, startTimeUtc, notes, repeatsOnADay, repeatsOnMonday, repeatsOnTuesday, repeatsOnWednesday, repeatsOnThursday, repeatsOnFriday, repeatsOnSaturday, repeatsOnSunday, isInQuickList, isTaskComplete, repeatedTaskCompletionDate);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", routines='" + routines + '\'' +
                ", durationInMinutes=" + durationInMinutes +
                ", scheduledDateUtc=" + scheduledDateUtc +
                ", startTimeUtc=" + startTimeUtc +
                ", notes='" + notes + '\'' +
                ", repeatsOnMonday=" + repeatsOnMonday +
                ", repeatsOnTuesday=" + repeatsOnTuesday +
                ", repeatsOnWednesday=" + repeatsOnWednesday +
                ", repeatsOnThursday=" + repeatsOnThursday +
                ", repeatsOnFriday=" + repeatsOnFriday +
                ", repeatsOnSaturday=" + repeatsOnSaturday +
                ", repeatsOnSunday=" + repeatsOnSunday +
                '}';
    }
}
