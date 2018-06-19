package io.keinix.protoflow.tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

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
        @BindView(R.id.text_view_task_name) TextView textView;
        @BindView(R.id.image_button_play_task) ImageButton playButton;
        @BindView(R.id.group_duration) Group durationGroup;
        @BindView(R.id.text_view_duration_display) TextView durationTextView;

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
            Task task = mTasks.get(position);
            textView.setText(task.getName());
            setUpPlay(task);
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


