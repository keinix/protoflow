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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
import io.keinix.protoflow.dialogs.NewProjectDialogFragment;

public class TasksActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener,
        NewProjectDialogFragment.OnNewProjectCreatedListener {

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
    private long mDateOfCurrentView;
    private String mLastViewValue;
    public static final String TAG = TasksActivity.class.getSimpleName();
    public static final String EXTRA_DATE_OF_CURRENT_VIEW = "EXTRA_DATE_OF_CURRENT_VIEW";
    public static final String KEY_DATE_OF_CURRENT_VIEW = "KEY_DATE_OF_CURRENT_VIEW";
    public static final String KEY_LAST_VIEW = "KEY_LAST_VIEW";
    public static final String LAST_VIEW_TODAY = "VALUE_LAST_VIEW_TODAY";
    public static final String LAST_VIEW_CALENDAR = "VALUE_LAST_VIEW_CALENDAR";
    public static final String LAST_VIEW_7_DAYS = "VALUE_LAST_VIEW_7_DAYS";

    // ------------------DI------------------

    @Inject
    public TasksAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory mFactory;

    @Inject
    Lazy<DatePickerDialogFragment> mDatePicker;

    @Inject
    Lazy<NewProjectDialogFragment> mNewProjectDialog;


    // ----------------OnClick----------------

    @OnClick(R.id.fab)
    void fabClick() {
        Intent intent = new Intent(TasksActivity.this, AddEditTaskActivity.class);
        if (getTitle().equals(sevenDaysString)) {
            startActivity(intent);
        } else {
            intent.putExtra(EXTRA_DATE_OF_CURRENT_VIEW, mDateOfCurrentView);
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
                mLastViewValue = LAST_VIEW_CALENDAR;
                mDatePicker.get().show(getSupportFragmentManager(), "date_picker");
                break;
            case R.id.nav_today:
                mLastViewValue = LAST_VIEW_TODAY;
                getTasksForToday();
                break;
            case R.id.nav_7_days:
                mLastViewValue = LAST_VIEW_7_DAYS;
                mDateOfCurrentView = 0;
                getTasksFor7Days();
            case R.id.nav_add_project:
                mNewProjectDialog.get().show(getSupportFragmentManager(), "new_project_dialog");
                break;
            // default case should be project
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // This callback is used when A DatePickerDialog is shown from the calendar
    // option in the nav drawer
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePicker.get().setStartDate(year, month, day);
        setTitle(mDatePicker.get().getStartDateTimeStampWithDay());
        mDateOfCurrentView = mDatePicker.get().getStartDateUtc();
         mViewModel.getLiveCalendarDay(mDateOfCurrentView)
                 .observe(this, this::displayTasksForDay);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LAST_VIEW, mLastViewValue);
        outState.putLong(KEY_DATE_OF_CURRENT_VIEW, mDateOfCurrentView);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDateOfCurrentView = savedInstanceState.getLong(KEY_DATE_OF_CURRENT_VIEW);
        mLastViewValue = savedInstanceState.getString(KEY_LAST_VIEW);
        restoreView();
    }

    @Override
    public void onProjectCreated(String projectName) {
        addProject(projectName);
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
        if (savedInstanceState == null) {
            mLastViewValue = LAST_VIEW_TODAY;
            restoreView();
        }
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
        mDateOfCurrentView = mDatePicker.get().getStartDateUtc();
        mViewModel.getLiveCalendarDay(mDateOfCurrentView)
                .observe(this, this::displayTasksForDay);
    }

    private void getTasksFor7Days() {
        setTitle(sevenDaysString);
        mViewModel.getNext7CalendarDays().observe(this, days -> {
            if (days != null) {
                mDisplayedTasks = mViewModel.getAllTasksFor7Days(days);
            } else {
                mDisplayedTasks = mViewModel.getAllRepeatedTasks();
            }
            mDisplayedTasks.observe(this, tasks -> {
                if (tasks.size() != 0) {
                    List<Task> formattedTasks = mViewModel.format7DayTasks(tasks);
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

    private void addProject(String projectName) {
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_projects);
        SubMenu subMenu = item.getSubMenu();
        subMenu.add(projectName)
                .setIcon(R.drawable.ic_project_black_24)
                .setCheckable(true);
    }

    // used to restore view after configuration changes
    private void restoreView() {
        switch (mLastViewValue) {
            case LAST_VIEW_CALENDAR:
                navigationView.setCheckedItem(R.id.nav_calendar);
                mDatePicker.get().setStartDate(mDateOfCurrentView);
                setTitle(mDatePicker.get().getStartDateTimeStampWithDay());
                mViewModel.getLiveCalendarDay(mDateOfCurrentView)
                        .observe(this, this::displayTasksForDay);
                break;
            case LAST_VIEW_TODAY:
                navigationView.setCheckedItem(R.id.nav_today);
                getTasksForToday();
                break;
            case LAST_VIEW_7_DAYS:
                navigationView.setCheckedItem(R.id.nav_7_days);
                getTasksFor7Days();
                break;
        }
    }
}
