package io.keinix.protoflow.addeddittask;

import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.DurationPickerDialogFragment;
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
    @Provides static int taskIdExtra(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getIntExtra(AddEditTaskActivity.EXTRA_TASK_ID, -1);
    }

    @ActivityScope
    @Provides static long dateFromPreviousView(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getLongExtra(TasksActivity.EXTRA_DATE_OF_CURRENT_VIEW, 0);
    }

}
