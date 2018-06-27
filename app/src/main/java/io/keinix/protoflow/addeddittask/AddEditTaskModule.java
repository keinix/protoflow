package io.keinix.protoflow.addeddittask;

import android.app.DialogFragment;
import android.support.annotation.Nullable;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;
import io.keinix.protoflow.dialogs.DurationPickerDialogFragment;
import io.keinix.protoflow.dialogs.TimePickerDialogFragment;

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

    @Nullable
    @ActivityScope
    @Provides static int taskIdExtra(AddEditTaskActivity addEditTaskActivity) {
        return addEditTaskActivity.getIntent().getIntExtra(AddEditTaskActivity.EXTRA_TASK_ID, -1);
    }

}
