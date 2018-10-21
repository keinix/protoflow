package io.keinix.protoflow.tasks;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.DatePicker;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;
import io.keinix.protoflow.R;
import io.keinix.protoflow.adapters.ProjectPickerAdapter;
import io.keinix.protoflow.addeddittask.AddEditTaskActivity;
import io.keinix.protoflow.data.CalendarDay;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.dialogs.AddListItemDialogFragment;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.NewProjectDialogFragment;
import io.keinix.protoflow.dialogs.NewRoutineDialogFragment;
import io.keinix.protoflow.dialogs.ProjectPickerDialogFragment;
import io.keinix.protoflow.util.ListItem;
import io.keinix.protoflow.util.SwipeToDeleteCallback;

public class TasksActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener,
        NewProjectDialogFragment.OnNewProjectCreatedListener,
        NewRoutineDialogFragment.OnNewRoutineCreatedListener,
        TasksAdapter.RoutineListener,
        TasksAdapter.TaskCompleteListener,
        ProjectPickerAdapter.OnProjectSelectedListener,
        AddListItemDialogFragment.OnListItemSelectedListener {

    // --------------view Binding--------------

    // @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fab) com.github.clans.fab.FloatingActionMenu fab;
    @BindView(R.id.routine_fab)com.github.clans.fab.FloatingActionButton routineFab;

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
    private LiveData<List<Task>> mTasksLiveData;
    private LiveData<List<CalendarDay>> mCalendarDaysLiveData;
    private LiveData<List<Routine>> mRoutineLiveData;
    private LiveData<CalendarDay> mCalendarDayLiveData;
    private List<Project> mProjects;
    private long mDateOfCurrentView;
    private CalendarDay mDisplayedCalendarDay;
    private List<CalendarDay> mDisplayed7CalendarDays;
    private String mLastViewValue;
    private Project mProject;
    public static final String TAG = TasksActivity.class.getSimpleName();
    public static final int REQUEST_CODE_ROUTINE = 1001;
    public static final int REQUEST_CODE_NOTIFICATION = 1002;
    public static final String EXTRA_DATE_OF_CURRENT_VIEW = "EXTRA_DATE_OF_CURRENT_VIEW";
    public static final String EXTRA_PROJECT = "EXTRA_PROJECT";
    public static final String EXTRA_ROUTINE = "EXTRA_ROUTINE";
    public static final String EXTRA_QUICK_LIST = "EXTRA_QUICK_LIST";
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

    @Inject
    AddListItemDialogFragment mAddListItemDialog;

    @Inject
    ProjectPickerDialogFragment mProjectPickerDialog;

    // ----------------OnClick----------------

    @OnClick(R.id.sub_fab_project)
    void projectSubFabCLick() {
        fab.close(true);
        LiveData<List<Project>> liveData =  mViewModel.getAllProjects();
        liveData.observe(TasksActivity.this, projects -> {
            mProjectPickerDialog.setTitle("Projects");
            mProjectPickerDialog.setProjects(projects);
            mProjectPickerDialog.show(getSupportFragmentManager(), "project_Picker");
            liveData.removeObservers(TasksActivity.this);
        });
    }

    // Add a Routine's tasks to the currently displayed list of tasks
    @OnClick(R.id.sub_fab_routine)
    void subRoutineFabClick() {
        fab.close(true);
        LiveData<List<Routine>> liveData = mViewModel.getAllRoutines();
        liveData.observe(TasksActivity.this, routines -> {
            mAddListItemDialog.setTitle(routinesString);
            mAddListItemDialog.setListItems(routines);
            mAddListItemDialog.show(getSupportFragmentManager(), "add_list_item");
            liveData.removeObservers(TasksActivity.this);
        });
    }

    // create a new routine
    @OnClick(R.id.routine_fab)
    void routineFabClick() {
        mNewRoutineDialog.get().show(getSupportFragmentManager(), "new_routine");
    }


    @OnClick(R.id.sub_fab_task)
    void fabClick() {
        fab.close(true);
        Intent intent = new Intent(TasksActivity.this, AddEditTaskActivity.class);
        if (getTitle().equals(sevenDaysString)) {
            startActivity(intent);
        } else if (mLastViewValue.equals(LAST_VIEW_PROJECT)) {
            intent.putExtra(EXTRA_PROJECT, mProject);
            startActivity(intent);
        } else if (mLastViewValue.equals(LAST_VIEW_QUICK_LIST)) {
            intent.putExtra(EXTRA_QUICK_LIST, true);
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
                clearObservers();
                mAdapter.setLastViewValue(mLastViewValue);
//                mAdapter.clearTasks();
                getTasksForToday();
                break;
            case R.id.nav_7_days:
                mLastViewValue = LAST_VIEW_7_DAYS;
                mDateOfCurrentView = 0;
                mAdapter.setLastViewValue(mLastViewValue);
                clearObservers();
//                mAdapter.clearTasks();
                getTasksFor7Days();
                break;
            case R.id.nav_add_project:
                mNewProjectDialog.get().show(getSupportFragmentManager(), "new_project_dialog");
                clearObservers();
                mAdapter.setLastViewValue(mLastViewValue);
//                mAdapter.clearTasks();
                mLastViewValue = LAST_VIEW_PROJECT;
                break;
            case R.id.nav_routines:
                mLastViewValue = LAST_VIEW_ROUTINE;
                mAdapter.setLastViewValue(mLastViewValue);
                clearObservers();
//                mAdapter.clearTasks();
                displayAllRoutines();
                break;
            case R.id.nav_quick_list:
                mLastViewValue = LAST_VIEW_QUICK_LIST;
                clearObservers();
                mAdapter.setLastViewValue(mLastViewValue);
//                mAdapter.clearTasks();
                displayTasksInQuickList();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        swapFabs();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LAST_VIEW, mLastViewValue);
        outState.putLong(KEY_DATE_OF_CURRENT_VIEW, mDateOfCurrentView);
        mAdapter.persistTimers();
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
        clearObservers();
//        mAdapter.clearTasks();
        mDatePicker.get().setStartDate(year, month, day);
        setTitle(mDatePicker.get().getStartDateTimeStampWithDay());
        mDateOfCurrentView = mDatePicker.get().getStartDateUtc();
        mAdapter.setLastViewValue(mLastViewValue);
        mCalendarDayLiveData = mViewModel.getLiveCalendarDay(mDateOfCurrentView);
        mCalendarDayLiveData.observe(TasksActivity.this, this::displayTasksForDay);
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

        switch (mLastViewValue) {
            case LAST_VIEW_7_DAYS:
                toggle7DaysTaskCompleted(task);
                break;
            case LAST_VIEW_QUICK_LIST:
                toggleQuickListTaskComplete(task);
                break;
            case LAST_VIEW_PROJECT:
                toggleProjectTaskComplete(task.getId());
            default:
                toggleScheduledTaskCompleted(task.getId());
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

    @Override
    public void onProjectSelected(Project project) {
        mProjectPickerDialog.dismiss();
        LiveData<List<Task>> liveData = mViewModel.getTasksInProject(project.getId());
        liveData.observe(TasksActivity.this, tasks -> {
            mAddListItemDialog.setListItems(tasks);
            mAddListItemDialog.setTitle(project.getName());
            mAddListItemDialog.show(getSupportFragmentManager(), "add_list_item");
            liveData.removeObservers(TasksActivity.this);
        });
    }

    // Task from a projected was added to the quick list or a CalendarDay
    @Override
    public void onTaskSelected(Task task) {
        mAddListItemDialog.dismiss();
        if (mLastViewValue.equals(LAST_VIEW_QUICK_LIST)) {
            task.setInQuickList(true);
            mViewModel.updateTask(task);
        } else { // AddToCalendarDay
            Log.d(TAG, "Date Of Current View: "  + mDateOfCurrentView);
            mViewModel.updateCalendarDay(mDisplayedCalendarDay, task, mDateOfCurrentView);
        }
    }

    //TODO: include some kind of indication that the task are a part of a routine
    @Override
    public void onRoutineSelected(int routineId) {
        mAddListItemDialog.dismiss();
        if (mLastViewValue.equals(LAST_VIEW_QUICK_LIST)) {
            addRoutineToQuickList(routineId);
        } else {
            scheduleRoutine(routineId);
        }
    }

    /**
     * persist the current state of a {@link Task}'s {@link TaskCountDownTimer}
     * @param bundle representing the state of a {@link TaskCountDownTimer}
     */
    @Override
    public void addCountDownTimerValues(Bundle bundle) {
        mViewModel.saveCountDownTimerValues(bundle);
    }

    /**
     * persist the current state of a {@link Task}'s {@link TaskCountDownTimer}
     * @return bundle representing the state of a {@link TaskCountDownTimer}
     */
    @Override
    public Bundle getCountDownTimerValues(Task task) {
        return mViewModel.restoreCountDownTimer(task);
    }

    // --------------Lifecycle--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mViewModel = ViewModelProviders.of(this, mFactory).get(TasksViewModel.class);
        //TODO: merge all tasks callbacks into MediatorLiveData
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
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void clearObservers() {
        Log.d(TAG, "Clear Observers Called");
        if (mTasksLiveData != null) mTasksLiveData.removeObservers(TasksActivity.this);
        if (mCalendarDayLiveData != null) mCalendarDayLiveData.removeObservers(TasksActivity.this);
        if (mCalendarDaysLiveData != null) mCalendarDaysLiveData.removeObservers(TasksActivity.this);
        if (mRoutineLiveData != null) mRoutineLiveData.removeObservers(TasksActivity.this);
    }

    // used to restore view after configuration changes
    private void restoreView() {
        // clearObservers();
        switch (mLastViewValue) {
            case LAST_VIEW_CALENDAR:
                clearObservers();
                navigationView.setCheckedItem(R.id.nav_calendar);
                mDatePicker.get().setStartDate(mDateOfCurrentView);
                mAdapter.setLastViewValue(mLastViewValue);
                setTitle(mDatePicker.get().getStartDateTimeStampWithDay());
                mCalendarDayLiveData =  mViewModel.getLiveCalendarDay(mDateOfCurrentView);
                mCalendarDayLiveData.observe(TasksActivity.this, this::displayTasksForDay);
                break;
            case LAST_VIEW_TODAY:
                clearObservers();
                navigationView.setCheckedItem(R.id.nav_today);
                mAdapter.setLastViewValue(mLastViewValue);
                getTasksForToday();
                break;
            case LAST_VIEW_7_DAYS:
                clearObservers();
                navigationView.setCheckedItem(R.id.nav_7_days);
                mAdapter.setLastViewValue(mLastViewValue);
                getTasksFor7Days();
                break;
            case LAST_VIEW_PROJECT:
                clearObservers();
                mProject = mViewModel.getProject();
                mAdapter.setLastViewValue(mLastViewValue);
                displayTasksInProject(mProject);
                setProjectAsClickInNavMenu(mProject);
                break;
            case LAST_VIEW_ROUTINE:
                clearObservers();
                navigationView.setCheckedItem(R.id.nav_routines);
                mAdapter.setLastViewValue(mLastViewValue);
                displayAllRoutines();
                break;
            case LAST_VIEW_QUICK_LIST:
                clearObservers();
                navigationView.setCheckedItem(R.id.checkBox_quick_list);
                mAdapter.setLastViewValue(mLastViewValue);
                displayTasksInQuickList();
                break;
        }
        swapFabs();
    }

    private void scheduleRoutine(int routineId) {
        //if tasks are being added to a scheduled day
        LiveData<List<Task>> liveData = mViewModel.getChildTasksForRoutine(routineId);
        liveData.observe(TasksActivity.this, tasks -> {
            mViewModel.updateCalendarDay(mDisplayedCalendarDay, tasks, mDateOfCurrentView);
            liveData.removeObservers(TasksActivity.this);
        });
    }

    private void swapFabs() {
        if (mLastViewValue.equals(LAST_VIEW_ROUTINE)) {
            routineFab.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        } else if (fab.getVisibility() == View.GONE){
            routineFab.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void addRoutineToQuickList(int routineId) {
        LiveData<List<Task>> liveData = mViewModel.getChildTasksForRoutine(routineId);
        liveData.observe(TasksActivity.this, tasks -> {
            for (Task task : tasks) {
                task.setInQuickList(true);
                mViewModel.updateBatchTasks(task);
            }
            liveData.removeObservers(TasksActivity.this);
        });
    }

    private void toggleScheduledTaskCompleted(int id) {
        boolean wasRemoved = false;
        if (mDisplayedCalendarDay.getCompletedTasks() != null) {
            wasRemoved = mDisplayedCalendarDay.getCompletedTasks().remove(Integer.valueOf(id));
        }
        if (!wasRemoved) mDisplayedCalendarDay.addCompletedTasks(id);
        mViewModel.updateCalendarDay(mDisplayedCalendarDay);
    }

    private void toggleQuickListTaskComplete(Task task) {
        task.setTaskCompleteInQuickList(!task.isTaskCompleteInQuickList());
        mViewModel.updateTask(task);
    }

    private void toggleProjectTaskComplete(int id) {
        boolean wasRemoved = false;
        if (mProject.getCompletedTasks() != null) {
            wasRemoved = mProject.getCompletedTasks().remove(Integer.valueOf(id));
        }
        if (!wasRemoved) mProject.addCompletedTasks(id);
        mViewModel.updateProject(mProject);
    }

    private void toggle7DaysTaskCompleted(Task task) {
        boolean calendarDayFound = false;
        for (CalendarDay calendarDay : mDisplayed7CalendarDays) {
            if (calendarDay.getDate() == task.getScheduledDateUtc()) {
                if (calendarDay.getCompletedTasks() == null ||
                        !calendarDay.getCompletedTasks().remove(Integer.valueOf(task.getId()))) {
                    calendarDay.addCompletedTasks(task.getId());
                }
                calendarDayFound = true;
                mViewModel.updateCalendarDay(calendarDay);
                break;
            }
        }
        if (!calendarDayFound) {
            CalendarDay calendarDay = new CalendarDay(task.getScheduledDateUtc());
            calendarDay.addCompletedTasks(task.getId());
            mViewModel.insertCalendarDay(calendarDay);
        }
    }

    //~~~~~~~Methods for scheduled tasks~~~~~~~

    // calendarDay can be null if no task was scheduled for that day
    private void displayTasksForDay(@Nullable CalendarDay calendarDay) {
        mDisplayedCalendarDay = calendarDay;
        if (calendarDay != null) {
            getTaskForDate(calendarDay);
        } else {
            getTaskThatRepeatOnDay();
        }
    }

    private void getTaskThatRepeatOnDay() {
        //TODO: may need to add the new CalendarDay here
        mTasksLiveData = mViewModel.getAllTasksOnDay(mDatePicker.get().getStartDateUtc());
        mTasksLiveData.observe(TasksActivity.this, tasks -> {
            Log.d(TAG, "OBSERVER TRIGGERED: getTaskThatRepeatOnDay. Observer on mTasksLiveData");
            if (tasks == null) {
                mAdapter.clearTasks();
            } else {
                mTasksLiveData.removeObservers(TasksActivity.this);
                CalendarDay calendarDay = new CalendarDay(mDatePicker.get().getStartDateUtc());
                for (Task task: tasks) {
                    calendarDay.addScheduledTaskIds(task.getId());
                }
                mViewModel.insertCalendarDay(calendarDay);
//                if (mCalendarDaysLiveData.hasObservers()) mCalendarDaysLiveData.removeObservers(TasksActivity.this);
                mCalendarDayLiveData = mViewModel.getLiveCalendarDay(mDatePicker.get().getStartDateUtc());
                mCalendarDayLiveData.observe(TasksActivity.this, calendarDayAfterInsert -> {
                    Log.d(TAG, "OBSERVER TRIGGERED: getTaskThatRepeatOnDay. Observer on mCalendarDayLiveData");
                    if (calendarDayAfterInsert != null) {
                        getTaskForDate(calendarDayAfterInsert);
                        mDisplayedCalendarDay = calendarDayAfterInsert;
                    }
                });
               // mAdapter.updateListItems(tasks);
            }
        });
    }

    // change is here
    private void getTaskForDate(CalendarDay calendarDay) {
        mAdapter.setCompletedTasks(calendarDay.getCompletedTasks());
        mTasksLiveData = mViewModel.getAllTasksOnDay(calendarDay);
        mTasksLiveData.observe(TasksActivity.this, tasks -> {
            mAdapter.updateListItems(tasks);
            Log.d(TAG, "OBSERVER TRIGGERED: GetTaskForDate. Observer on mTasksLiveData");
            mTasksLiveData.removeObservers(this);
        });
    }

    private void getTasksForToday() {
        setTitle(todayString);
        mDatePicker.get().setStartDate(System.currentTimeMillis());
        mDateOfCurrentView = mDatePicker.get().getStartDateUtc();
        mCalendarDayLiveData =  mViewModel.getLiveCalendarDay(mDateOfCurrentView);
        mCalendarDayLiveData.observe(TasksActivity.this, calendarDay -> {
            displayTasksForDay(calendarDay);
            Log.d(TAG, "OBSERVER TRIGGERED: GetTasksForToday. Observer on mCalendarDayLiveData");
        });
    }

    private void getTasksFor7Days() {
        setTitle(sevenDaysString);
        mCalendarDaysLiveData = mViewModel.getNext7CalendarDays();
        mCalendarDaysLiveData.observe(TasksActivity.this, days -> {
            mDisplayed7CalendarDays = days;
            if (mTasksLiveData.hasObservers()) mTasksLiveData.removeObservers(TasksActivity.this);
            if (days != null) {
                setCompletedTaskFor7Days(days);
                mTasksLiveData = mViewModel.getAllTasksFor7Days(days);
            } else {
                mTasksLiveData = mViewModel.getAllRepeatedTasks();
            }
            mTasksLiveData.observe(TasksActivity.this, tasks -> {
                if (tasks.size() != 0) {
                    List<Task> formattedTasks = mViewModel.format7DayTasks(tasks);
                    mAdapter.updateListItems(formattedTasks);
                } else {
                    mAdapter.clearTasks();
                }
            });
        });
    }

    private void setCompletedTaskFor7Days(List<CalendarDay> days) {
        LongSparseArray<List<Integer>> tasksCompletedOnDay = new LongSparseArray<>();
        for (CalendarDay day : days) {
            tasksCompletedOnDay.put(day.getDate(), day.getCompletedTasks());
        }
        mAdapter.setCompletedTasksFor7Days(tasksCompletedOnDay);
    }

    //~~~~~~~Methods for Projects~~~~~~~

    // only one project can be added at a time from the nav menu's
    // add button, so if mProjects is not null (e.g. it is not the initial load
    // (of the menu) only one project need to be added to the menu from projects
    private void setUpProjectInNavDrawer() {
        LiveData<List<Project>> liveProjects = mViewModel.getAllProjects();
        liveProjects.observe(TasksActivity.this, projects -> {
            if (projects != null) {
                if (mProjects == null) {
                    updateProjectsInMenu(projects);
                } else {
                    // called only when a new project is created
                    if (mProjects.size() != projects.size()) {
                        updateProjectsInMenu(projects.subList(projects.size() - 1, projects.size()));
                        onProjectClicked(projects.get(projects.size() - 1));
                    }
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
        mProject = project;
        displayTasksInProject(project);
        mViewModel.setProject(project);
        drawer.closeDrawer(GravityCompat.START);
        mLastViewValue = LAST_VIEW_PROJECT;
        return true;
    }

    private void displayTasksInProject(Project project) {
        clearObservers();
        setTitle(project.getName());
        mAdapter.setCompletedTasks(project.getCompletedTasks());
        mTasksLiveData = mViewModel.getTasksInProject(project.getId());
        mTasksLiveData.observe(TasksActivity.this, tasks -> {
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
        mTasksLiveData = mViewModel.getChildTasksForRoutine(routine.getId());
        mTasksLiveData.observe(TasksActivity.this, children -> {
            mViewModel.setCachedRoutineChildren(routine, children);
            mAdapter.updateListItems(getRoutineListItems());
            });
    }

    private void displayAllRoutines() {
        setTitle(routinesString);
        mRoutineLiveData = mViewModel.getAllRoutines();
        mRoutineLiveData.observe(TasksActivity.this, routines -> {
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
        mTasksLiveData = mViewModel.getTasksInQuickList();
        mTasksLiveData.observe(TasksActivity.this, tasks -> {
            mAdapter.updateListItems(tasks);
            Log.d(TAG, "OBSERVER TRIGGERED: DisplayTasksInQuickList. Observer on mTasksLiveData");
        });
    }
}
