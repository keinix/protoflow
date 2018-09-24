package io.keinix.protoflow.scheduling;


import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.keinix.protoflow.data.Task;

public class NotificationScheduler {

    public static final String INPUT_ID = "INPUT_ID";
    public static final String INPUT_TITLE = "INPUT_TITLE";
    public static final String INPUT_START_TIME = "INPUT_START_TIME";


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
      return new Data.Builder()
              .putInt(INPUT_ID, task.getId())
              .putString(INPUT_TITLE, task.getName())
              .putLong(INPUT_START_TIME, task.getStartTimeUtc())
              .build();

    }

    private String getTag(long startTime) {
        return Long.toString(startTime);
    }

}
