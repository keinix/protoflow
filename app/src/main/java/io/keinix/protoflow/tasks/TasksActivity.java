package io.keinix.protoflow.tasks;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;


import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;
import io.keinix.protoflow.R;
import io.keinix.protoflow.addeddittask.AddEditTaskActivity;
import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;

public class TasksActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener {

    // --------------view Binding--------------

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_tasks) RecyclerView recyclerView;

    @BindString(R.string.tasks_toolbar_title_today) String todayString;
    @BindString(R.string.tasks_toolbar_title_7_days) String sevenDaysString;

    // ----------Member variables------------

    private TasksViewModel mViewModel;
    private LiveData<List<Task>> mDisplayedTasks;
    public static final String TAG = TasksActivity.class.getSimpleName();

    public static final int REQUEST_CODE_ADD_TASK_TO_7_DAYS = 1001;

    // ------------------DI------------------

    @Inject
    public TasksAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory mFactory;

    @Inject
    Lazy<DatePickerDialogFragment> mDatePicker;


    // ----------------OnClick----------------

    @OnClick(R.id.fab)
    void fabClick() {
        Intent intent = new Intent(TasksActivity.this, AddEditTaskActivity.class);
        if (getTitle().equals(sevenDaysString)) {
            startActivityForResult(intent, REQUEST_CODE_ADD_TASK_TO_7_DAYS);
        } else {
            startActivity(intent);
        }
    }

    // ----------------Override----------------

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_calendar:
                mDatePicker.get().show(getSupportFragmentManager(), "date_picker");
                break;
            case R.id.nav_today:
                getTasksForToday();
                break;
            case R.id.nav_7_days:
                getTasksFor7Days();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // If a new task is created within the 7 day range it will not have been included in the live
    // Data so the view will not update automatically. In this case when adding a new task
    // while the 7 day view is active a result is used to determine if the UI needs to be
    // reloaded
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD_TASK_TO_7_DAYS) {
            getTasksFor7Days();
            Log.d(TAG, "7 Days UI Reload Triggered");
        }
    }

    // This callback is used when A DatePickerDialog is shown from the calendar
    // option in the nav drawer
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePicker.get().setStartDate(year, month, day);
        setTitle(mDatePicker.get().getStartDateTimeStampWithDay());
        long startDateUtc = mDatePicker.get().getStartDateUtc();
         mViewModel.getLiveCalendarDay(startDateUtc).observe(this, this::displayTasksForDay);
    }

    // --------------Lifecycle--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupNavDrawer();
        setUpRecyclerView();
        mViewModel = ViewModelProviders.of(this, mFactory).get(TasksViewModel.class);
        if (mDisplayedTasks == null) getTasksForToday();
    }

    // ------------------Private------------------

    private void setupNavDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    // calendarDay can be null if no task was scheduled for that day
    private void displayTasksForDay(@Nullable CalendarDay calendarDay) {
        if (calendarDay != null) {
            getTaskForDate(calendarDay);
        } else {
            getTaskThatRepeatOnDay();
        }
    }

    private void getTaskThatRepeatOnDay() {
        mDisplayedTasks = mViewModel.getAllTasksOnDay(mDatePicker.get().getStartDateUtc());
        mDisplayedTasks.observe(this, tasks -> {
            if (tasks == null) {
                mAdapter.clearTasks();
            } else {
                mAdapter.setTasks(tasks);
            }
        });
    }

    private void getTaskForDate(CalendarDay calendarDay) {
        mDisplayedTasks = mViewModel.getAllTasksOnDay(calendarDay);
        mDisplayedTasks.observe(this, mAdapter::setTasks);
    }

    private void getTasksForToday() {
        setTitle(todayString);
        mDatePicker.get().setStartDate(System.currentTimeMillis());
        mViewModel.getLiveCalendarDay(mDatePicker.get().getStartDateUtc())
                .observe(this, this::displayTasksForDay);
    }

    private void getTasksFor7Days() {
        setTitle(sevenDaysString);
        mViewModel.getNext7CalendarDays().observe(this, days -> {
                mDisplayedTasks = mViewModel.getAllTasksFor7Days(days);
                mDisplayedTasks.observe(this, tasks -> {
                    List<Task> formattedTasks = mViewModel.format7DayTasks(tasks);
                    if (formattedTasks != null) {
                        mAdapter.setTasks(formattedTasks);
                    } else {
                        mAdapter.clearTasks();
                    }
            });
        });
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
