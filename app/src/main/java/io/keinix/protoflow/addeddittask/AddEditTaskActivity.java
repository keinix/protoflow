package io.keinix.protoflow.addeddittask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;
import io.keinix.protoflow.R;
import io.keinix.protoflow.adapters.ProjectPickerAdapter;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.DurationPickerDialogFragment;
import io.keinix.protoflow.dialogs.ProjectPickerDialogFragment;
import io.keinix.protoflow.dialogs.TimePickerDialogFragment;
import io.keinix.protoflow.tasks.TasksActivity;

public class AddEditTaskActivity extends DaggerAppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        DurationPickerDialogFragment.onDurationSetListener,
        ProjectPickerAdapter.OnProjectSelectedListener {

    // -------------View Binding-------------
    @BindDrawable(R.drawable.shape_repeat_day_circle_backgroud) Drawable circle;

    @BindColor(R.color.gray) int gray;
    @BindColor(R.color.black) int black;
    @BindColor(R.color.white) int white;
    @BindColor(R.color.errorHintText) int cancelColor;

    @BindString(R.string.add_task_duration) String duration;
    @BindString(R.string.add_task_unscheduled) String unscheduled;
    @BindString(R.string.add_task_start_time) String startTimeString;
    @BindString(R.string.add_task_task_name) String taskName;
    @BindString(R.string.add_task_not_in_project) String projectString;

    @BindView(R.id.scroll_view_add_edit) ScrollView addEditScrollView;
    @BindView(R.id.checkbox_notes) CheckBox notesCheckbox;
    @BindView(R.id.checkbox_repeat) CheckBox repeatCheckbox;
    @BindView(R.id.edit_text_notes) EditText notesEditText;
    @BindView(R.id.edit_text_task_name) EditText taskNameEditText;
    @BindView(R.id.image_button_cancel_duration) ImageButton cancelSelectedDurationImageButton;
    @BindView(R.id.image_button_cancel_start_time) ImageButton cancelStartTimeImageButton;
    @BindView(R.id.image_button_cancel_start_date) ImageButton cancelSelectedImageButton;
    @BindView(R.id.image_button_cancel_project) ImageButton cancelProjectImageButton;
    @BindView(R.id.group_days) Group daysGroup;
    @BindView(R.id.group_project) Group projectGroup;
    @BindView(R.id.group_routine) Group routineGroup;
    @BindView(R.id.text_view_routine) TextView routineTextView;
    @BindView(R.id.text_view_duration) TextView durationTextView;
    @BindView(R.id.text_view_start_date) TextView startDateTextView;
    @BindView(R.id.text_view_start_time) TextView startTimeTextView;
    @BindView(R.id.text_view_project) TextView projectTextView;
    @BindViews({R.id.text_view_repeat_monday, R.id.text_view_repeat_tuesday,
            R.id.text_view_repeat_wednesday, R.id.text_view_repeat_thursday,
            R.id.text_view_repeat_friday, R.id.text_view_repeat_saturday,
            R.id.text_view_repeat_sunday}) List<TextView> repeatDays;

    // -----------Member variables-----------
    private AddEditTaskViewModel mViewModel;
    private boolean asyncTaskLoadIsInProgress;
    private boolean repeatIsChecked;
    private boolean notesAreChecked;

    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    public static final String TAG = AddEditTaskActivity.class.getSimpleName();

    // ------------------DI------------------
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    Lazy<TimePickerDialogFragment> mTimePicker;

    @Inject
    Lazy<DatePickerDialogFragment> mDatePicker;

    @Inject
    Lazy<DurationPickerDialogFragment> mDurationPicker;

    @Inject
    ProjectPickerDialogFragment mProjectPicker;

    @Inject
    @Nullable Project mProject;

    @Inject
    @Nullable Routine mRoutine;

    @Inject
    int mTaskIdToEdit;

    @Inject
    long mDateFromPreviousView;

    //----------------OnCLicks----------------

    @OnCheckedChanged(R.id.checkbox_repeat)
    void showHideRepeatDays(boolean checked) {
        repeatIsChecked = checked;
        if (checked) {
            daysGroup.setVisibility(View.VISIBLE);
        } else {
            daysGroup.setVisibility(View.GONE);
        }
    }

    @OnCheckedChanged(R.id.checkbox_notes)
    void showHideNotes(boolean checked) {
        notesAreChecked = checked;
        if (checked) {
            notesEditText.setVisibility(View.VISIBLE);
            notesEditText.requestFocus();
        } else {
            notesEditText.setVisibility(View.GONE);
        }
    }

    // used to toggle the background/text color of repeat day icons and add
    // their state to the isDaySelected Array
    @OnClick({R.id.text_view_repeat_monday, R.id.text_view_repeat_tuesday,
            R.id.text_view_repeat_wednesday, R.id.text_view_repeat_thursday,
            R.id.text_view_repeat_friday, R.id.text_view_repeat_saturday,
            R.id.text_view_repeat_sunday})
    void dayClicked(TextView day) {
        if (mViewModel.isDaySelected(day.getId())) {
            setDayUiAsUnSelected(day);
        } else {
            day.setBackground(circle);
            day.setTextColor(white);
        }
    }

    @OnClick({R.id.image_button_scheduled, R.id.text_view_start_date})
    void launchDatePicker() {
        mDatePicker.get().show(getSupportFragmentManager(), "date_picker");
    }

    @OnClick({R.id.image_button_start_time, R.id.text_view_start_time})
    void launchTimePicker() {
        mTimePicker.get().show(getSupportFragmentManager(), "time_picker");
    }

    @OnClick({R.id.image_button_timer, R.id.text_view_duration})
    void launchDurationPicker() {
        mDurationPicker.get().show(getSupportFragmentManager(), "duration_picker");
    }

    @OnClick({R.id.image_button_project, R.id.text_view_project})
    void launchProjectPicker() {
        mViewModel.getAllProjects().observe(this, mProjectPicker::setProjects);
        mProjectPicker.show(getSupportFragmentManager(), "project_picker");
    }

    @OnClick(R.id.image_button_cancel_project)
    void removeFromProject() {
        scheduleCanceled(cancelProjectImageButton, projectTextView, projectString);
        mViewModel.setProject(null);
        //TODO: update viewmodel and remove task from project if confirmed
    }

    @OnClick(R.id.image_button_cancel_start_date)
    void unScheduleTask() {
        scheduleCanceled(cancelSelectedImageButton, startDateTextView, unscheduled);
        mViewModel.setStartDateUtc(0);
    }

    @OnClick(R.id.image_button_cancel_start_time)
    void unScheduleStartTime() {
        scheduleCanceled(cancelStartTimeImageButton, startTimeTextView, startTimeString);
        mViewModel.setStartTimeUtc(0);
    }

    @OnClick(R.id.image_button_cancel_duration)
    void deselectDuration() {
        scheduleCanceled(cancelSelectedDurationImageButton, durationTextView, duration);
        mViewModel.setTaskDurationInMinutes(0);
    }


    //------------------Override------------------

    // Callback from mDatePicker
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePicker.get().setStartDate(year, month, day);
        String selectedDate = mDatePicker.get().getStartDateTimeStamp();
        mViewModel.setStartDateUtc(year, month, day);
        scheduleSelected(cancelSelectedImageButton, startDateTextView, selectedDate);
    }

    // Callback from mTimePicker
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        mTimePicker.get().setStartTime(hour, minute);
        boolean is24HourClock = android.text.format.DateFormat.is24HourFormat(this);
        String timeString = mViewModel.parseStartTimeForTimeStamp(hour, minute, is24HourClock);
        mViewModel.setStartTimeUtc(hour, minute);
        scheduleSelected(cancelStartTimeImageButton, startTimeTextView, timeString);
    }

    // callback from mDurationPicker
    @Override
    public void onDurationSet() {
        int hours = mDurationPicker.get().getSelectedHour();
        int minutes = mDurationPicker.get().getSelectedMinute();
        mDurationPicker.get().setStartDuration(hours, minutes);
        String timeStamp = mViewModel.parseDurationForTimeStamp(hours, minutes);
        mViewModel.setTaskDurationInMinutes(hours, minutes);
        scheduleSelected(cancelSelectedDurationImageButton, durationTextView, timeStamp);
    }

    // callback from mProjectPicker
    @Override
    public void onProjectSelected(Project project) {
        scheduleSelected(cancelProjectImageButton, projectTextView, project.getName());
        mProjectPicker.dismiss();
        mViewModel.setProject(project);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_tasks, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_task:
                if (!taskNameIsEmpty()) {
                    initTaskCreation();
                    Intent intent = new Intent();
                    intent.putExtra(TasksActivity.EXTRA_ROUTINE, mRoutine);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.menu_clear_task:
                setResult(RESULT_CANCELED);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (!asyncTaskLoadIsInProgress) {
            loadUiData();
        }
    }

    // ------------------Lifecycle------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(AddEditTaskViewModel.class);
        mViewModel.initNewIsDaySelectedArray(repeatDays);

        if (mTaskIdToEdit >= 0) {
            LiveData<Task> task = mViewModel.getTaskToEdit(mTaskIdToEdit);
            task.observe(this, this::setUpUiToEditTask);
        }
        if (mDateFromPreviousView > 0) setDateFromPreviousView();
        if (mProject != null) {
                scheduleSelected(cancelProjectImageButton, projectTextView, mProject.getName());
                mViewModel.setProject(mProject);
        }
        setUpForRoutine();
    }

    // ------------------Private------------------
    /**
     * Below params refer to the start time, timer (duration), and start date view
     * @param imageButton cancel image button
     * @param textView text view
     * @param text text for a date, start time, or duration
     */
    private void scheduleSelected(ImageButton imageButton, TextView textView, String text) {
        imageButton.setVisibility(View.VISIBLE);
        textView.setPadding(0, 0, 10, 10);
        textView.setTextColor(black);
        textView.setText(text);
    }

    /**
     * Below params refer to the start time, timer (duration), and start date view
     * @param imageButton cancel image button
     * @param textView text view
     * @param text default text when no schedule is specified
     */
    private void scheduleCanceled(ImageButton imageButton, TextView textView, String text) {
        imageButton.setVisibility(View.INVISIBLE);
        textView.setPadding(0,0,400,0);
        textView.setTextColor(gray);
        textView.setText(text);
    }
    // Called to save/update a task before leaving AddEditTaskActivity
    // Called in onOptionItemSelected
    private void initTaskCreation() {
        if (!repeatIsChecked) mViewModel.setIsDaySelectedArray(null);
        if (notesAreChecked) mViewModel.setTaskNotes(notesEditText.getText().toString());
        if (mTaskIdToEdit >= 0) {
            mViewModel.updateExistingTask(taskNameEditText.getText().toString(), mTaskIdToEdit);
        } else {
            mViewModel.createNewTask(taskNameEditText.getText().toString().trim());
        }
    }

    /**
     * @return true if there is no task name
     */
    private boolean taskNameIsEmpty() {
        if (taskNameEditText.getText().toString().length() < 1) {
            taskNameEditText.setHint(taskName + "*");
            taskNameEditText.setHintTextColor(cancelColor);
            Toast.makeText(this, "tasks need a name :(", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    // Called on configuration changes or if a task is being edited
    private void loadUiData() {
        setUpRepeatedDaysUi();
        setDurationFromViewModel();
        setStartTimeFromViewModel();
        setStartDateFromViewModel();
        setProjectFromViewModel();
        if (mViewModel.getRoutine() != null) {
            routineTextView.setText(mViewModel.getRoutine().getName());
        }
    }

    private void setStartDateFromViewModel() {
        if (mViewModel.getStartDateUtc() > 0) {
            scheduleSelected(cancelSelectedImageButton,
                    startDateTextView, mViewModel.getTaskStartDateTimeStamp());
        }
    }

    private void setStartTimeFromViewModel() {
        if (mViewModel.getStartTimeUtc() > 0) {
            boolean is24Hours = android.text.format.DateFormat.is24HourFormat(this);
            scheduleSelected(cancelStartTimeImageButton, startTimeTextView,
                    mViewModel.getTaskStartTimeStamp(is24Hours));
        }
    }

    private void setDurationFromViewModel() {
        if (mViewModel.getTaskDurationInMinutes() > 0) {
            scheduleSelected(cancelSelectedDurationImageButton,
                    durationTextView, mViewModel.getTaskDurationTimeStamp());
        }
    }

    private void setProjectFromViewModel() {
        Project project = mViewModel.getProject();
        if (project != null) {
            scheduleSelected(cancelProjectImageButton, projectTextView, project.getName());
        }
    }

    // Only Called when a task is being edited
    private void setUpUiToEditTask(Task task) {
        mViewModel.setViewModelVariablesFromTask(task);
        taskNameEditText.setText(task.getName());
        if (task.getNotes() != null) {
            notesCheckbox.setChecked(true);
            notesEditText.setText(task.getNotes());
        }
        if (task.isRepeatsOnADay()) repeatCheckbox.setChecked(true);
        mViewModel.getProject(task.getProjectId()).observe(this, project -> {
            mViewModel.setProject(project);
            loadUiData();
        });
    }

    private void setDayUiAsUnSelected(TextView day) {
        day.setBackgroundResource(0);
        day.setTextColor(black);
    }

    /**
     * dayArray should never be null b/c it inits in onCreate and is stored in
     * {@link AddEditTaskViewModel}. dayArray maps TextView ids to booleans
     * the id is used to search a list TextViews (repeatDays) and the boolean sets its ui
     */
    private void setUpRepeatedDaysUi() {
        SparseBooleanArray dayArray = mViewModel.getIsDaySelectedArray();
        for (int i = 0; i < dayArray.size(); i++) {
            if (!dayArray.valueAt(i)) {
                TextView dayTextView = null;
                for (TextView day : repeatDays) {
                    if (day.getId() == dayArray.keyAt(i)) {
                        dayTextView = day;
                        break;
                    }
                }
                setDayUiAsUnSelected(dayTextView);
            }
        }
    }

    private void setDateFromPreviousView() {
        mDatePicker.get().setStartDate(mDateFromPreviousView);
        String selectedDate = mDatePicker.get().getStartDateTimeStamp();
        mViewModel.setStartDateUtc(mDateFromPreviousView);
        scheduleSelected(cancelSelectedImageButton, startDateTextView, selectedDate);
    }

    // routines can not be added to projects
    private void setUpForRoutine() {
        if (mRoutine != null) {
            routineGroup.setVisibility(View.VISIBLE);
            projectGroup.setVisibility(View.INVISIBLE);
            routineTextView.setText(mRoutine.getName());
            mViewModel.setRoutine(mRoutine);
        } else {
            projectGroup.setVisibility(View.VISIBLE);
            routineGroup.setVisibility(View.GONE);
        }
    }
}
