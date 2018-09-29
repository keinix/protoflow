package io.keinix.protoflow.scheduling;

import android.support.annotation.NonNull;

import androidx.work.Worker;

public class RepeatingNotificationWorker extends Worker {

    @NonNull
    @Override
    public Result doWork() {
        NotificationScheduler.schedulePeriodicNotification();
        return Result.SUCCESS;
    }


}
