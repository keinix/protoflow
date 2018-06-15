package io.keinix.protoflow.addeddittask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.support.constraint.Group;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
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
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.DurationPickerDialogFragment;
import io.keinix.protoflow.dialogs.TimePickerDialogFragment;

public class AddEditTaskActivity extends DaggerAppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        DurationPickerDialogFragment.onDurationSetListener {

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

    @BindView(R.id.scroll_view_add_edit) ScrollView addEditScrollView;
    @BindView(R.id.checkbox_notes) CheckBox notesCheckbox;
    @BindView(R.id.checkbox_repeat) CheckBox repeatCheckbox;
    @BindView(R.id.edit_text_notes) EditText notesEditText;
    @BindView(R.id.edit_text_task_name) EditText editText;
    @BindView(R.id.image_button_cancel_timer) ImageButton cancelSelectedDurationImageButton;
    @BindView(R.id.image_button_cancel_start_time) ImageButton cancelStartTimeImageButton;
    @BindView(R.id.image_button_cancel_selected_date) ImageButton cancelSelectedImageButton;
    @BindView(R.id.group_days) Group daysGroup;
    @BindView(R.id.text_view_timer) TextView timerTextView;
    @BindView(R.id.text_view_scheduled) TextView scheduledDayTextView;
    @BindView(R.id.text_view_start_time) TextView startTimeTextView;
    @BindViews({R.id.text_view_repeat_monday, R.id.text_view_repeat_tuesday,
            R.id.text_view_repeat_wednesday, R.id.text_view_repeat_thursday,
            R.id.text_view_repeat_friday, R.id.text_view_repeat_saturday,
            R.id.text_view_repeat_sunday}) List<TextView> repeatDays;

    // -----------Member variables-----------
    private AddEditTaskViewModel mViewModel;
    private boolean repeatIsChecked;
    private boolean notesAreChecked;

    // ------------------DI------------------
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    Lazy<TimePickerDialogFragment> mTimePicker;

    @Inject
    Lazy<DatePickerDialogFragment> mDatePicker;

    @Inject
    Lazy<DurationPickerDialogFragment> mDurationPicker;

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
            day.setBackgroundResource(0);
            day.setTextColor(black);
        } else {
            day.setBackground(circle);
            day.setTextColor(white);
        }
    }

    @OnClick({R.id.image_button_scheduled, R.id.text_view_scheduled})
    void launchDatePicker() {
        mDatePicker.get().show(getSupportFragmentManager(), "date_picker");
    }

    @OnClick({R.id.image_button_start_time, R.id.text_view_start_time})
    void launchTimePicker() {
        mTimePicker.get().show(getSupportFragmentManager(), "time_picker");
    }

    @OnClick({R.id.image_button_timer, R.id.text_view_timer})
    void launchDurationPicker() {
        mDurationPicker.get().show(getSupportFragmentManager(), "duration_picker");
    }

    @OnClick(R.id.image_button_cancel_selected_date)
    void unScheduleTask() {
        scheduleCanceled(cancelSelectedImageButton, scheduledDayTextView, unscheduled);
        mViewModel.setScheduledDateUtc(0);
    }

    @OnClick(R.id.image_button_cancel_start_time)
    void unScheduleStartTime() {
        scheduleCanceled(cancelStartTimeImageButton, startTimeTextView, startTimeString);
        mViewModel.setStartTime(0, 0);
    }

    @OnClick(R.id.image_button_cancel_timer)
    void deselectDuration() {
        scheduleCanceled(cancelSelectedDurationImageButton, timerTextView, duration);
        mViewModel.setTaskDurationInMinutes(0);
    }


    //------------------Override------------------

    // Callback from mDatePicker
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePicker.get().setStartDate(year, month, day);
        String selectedDate = mViewModel.formatDate(year, month, day);
        scheduleSelected(cancelSelectedImageButton, scheduledDayTextView, selectedDate);
    }

    // Callback from mTimePicker
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        mTimePicker.get().setStartTime(hour, minute);
        boolean is24HourClock = android.text.format.DateFormat.is24HourFormat(this);
        String timeString = mViewModel.parseStartTimeForTimeStamp(hour, minute, is24HourClock);
        scheduleSelected(cancelStartTimeImageButton, startTimeTextView, timeString);
    }
    // callback from mDurationPicker

    @Override
    public void onDurationSet() {
        int hours = mDurationPicker.get().getSelectedHour();
        int minutes = mDurationPicker.get().getSelectedMinute();
        mDurationPicker.get().setStartDuration(hours, minutes);

        // the duration var in the viewModel is also set with this method
        String timeStamp = mViewModel.parseDurationForTimeStamp(hours, minutes);
        scheduleSelected(cancelSelectedDurationImageButton, timerTextView, timeStamp);
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
                    finish();
                }
                break;
            case R.id.menu_clear_task:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    // ------------------Lifecycle------------------
    // TODO: otherwise false positive repeat days will be passed to the task

    // TODO: ********When creating a new task make sure to check if repeat is checked
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(AddEditTaskViewModel.class);
        mViewModel.initNewIsDaySelectedArray(repeatDays);
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

    private void initTaskCreation() {
        if (!repeatIsChecked) {
            mViewModel.setIsDaySelectedArray(null);
        } else if (notesAreChecked) {
            mViewModel.setTaskNotes(notesEditText.getText().toString());
        }
        mViewModel.createTask(editText.getText().toString());
    }

    /**
     *
     * @return returns true if there is no task name
     */
    private boolean taskNameIsEmpty() {
        if (editText.getText().toString().length() < 1) {
            editText.setHint(taskName + "*");
            editText.setHintTextColor(cancelColor);
            Toast.makeText(this, "tasks need a name :(", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}
