package io.keinix.protoflow.util;

import android.app.Application;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.keinix.protoflow.di.DaggerApplicationComponent;

public class ProtoflowApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent.builder().application(this).build();
    }
}
