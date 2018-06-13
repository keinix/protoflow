package io.keinix.protoflow.addeddittask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.support.constraint.Group;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

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

    // ~~~~~~view Binding ~~~~~
    @BindDrawable(R.drawable.shape_repeat_day_circle_backgroud) Drawable circle;
    @BindColor(R.color.black) int black;
    @BindColor(R.color.white) int white;
    @BindColor(R.color.gray) int gray;
    @BindString(R.string.add_task_unscheduled) String unscheduled;
    @BindString(R.string.add_task_start_time) String startTimeString;
    @BindString(R.string.add_task_duration) String duration;
    @BindView(R.id.scroll_view_add_edit) ScrollView addEditScrollView;
    @BindView(R.id.edit_text_notes) EditText notesEditText;
    @BindView(R.id.checkbox_notes) CheckBox notesCheckbox;
    @BindView(R.id.text_view_start_time) TextView startTimeTextView;
    @BindView(R.id.text_view_timer) TextView timerTextView;
    @BindView(R.id.image_button_cancel_start_time) ImageButton cancelStartTimeImageButton;
    @BindView(R.id.image_button_cancel_selected_date) ImageButton cancelSelectedImageButton;
    @BindView(R.id.image_button_cancel_timer) ImageButton cancelSelectedDurationImageButton;
    @BindView(R.id.button_submit) Button btn;
    @BindView(R.id.edit_text_task_name) EditText editText;
    @BindView(R.id.text_view_scheduled) TextView scheduledDayTextView;
    @BindView(R.id.checkbox_repeat) CheckBox repeatCheckbox;
    @BindView(R.id.group_days) Group daysGroup;
    @BindViews({R.id.text_view_repeat_monday, R.id.text_view_repeat_tuesday,
            R.id.text_view_repeat_wednesday, R.id.text_view_repeat_thursday,
            R.id.text_view_repeat_friday, R.id.text_view_repeat_saturday,
            R.id.text_view_repeat_sunday})
    List<TextView> repeatDays;

    // ~~~~~~Member variables ~~~~~
    private AddEditTaskViewModel mViewModel;

    // ~~~~~~~DI~~~~~~
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    Lazy<TimePickerDialogFragment> mTimePicker;

    @Inject
    Lazy<DatePickerDialogFragment> mDatePicker;

    @Inject
    Lazy<DurationPickerDialogFragment> mDurationPicker;

    //~~~~~~~OnCLicks~~~~~~~~

    @OnCheckedChanged(R.id.checkbox_repeat)
    void showHideRepeatDays(boolean checked) {
        if (checked) {
            daysGroup.setVisibility(View.VISIBLE);
        } else {
            daysGroup.setVisibility(View.GONE);
        }
    }

    @OnCheckedChanged(R.id.checkbox_notes)
    void showHideNotes(boolean checked) {
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

    //TODO:remove date from the task to be generated
    @OnClick(R.id.image_button_cancel_selected_date)
    void unScheduleTask() {
        scheduleCanceled(cancelSelectedImageButton, scheduledDayTextView, unscheduled);
    }

    @OnClick(R.id.image_button_cancel_start_time)
    void unScheduleStartTime() {
        scheduleCanceled(cancelStartTimeImageButton, startTimeTextView, startTimeString);
    }

    @OnClick(R.id.image_button_cancel_timer)
    void deselectDuration() {
        scheduleCanceled(cancelSelectedDurationImageButton, timerTextView, duration);
    }

    @OnClick(R.id.button_submit)
    void submit() {
        Task task = new Task(editText.getText().toString());
       mViewModel.addTask(task);
       finish();
    }

    //~~~~~~~~~Override~~~~~~~~~

    // Callback from mDatePicker
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String selectedDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        mDatePicker.get().setStartDate(year, month, day);
        scheduleSelected(cancelSelectedImageButton, scheduledDayTextView, selectedDate);
    }

    // Callback from mTimePicker
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String timeSuffix = "";
        if (!android.text.format.DateFormat.is24HourFormat(this)) {
            timeSuffix = hour < 12 ? "AM" : "PM";
            if (hour > 12) {
                hour -= 12;
            } else if (hour == 0) {
                hour = 12;
            }
        }
        mTimePicker.get().setStartTime(hour, minute);
        String timeString = String.format("%s:%02d %s", hour, minute, timeSuffix);
        scheduleSelected(cancelStartTimeImageButton, startTimeTextView, timeString);
    }

    @Override
    public void onDurationSet() {
        int hours = mDurationPicker.get().getSelectedHour();
        int minutes = mDurationPicker.get().getSelectedMinute();
        mDurationPicker.get().setStartDuration(hours, minutes);

        String minutesString = minutes == 1 ? "Minute" : "Minutes";
        String hoursString = hours > 0 ?  hours + " Hours" : "";
        if (hours == 1) hoursString = hoursString.replace("s", "");
        String timeStamp = String.format("%s %s %s", hoursString, minutes, minutesString);
        scheduleSelected(cancelSelectedDurationImageButton, timerTextView, timeStamp);
        int duration = (hours * 60) + minutes;
        mViewModel.setTaskDurationInMinutes(duration);
    }

    // ~~~~~~~lifecycle~~~~~~~~
    // TODO: ********When creating a new task make sure to check if repeat is checked

    // TODO: otherwise false positive repeat days will be passed to the task
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(AddEditTaskViewModel.class);
        mViewModel.initNewIsDaySelectedArray(repeatDays);
    }

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
}
