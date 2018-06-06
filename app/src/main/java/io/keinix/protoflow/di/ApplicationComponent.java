package io.keinix.protoflow.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.keinix.protoflow.data.source.TaskRepository;
import io.keinix.protoflow.data.source.TaskRepositoryModule;
import io.keinix.protoflow.util.ProtoflowApplication;

@Singleton
@Component(modules = {ActivityBindingModule.class,
        ApplicationModule.class,
        TaskRepositoryModule.class,
        AndroidSupportInjectionModule.class})
public interface ApplicationComponent extends AndroidInjector<ProtoflowApplication>{

    TaskRepository getTaskRepository();

    @Component.Builder
    interface Builder {

        @BindsInstance
        ApplicationComponent.Builder application(Application application);

        ApplicationComponent build();
    }



}
