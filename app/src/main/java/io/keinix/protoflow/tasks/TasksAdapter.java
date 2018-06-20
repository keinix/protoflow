package io.keinix.protoflow.tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    public static final String TAG = TasksAdapter.class.getSimpleName();
    private Context mContext;
    private List<Task> mTasks;

    @Inject
    public TasksAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if (mTasks == null) {
            return 0;
        } else {
            return mTasks.size();
        }
    }

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        @BindColor(R.color.starTimeDotColor) int startTimeDotColor;

        @BindView(R.id.text_view_task_name) TextView taskNameTextView;
        @BindView(R.id.text_view_task_details) TextView taskDetailsTextView;
        @BindView(R.id.image_button_play_task) ImageButton playButton;
        @BindView(R.id.group_duration) Group durationGroup;
        @BindView(R.id.text_view_duration_display) TextView durationTextView;

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
            Task task = mTasks.get(position);
            taskNameTextView.setText(task.getName());
            setUpPlay(task);
            setDescription(task);
        }

        private void setDescription(Task task) {
            SpannableStringBuilder descriptionStringBuilder = new SpannableStringBuilder();
            Log.d(TAG, "long utx" + task.getStartTimeUtc());
            if (task.getStartTimeUtc() > 1) taskDetailsTextView.setText(getTimeStamp(task));
        }

        public String getTimeStamp(Task task) {
            Calendar calendar  = Calendar.getInstance();
            calendar.setTimeInMillis(task.getStartTimeUtc());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String timeSuffix = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
            return  hour + ":" + minute + timeSuffix;
        }

        private SpannableString getSpannableString(String text, int dotColor) {

            return null;
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
            return hourString + minuteString;
        }

    }
}


