package io.keinix.protoflow.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.keinix.protoflow.data.source.TaskRepositoryModule;
import io.keinix.protoflow.util.ProtoflowApplication;

/**
 * {@link ActivityBindingModule} creates the subComponents for each activity
 * {@link TaskRepositoryModule} provides the Repository and Room dependencies
 * {@link ViewModelModule} binds the ViewModelProvider.Factory
 */
@Singleton
@Component(modules = {ActivityBindingModule.class,
        ApplicationModule.class,
        TaskRepositoryModule.class,
        ViewModelModule.class,
        AndroidSupportInjectionModule.class})
public interface ApplicationComponent extends AndroidInjector<ProtoflowApplication>{

    @Component.Builder
    interface Builder {

        @BindsInstance
        ApplicationComponent.Builder application(Application application);

        ApplicationComponent build();
    }



}
