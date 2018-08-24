package io.keinix.protoflow.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

    interface RoutineListener {
        void onRoutineExpanded(Routine routine);
    }

    @Inject
    public TasksAdapter(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mRoutineListener = (RoutineListener) activity;
    }



    // ----------------Override----------------
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_VIEW_TYPE_DATE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_date_separator, parent, false);
                return new DateSeparatorViewHolder(view);
            case ITEM_VIEW_TYPE_ROUTINE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_routine, parent, false);
                return new RoutineViewHolder(view);
            default:
                view = LayoutInflater.from(mContext)
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

    // let's try this new setter comes in as anything that extends then is casted to ListItems
    public void setListItems(List<? extends ListItem> listItems) {
        mListItems = (List<ListItem>) listItems;
        notifyDataSetChanged();
    }

    public void clearTasks() {
        mListItems = null;
        notifyDataSetChanged();
    }

    public void insertRoutineChildTasks(List<? extends ListItem> tasks) {
        int routineId = ((Task) tasks.get(0)).getRoutineId();
        int insertPosition = getRoutineIndex(routineId) + 1;
        mListItems.addAll(insertPosition, tasks);
        notifyDataSetChanged();
        //notifyItemRangeInserted(insertPosition, tasks.size());
    }

    public void insertRoutineChildTasks(List<? extends ListItem> tasks, int insertOffset) {
        int routineId = ((Task) tasks.get(0)).getRoutineId();
        int insertPosition = getRoutineIndex(routineId)  + insertOffset; // +1
        mListItems.addAll(insertPosition, tasks);
        notifyDataSetChanged();
        //notifyItemRangeInserted(insertPosition, tasks.size());
    }

    public void updateRoutinesChildCount(int routineId, int childCount) {
        int index = getRoutineIndex(routineId);
        ((Routine) mListItems.get(index)).setChildTaskCount(childCount);
    }

    public List<ListItem> getListItems() {
        return mListItems;
    }

    public void addNewRoutine(Routine routine) {
        mListItems.add(routine);
        notifyItemInserted(mListItems.size() -1);
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

    // ----------------private----------------

    private int getRoutineIndex(int routineId) {
        for (int i = 0; i < mListItems.size(); i++) {
            if (mListItems.get(i).getItemType() == ListItem.TYPE_ROUTINE &&
                    ((Routine) mListItems.get(i)).getId() == routineId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param routine who's child tasks will be hidden
     * Tasks are removed from the main list and added to memory encase they
     * are expanded later.
     */
    private void collapseRoutine(Routine routine) {
        int firstChildIndex = getRoutineIndex(routine.getId()) + 1;
        int lastChildPosition = firstChildIndex + routine.getChildTaskCount();
        Log.d(TAG, "child Task count: " + routine.getChildTaskCount());
        List<ListItem> childTasks = mListItems.subList(firstChildIndex, lastChildPosition);
        mListItems.removeAll(childTasks);
        routine.setChildTaskCount(0);
        notifyItemRangeRemoved(firstChildIndex, lastChildPosition);
    }


    // -------------View Holders--------------

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
            Task task = (Task) mListItems.get(position);
            taskNameTextView.setText(task.getName());
            setUpPlay(task);
            setDetails(task);
            playButton.setOnClickListener(v -> launchEditTask(task.getId()));
            // "\u2022"
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
            return hourString + minuteString;
        }

        private void launchEditTask(int taskId) {
            Intent intent = new Intent(mContext, AddEditTaskActivity.class);
            intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, taskId);
            mContext.startActivity(intent);
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
            (mActivity).startActivityForResult(intent, TasksActivity.REQUEST_CODE_ROUTINE);
            mRoutine.setExpanded(true);
        }

        @OnClick(R.id.image_button_routine_drop_down)
        void showChildTasks() {
            if (mRoutine.isExpanded()) {
                collapseRoutine(mRoutine);
                routineDropDownImageButton.setRotation(0);
            } else {
                mRoutineListener.onRoutineExpanded(mRoutine);
                routineDropDownImageButton.setRotation(180);
            }
            mRoutine.setExpanded(!mRoutine.isExpanded());
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


