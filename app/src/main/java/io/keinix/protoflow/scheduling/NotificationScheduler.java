package io.keinix.protoflow.scheduling;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.keinix.protoflow.data.Task;

public abstract class NotificationScheduler {

    public static final String INPUT_ID = "INPUT_ID";
    public static final String INPUT_TITLE = "INPUT_TITLE";
    public static final String INPUT_START_TIME = "INPUT_START_TIME";


    public static void scheduleNotification(Task task) {
        WorkManager.getInstance().enqueue(getWorkRequest(task));
    }



    private static OneTimeWorkRequest getWorkRequest(Task task) {
        return new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(calculateDelay(task.getStartTimeUtc()), TimeUnit.MILLISECONDS)
                .setInputData(getInputData(task))
                .addTag(getTag(task.getStartTimeUtc()))
                .build();
    }

    private static long calculateDelay(long startTime) {
        long currentTime = System.currentTimeMillis();
        Log.d("FINDME", "Current Time: " + currentTime);
        return startTime - System.currentTimeMillis();
    }

    private static Data getInputData(Task task) {
      return new Data.Builder()
              .putInt(INPUT_ID, task.getId())
              .putString(INPUT_TITLE, task.getName())
              .putLong(INPUT_START_TIME, task.getStartTimeUtc())
              .build();

    }

    private static String getTag(long startTime) {
        return Long.toString(startTime);
    }

}
