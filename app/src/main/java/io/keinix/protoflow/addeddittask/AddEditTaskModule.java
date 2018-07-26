package io.keinix.protoflow.addeddittask;

import android.support.annotation.Nullable;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import io.keinix.protoflow.adapters.ProjectPickerAdapter;
import io.keinix.protoflow.adapters.ProjectPickerAdapter.OnProjectSelectedListener;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.di.FragmentScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.DurationPickerDialogFragment;
import io.keinix.protoflow.dialogs.ProjectPickerDialogFragment;
import io.keinix.protoflow.dialogs.TimePickerDialogFragment;
import io.keinix.protoflow.tasks.TasksActivity;

@Module
public abstract class AddEditTaskModule {

    @ActivityScope
    @Provides static DatePickerDialogFragment provideDatePicker() {
        return new DatePickerDialogFragment();
    }

    @ActivityScope
    @Provides static TimePickerDialogFragment timePickerDialogFragment() {
        return new TimePickerDialogFragment();
    }

    @ActivityScope
    @Provides static DurationPickerDialogFragment durationPickerDialogFragment() {
        return new DurationPickerDialogFragment();
    }

    @ActivityScope
    @Provides static ProjectPickerDialogFragment projectPickerDialogFragment() {
        return new ProjectPickerDialogFragment();
    }

    @FragmentScope
    @ContributesAndroidInjector
    abstract ProjectPickerDialogFragment projectPickerDialogFragmentInjector();


    @ActivityScope
    @Provides static int taskIdExtra(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getIntExtra(AddEditTaskActivity.EXTRA_TASK_ID, -1);
    }

    @ActivityScope
    @Provides static long dateFromPreviousView(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getLongExtra(TasksActivity.EXTRA_DATE_OF_CURRENT_VIEW, 0);
    }

    @Nullable
    @ActivityScope
    @Provides static Project getProjectFromIntent(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getParcelableExtra(TasksActivity.EXTRA_PROJECT);
    }

    @Nullable
    @ActivityScope
    @Provides static Routine getRoutineFromIntent(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getParcelableExtra(TasksActivity.EXTRA_ROUTINE);
    }


    @ActivityScope
    @Provides static ProjectPickerAdapter projectPickerAdapter(OnProjectSelectedListener listener) {
        return new ProjectPickerAdapter(listener);
    }

//    @ActivityScope
//    @Provides static OnProjectSelectedListener onProjectSelectedListener(AddEditTaskActivity addEditTaskActivity) {
//        return addEditTaskActivity;
//    }
    @ActivityScope
    @Binds abstract ProjectPickerAdapter.OnProjectSelectedListener provideListener(AddEditTaskActivity addEditTaskActivity);

}
