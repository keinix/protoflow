package io.keinix.protoflow.di;

import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import io.keinix.protoflow.util.ProtoflowViewModelFactory;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ProtoflowViewModelFactory factory);
}
