package io.keinix.protoflow.tasks;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.NewProjectDialogFragment;
import io.keinix.protoflow.dialogs.NewRoutineDialogFragment;
import io.keinix.protoflow.util.ListItem;

public class TasksActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener,
        NewProjectDialogFragment.OnNewProjectCreatedListener,
        NewRoutineDialogFragment.OnNewRoutineCreatedListener,
        TasksAdapter.RoutineListener,
        TasksAdapter.TaskCompleteListener {

    // --------------view Binding--------------

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_tasks) RecyclerView recyclerView;

    @BindString(R.string.tasks_toolbar_title_today) String todayString;
    @BindString(R.string.tasks_toolbar_title_7_days) String sevenDaysString;
    @BindString(R.string.tasks_toolbar_title_routines) String routinesString;
    @BindString(R.string.quick_list) String quickListString;

    // ----------Member variables------------

    private TasksViewModel mViewModel;
    private LiveData<List<Task>> mDisplayedTasks;
    private List<Project> mProjects;
    private long mDateOfCurrentView;
    private String mLastViewValue;
    private Project mProject;
    public static final String TAG = TasksActivity.class.getSimpleName();
    public static final int REQUEST_CODE_ROUTINE = 1001;
    public static final String EXTRA_DATE_OF_CURRENT_VIEW = "EXTRA_DATE_OF_CURRENT_VIEW";
    public static final String EXTRA_PROJECT = "EXTRA_PROJECT";
    public static final String EXTRA_ROUTINE = "EXTRA_ROUTINE";
    public static final String KEY_DATE_OF_CURRENT_VIEW = "KEY_DATE_OF_CURRENT_VIEW";
    public static final String KEY_LAST_VIEW = "KEY_LAST_VIEW";
    public static final String LAST_VIEW_TODAY = "VALUE_LAST_VIEW_TODAY";
    public static final String LAST_VIEW_CALENDAR = "VALUE_LAST_VIEW_CALENDAR";
    public static final String LAST_VIEW_7_DAYS = "VALUE_LAST_VIEW_7_DAYS";
    public static final String LAST_VIEW_PROJECT = "LAST_VIEW_PROJECT";
    public static final String LAST_VIEW_ROUTINE = "LAST_VIEW_ROUTINE";
    public static final String LAST_VIEW_QUICK_LIST = "LAST_VIEW_QUICK_LIST";

    // ------------------DI------------------

    @Inject
    public TasksAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory mFactory;

    @Inject
    Lazy<DatePickerDialogFragment> mDatePicker;

    @Inject
    Lazy<NewProjectDialogFragment> mNewProjectDialog;

    @Inject
    Lazy<NewRoutineDialogFragment> mNewRoutineDialog;

    // ----------------OnClick----------------

    @OnClick(R.id.fab)
    void fabClick() {
        Intent intent = new Intent(TasksActivity.this, AddEditTaskActivity.class);
        if (getTitle().equals(sevenDaysString)) {
            startActivity(intent);
        } else if (mLastViewValue.equals(LAST_VIEW_PROJECT)) {
            intent.putExtra(EXTRA_PROJECT, mProject);
            startActivity(intent);
        } else if (mLastViewValue.equals(LAST_VIEW_ROUTINE)) {
            mNewRoutineDialog.get().show(getSupportFragmentManager(), "new_routine");
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // OnClicks for Projects are set individually when they are added to the menu
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
                break;
            case R.id.nav_add_project:
                mNewProjectDialog.get().show(getSupportFragmentManager(), "new_project_dialog");
                mLastViewValue = LAST_VIEW_PROJECT;
                break;
            case R.id.nav_routines:
                mLastViewValue = LAST_VIEW_ROUTINE;
                displayAllRoutines();
                break;
            case R.id.nav_quick_list:
                mLastViewValue = LAST_VIEW_QUICK_LIST;
                displayTasksInQuickList();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    // Callback from mDatePicker
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePicker.get().setStartDate(year, month, day);
        setTitle(mDatePicker.get().getStartDateTimeStampWithDay());
        mDateOfCurrentView = mDatePicker.get().getStartDateUtc();
        mViewModel.getLiveCalendarDay(mDateOfCurrentView)
                .observe(this, this::displayTasksForDay);
    }

    // Callback from mNewProjectDialog
    @Override
    public void onProjectCreated(String projectName) {
        Project project = new Project(projectName);
        mViewModel.insertProject(project);
    }

    @Override
    public void onRoutineCreated(String routineName) {
        Routine routine = new Routine(routineName);
        mViewModel.insertRoutine(routine);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ROUTINE && resultCode == RESULT_OK) {
            mLastViewValue = LAST_VIEW_ROUTINE;
            Routine routine = data.getParcelableExtra(EXTRA_ROUTINE);
            routine.setExpanded(true);
            mViewModel.updateRoutineExpandedValue(routine);
            if (!mViewModel.routineHasCachedChildren(routine)) getRoutineChildren(routine);
        }
    }

    @Override
    public void onRoutineExpandedOrCollapsed(Routine routine) {
        mViewModel.updateRoutineExpandedValue(routine);
        showHideRoutineChildTasks(routine);
    }

    @Override
    public void toggleTaskCompleted(Task task) {
        // get date from date picker
        Task newTask = task.cloneWithNewDate(task.getScheduledDateUtc());
        if (newTask.isRepeatsOnADay()) {
            newTask.toggleRepeatedTaskComplete(mDateOfCurrentView);
        } else {
            newTask.toggleTaskComplete();
        }

        mViewModel.updateTask(newTask);
    }

    @Override
    public boolean isTaskComplete(Task task) {
        if (task.isRepeatsOnADay()) {
            return task.isRepeatedTaskComplete(mDateOfCurrentView);
        } else {
            return task.isTaskComplete();
        }
    }

    @Override
    public void deleteTask(Task task) {
        mViewModel.deleteTask(task);
    }

    @Override
    public void insertTask(Task task) {
        mViewModel.insertTask(task);
    }

    // --------------Lifecycle--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mViewModel = ViewModelProviders.of(this, mFactory).get(TasksViewModel.class);
        setupNavDrawer();
        setUpRecyclerView();
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
        setUpProjectInNavDrawer();
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            case LAST_VIEW_PROJECT:
                mProject = mViewModel.getProject();
                displayTasksInProject(mProject);
                setProjectAsClickInNavMenu(mProject);
                break;
            case LAST_VIEW_ROUTINE:
                navigationView.setCheckedItem(R.id.nav_routines);
                displayAllRoutines();
                break;
            case LAST_VIEW_QUICK_LIST:
                navigationView.setCheckedItem(R.id.checkBox_quick_list);
                displayTasksInQuickList();
                break;
        }
    }

    //~~~~~~~Methods for scheduled tasks~~~~~~~

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
                mAdapter.updateListItems(tasks);
            }
        });
    }

    // change is here
    private void getTaskForDate(CalendarDay calendarDay) {
        mDisplayedTasks = mViewModel.getAllTasksOnDay(calendarDay);
        mDisplayedTasks.observe(this, mAdapter::updateListItems);
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
                    mAdapter.updateListItems(formattedTasks);
                } else {
                    mAdapter.clearTasks();
                }
            });
        });
    }

    //~~~~~~~Methods for Projects~~~~~~~

    // only one project can be added at a time from the nav menu's
    // add button, so if mProjects is not null (e.g. it is not the initial load
    // (of the menu) only one project need to be added to the menu from projects
    private void setUpProjectInNavDrawer() {
        LiveData<List<Project>> liveProjects = mViewModel.getAllProjects();
        liveProjects.observe(this, projects -> {
            if (projects != null) {
                if (mProjects == null) {
                    updateProjectsInMenu(projects);
                } else {
                    // called only when a new project is created
                    updateProjectsInMenu(projects.subList(projects.size() -1, projects.size()));
                    onProjectClicked(projects.get(projects.size() -1));
                }
                mProjects = projects;
            }
        });
    }

    private void updateProjectsInMenu(List<Project> projects) {
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_projects);
        SubMenu subMenu = item.getSubMenu();
        for (Project project : projects) {
            subMenu.add(project.getName())
                    .setOnMenuItemClickListener(v -> onProjectClicked(project))
                    .setIcon(R.drawable.ic_project_black_24)
                    .setCheckable(true);
        }
    }

    private boolean onProjectClicked(Project project) {
        displayTasksInProject(project);
        mProject = project;
        mViewModel.setProject(project);
        drawer.closeDrawer(GravityCompat.START);
        mLastViewValue = LAST_VIEW_PROJECT;
        return true;
    }

    private void displayTasksInProject(Project project) {
        setTitle(project.getName());
        mDisplayedTasks = mViewModel.getTasksInProject(project.getId());
        mDisplayedTasks.observe(this, tasks -> {
            if (tasks.size() > 0) {
                mAdapter.updateListItems(tasks);
            } else {
                mAdapter.clearTasks();
            }
        });
    }

    //TODO: fix this
    private void setProjectAsClickInNavMenu(Project project) {
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_projects);
        SubMenu subMenu = item.getSubMenu();
        subMenu.getItem().setChecked(true);
    }

    //~~~~~~~Methods for Routines~~~~~~~

    private void showHideRoutineChildTasks(Routine routine) {
        if (!mViewModel.routineHasCachedChildren(routine)) {
            getRoutineChildren(routine);
        } else {
            mAdapter.updateListItems(getRoutineListItems());
        }
    }

    private void getRoutineChildren(Routine routine) {
        mViewModel.getChildTasksForRoutine(routine.getId()).observe(this, children -> {
            mViewModel.setCachedRoutineChildren(routine, children);
            mAdapter.updateListItems(getRoutineListItems());
            });
    }

    private void displayAllRoutines() {
        mDisplayedTasks.removeObservers(this);
        setTitle(routinesString);
        mViewModel.getAllRoutines().observe(this, routines -> {
            mViewModel.updateCachedRoutines(routines);
            mAdapter.updateListItems(mViewModel.getRoutineListItems());
        });
    }

    private List<? extends ListItem> getRoutineListItems() {
        return mViewModel.getRoutineListItems();
    }

    //~~~~~~~Methods for Quick List~~~~~~~

    private void displayTasksInQuickList() {
        setTitle(quickListString);
        mDisplayedTasks = mViewModel.getTasksInQuickList();
        mDisplayedTasks.observe(this, mAdapter::updateListItems);
    }
}
