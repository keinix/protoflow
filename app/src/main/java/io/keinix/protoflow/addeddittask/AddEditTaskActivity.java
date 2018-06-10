package io.keinix.protoflow.addeddittask;

import android.app.DatePickerDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;

public class AddEditTaskActivity extends DaggerAppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    // ~~~~~~view Binding ~~~~~
    @BindDrawable(R.drawable.shape_repeat_day_circle_backgroud) Drawable circle;
    @BindColor(R.color.black) int black;
    @BindColor(R.color.white) int white;
    @BindView(R.id.button_submit) Button btn;
    @BindView(R.id.editText) EditText editText;
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
    DatePickerDialogFragment mDatePicker;

    //~~~~~~~OnCLicks~~~~~~~~

    @OnCheckedChanged(R.id.checkbox_repeat)
    void showHideRepeatDays(CompoundButton button, boolean checked) {
        if (checked) {
            daysGroup.setVisibility(View.VISIBLE);
        } else {
            daysGroup.setVisibility(View.GONE);
        }
    }

    // use to toggle the background/text color of repeat day icons and add
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
        mDatePicker.show(getSupportFragmentManager(), "date_picker");
    }


    @OnClick(R.id.button_submit)
    void submit() {
        Task task = new Task(editText.getText().toString());
       mViewModel.addTask(task);
       finish();
    }

    //~~~~~~~~~Override~~~~~~~~~

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String selectedDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        scheduledDayTextView.setText(selectedDate);
        scheduledDayTextView.setTextColor(black);
    }

    // TODO: ********When creating a new task make sure to check if repeat is checked
    // ~~~~~~~lifecycle~~~~~~~~

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
}
