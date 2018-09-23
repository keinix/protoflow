package io.keinix.protoflow.scheduling;


import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.keinix.protoflow.data.Task;

public class NotificationScheduler {

    public static final String TAG_INPUT_DATA = "TAG_INPUT_DATA";


    public void scheduleNotification(Task task) {
        WorkManager.getInstance().enqueue(getWorkRequest(task));
    }

    private OneTimeWorkRequest getWorkRequest(Task task) {
        return new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(calculateDelay(task.getStartTimeUtc()), TimeUnit.MILLISECONDS)
                .setInputData(getInputData(task))
                .addTag(getTag(task.getStartTimeUtc()))
                .build();
    }

    private long calculateDelay(long startTime) {
        return startTime - System.currentTimeMillis();
    }

    private Data getInputData(Task task) {
        return new Data.Builder().putInt(TAG_INPUT_DATA, task.getId()).build();
    }

    private String getTag(long startTime) {
        return Long.toString(startTime);
    }

}
