package io.keinix.protoflow.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ohoussein.playpause.PlayPauseView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.keinix.protoflow.R;
import io.keinix.protoflow.addeddittask.AddEditTaskActivity;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.util.ListItem;

@ActivityScope
public class TasksAdapter extends RecyclerView.Adapter {


    // ----------Member variables------------

    public static final String TAG = TasksAdapter.class.getSimpleName();
    public static final String DATE_HEADING = "$$$DATE_HEADING$$$";
    public static final int ITEM_VIEW_TYPE_TASK = 101;
    public static final int ITEM_VIEW_TYPE_DATE = 102;
    public static final int ITEM_VIEW_TYPE_ROUTINE = 103;
    private Context mContext;
    private List<ListItem> mListItems;
    private Activity mActivity;
    private RoutineListener mRoutineListener;
    private TaskCompleteListener mTaskCompleteListener;
    private Task mRecentlyDeleteTask;

    interface RoutineListener {
        void onRoutineExpandedOrCollapsed(Routine routine);
    }

    public interface TaskCompleteListener {
        void toggleTaskCompleted(Task task);
        boolean isTaskComplete(Task task);
        void deleteTask(Task task);
        void insertTask(Task task);
        void addCountDownTimerValues(Bundle bundle);
        Bundle getCountDownTimerValues(Task task);
    }

    @Inject
    public TasksAdapter(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mRoutineListener = (RoutineListener) activity;
        mTaskCompleteListener = (TaskCompleteListener) activity;
    }

