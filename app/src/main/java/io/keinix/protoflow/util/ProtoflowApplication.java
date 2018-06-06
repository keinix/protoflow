package io.keinix.protoflow.util;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.keinix.protoflow.di.DaggerApplicationComponent;

public class ProtoflowApplication extends DaggerApplication {

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent.builder().application(this).build();
    }
}
