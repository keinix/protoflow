package io.keinix.protoflow.addeddittask;

import android.app.DialogFragment;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.keinix.protoflow.di.ActivityScope;
import io.keinix.protoflow.dialogs.DatePickerDialogFragment;

@Module
public abstract class AddEditTaskModule {

    @ActivityScope
    @Provides static DatePickerDialogFragment provideDatePicker() {
        return new DatePickerDialogFragment();
    }

}
