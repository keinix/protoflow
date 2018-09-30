package io.keinix.protoflow.scheduling;

import android.support.annotation.NonNull;

import androidx.work.Data;
import androidx.work.Worker;

public class RepeatingNotificationWorker extends Worker {

    private int taskId;
    private String taskName;
    private long taskStartTime;

    @NonNull
    @Override
    public Result doWork() {
        getData();
        NotificationScheduler.schedulePeriodicNotification(taskId, taskName, taskStartTime);
        return Result.SUCCESS;
    }

    private void getData() {
        Data data = getInputData();
        taskId = data.getInt(NotificationScheduler.INPUT_ID, 0);
        taskName = data.getString(NotificationScheduler.INPUT_TITLE);
        taskStartTime = data.getLong(NotificationScheduler.INPUT_START_TIME, 0);
    }
}