    // ----------------Override----------------
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_VIEW_TYPE_DATE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_date_separator, parent, false);
                return new DateSeparatorViewHolder(view);
            case ITEM_VIEW_TYPE_ROUTINE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_routine, parent, false);
                return new RoutineViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_task, parent, false);
                return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_DATE:
                ((DateSeparatorViewHolder) holder).bindView(position);
                break;
            case ITEM_VIEW_TYPE_ROUTINE:
                ((RoutineViewHolder) holder).bindView(position);
                break;
            default:
                ((TaskViewHolder) holder).bindView(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mListItems == null) {
            return 0;
        } else {
            return mListItems.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mListItems.get(position).getItemType() == ListItem.TYPE_TASK) {
            String taskName = ((Task) mListItems.get(position)).getName();
            switch (taskName) {
                case DATE_HEADING:
                    return ITEM_VIEW_TYPE_DATE;
                default:
                    return ITEM_VIEW_TYPE_TASK;
            }
        } else {
            return ITEM_VIEW_TYPE_ROUTINE;
        }
    }

    // ----------------Public----------------


    public Context getContext() {
        return mContext;
    }

    public void clearTasks() {
        mListItems = null;
        notifyDataSetChanged();
    }

    public void updateListItems(List<? extends ListItem> listItems) {
            if (mListItems == null) mListItems = new ArrayList<>();
            ListItemDiffCallback diffCallback =
                    new ListItemDiffCallback(mListItems, (List<ListItem>) listItems);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

            mListItems.clear();
            mListItems.addAll(listItems);
            diffResult.dispatchUpdatesTo(this);
    }

    public void deleteTask(int position) {
        Task task = ((Task) mListItems.get(position));
        mRecentlyDeleteTask = task;
        mTaskCompleteListener.deleteTask(task);
        showUndoSnackbar();
    }

    /**
     * show a undo {@link Snackbar} when a Task is deleted
     */
    public void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> mTaskCompleteListener.insertTask(mRecentlyDeleteTask));
        snackbar.show();
    }

    /**
     * If a task has a duration and the timer has been started this method is
     * used to persist the {@link TaskCountDownTimer} state in {@link TasksViewModel}
     */
    public void persistTimers() {
        for (ListItem item : mListItems) {
            if (item.getItemType() == ListItem.TYPE_TASK && ((Task) item).getDurationInMinutes() > 0 &&
                    ((Task) item).getElapsedMillis() > 0) {
                mTaskCompleteListener.addCountDownTimerValues(((Task) item).getCountdownTimerValues());
                ((Task) item).cancelTimer();
            }
        }
    }


    // -------------View Holders--------------

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindColor(R.color.starTimeDotColor) int startTimeDotColor;
        @BindView(R.id.text_view_task_name) TextView taskNameTextView;
        @BindView(R.id.text_view_task_details) TextView taskDetailsTextView;
        @BindView(R.id.image_button_play_task) PlayPauseView playButton;
        @BindView(R.id.checkbox_task_completed) CheckBox taskCompletedCheckBox;
        @BindView(R.id.group_duration) Group durationGroup;
        @BindView(R.id.text_view_duration_display) TextView durationTextView;
        @BindView(R.id.progress_bar_task) ProgressBar progressBar;

        private Task mTask;

        @OnClick(R.id.image_button_play_task)
        void playClicked() {
            mTask.toggleCountdown();
        }

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindView(int position) {
            mTask = (Task) mListItems.get(position);
            if (mTask.getDurationInMinutes() > 0) setCountDownTimer();
            taskNameTextView.setText(mTask.getName());
            setUpPlay(mTask);
            setDetails(mTask);
            // taskCompletedCheckBox.setOnCheckedChangeListener((v, b) -> mTaskCompleteListener.toggleTaskCompleted(mTask));
            // markTaskComplete(mTask);
        }

        private void setCountDownTimer() {
            Bundle timerValues = mTaskCompleteListener.getCountDownTimerValues(mTask);
            if (timerValues == null) {
                mTask.setCountdownTimer(playButton, progressBar, durationTextView);
            } else {
                mTask.restoreCountDownTimer(timerValues, playButton, progressBar, durationTextView);
            }
        }

        private void setDetails(Task task) {
            if (task.getStartTimeUtc() > 1) {
                taskDetailsTextView.setVisibility(View.VISIBLE);
                String detailsText = "Start: " + getTimeStamp(task);
                taskDetailsTextView.setText(detailsText);
            } else {
                taskDetailsTextView.setVisibility(View.GONE);
            }
        }

        public String getTimeStamp(Task task) {
            Calendar calendar  = Calendar.getInstance();
            calendar.setTimeInMillis(task.getStartTimeUtc());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String timeSuffix = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
            boolean is24HourClock = android.text.format.DateFormat.is24HourFormat(mContext);
            if (!is24HourClock) {
                timeSuffix = hour < 12 ? "AM" : "PM";
                if (hour > 12) {
                    hour -= 12;
                } else if (hour == 0) {
                    hour = 12;
                }
            }
            return  hour + ":" + minute + timeSuffix;
        }

        private void setUpPlay(Task task) {
            if (task.getDurationInMinutes() < 1) {
                durationGroup.setVisibility(View.GONE);
            } else {
                durationGroup.setVisibility(View.VISIBLE);
                durationTextView.setText(parseDurationTimeStamp(task));
            }
        }

        private String parseDurationTimeStamp(Task task) {
            int taskInMins = task.getDurationInMinutes();
            String hourString = taskInMins >= 60 ? taskInMins / 60 + "h" : "";
            String minuteString = taskInMins > 60 ? taskInMins % 60 + "m" : String.valueOf(taskInMins) + "m";
            String combinedString = hourString + minuteString;
            combinedString = combinedString.length() > 2 ? combinedString : combinedString + "  ";
            return combinedString;
        }

        private void launchEditTask(int taskId) {
            Intent intent = new Intent(mContext, AddEditTaskActivity.class);
            intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, taskId);
            mContext.startActivity(intent);
        }

        private void markTaskComplete(Task task) {
            if (mTaskCompleteListener.isTaskComplete(task)) {
                taskNameTextView.setPaintFlags(taskDetailsTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                //taskNameTextView.setPaintFlags(taskDetailsTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                taskNameTextView.setPaintFlags(0);
            }
        }

        @Override
        public void onClick(View view) {
            launchEditTask(mTask.getId());
        }
    }


    class DateSeparatorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_date_separator) TextView dateSeparatorTextView;

        public DateSeparatorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(int position) {
            Task task = ((Task) mListItems.get(position));
            String dateString = DatePickerDialogFragment
                    .getStartDateTimeStampWithDay(task.getScheduledDateUtc());
            dateSeparatorTextView.setText(dateString);
        }
    }

    class RoutineViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.text_view_routine_name) TextView routineName;
        @BindView(R.id.image_button_routine_drop_down) ImageButton routineDropDownImageButton;

        private Routine mRoutine;

        @OnClick(R.id.image_button_add_task_to_routine)
        void addTaskToRoutine() {
            Intent intent = new Intent(mContext, AddEditTaskActivity.class);
            intent.putExtra(TasksActivity.EXTRA_ROUTINE, mRoutine);
            mActivity.startActivityForResult(intent, TasksActivity.REQUEST_CODE_ROUTINE);
        }

        @OnClick(R.id.image_button_routine_drop_down)
        void showChildTasks() {
            if (mRoutine.isExpanded()) {
                routineDropDownImageButton.setRotation(0);
            } else {
                routineDropDownImageButton.setRotation(180);
            }
            mRoutine.setExpanded(!mRoutine.isExpanded());
            mRoutineListener.onRoutineExpandedOrCollapsed(mRoutine);
        }

        private RoutineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            routineDropDownImageButton.getDrawable().mutate();
        }

        private void bindView(int position) {
            mRoutine = (Routine) mListItems.get(position);
            routineName.setText(mRoutine.getName());
        }
    }
}


