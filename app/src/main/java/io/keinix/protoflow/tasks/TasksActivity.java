package io.keinix.protoflow.tasks;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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

    // ----------Member variables------------

    private TasksViewModel mViewModel;
    private LiveData<CalendarDay> mCalendarDay;
    private LiveData<List<Task>> mDisplayedTasks;
    public static final String TAG = TasksActivity.class.getSimpleName();

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
        startActivity(intent);
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // This callback is used when A DatePickerDialog is shown from the calendar
    // option in the nav drawer
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePicker.get().setStartDate(year, month, day);
        setTitle(mDatePicker.get().getStartDateTimeStamp());
        long startDateUtc = mDatePicker.get().getStartDateUtc();
        Log.d(TAG, "Start date from onDateSet(): " + startDateUtc);
         mViewModel.getLiveCalendarDay(startDateUtc).observe(this, this::displayTasksForDay);
    }


    // --------------Lifecycle--------------

    //TODO:create a Queue to cache days. pop one if the Queue get past a certain length
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupNavDrawer();
        setUpRecyclerView();
        mViewModel = ViewModelProviders.of(this, mFactory).get(TasksViewModel.class);
        mDisplayedTasks = mViewModel.getAllTasks();
        mDisplayedTasks.observe(this, mAdapter::setTasks);
    }

    // ------------------Private------------------

    private void setupNavDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    // updating mDisplayedTasks will trigger the observer set on OnCreate to refresh
    // the adapter with the new tasks
    private void displayTasksForDay(CalendarDay calendarDay) {
        if (calendarDay != null) {
            LiveData<List<Task>> tasks = mViewModel.getTasks(calendarDay.getScheduledTaskIds());
            mDisplayedTasks = tasks;
            mDisplayedTasks.observe(this, mAdapter::setTasks);
        } else {
            mAdapter.clearTasks();
        }
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void reloadUiToCalendarDay() {
        mDatePicker.get().show(getSupportFragmentManager(), "date_picker");
    }

}
