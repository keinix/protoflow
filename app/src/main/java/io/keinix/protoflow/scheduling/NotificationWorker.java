package io.keinix.protoflow.scheduling;

import android.support.annotation.NonNull;

import androidx.work.Worker;

public class NotificationWorker extends Worker {

    @NonNull
    @Override
    public Result doWork() {


        return Result.SUCCESS;
    }
}
