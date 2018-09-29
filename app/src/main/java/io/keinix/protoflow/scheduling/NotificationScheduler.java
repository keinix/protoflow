package io.keinix.protoflow.scheduling;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.keinix.protoflow.data.Task;

public abstract class NotificationScheduler {

    static final String INPUT_ID = "INPUT_ID";
    static final String INPUT_TITLE = "INPUT_TITLE";
    static final String INPUT_START_TIME = "INPUT_START_TIME";
    private static final long MILLIS_IN_24_HOURS = 86400000;
    private static final long MILLIS_IN_WEEK = 604800000;



    public static void scheduleNotification(Task task) {
        WorkManager.getInstance().enqueue(getWorkRequest(task));
        if (task.isRepeatsOnADay()) scheduleRepeatingNotification(task);
    }

    private static void scheduleRepeatingNotification(Task task) {

    }

    private static List<Long> getRepeatedTaskStartDelays(Task task) {
        boolean[] repeatsOnDay = {task.isRepeatsOnSunday(), task.isRepeatsOnMonday(),
                task.isRepeatsOnTuesday(), task.isRepeatsOnWednesday(), task.isRepeatsOnThursday(),
                task.isRepeatsOnFriday(), task.isRepeatsOnSaturday()};
        List<Long> repeatedDayStartDelay = new ArrayList<>();
        int today = getToday();

        for (int i = 0; i < repeatsOnDay.length; i++) {
            // If the task repeats on the current day it will have already been
            // scheduled in the initial call to ScheduleNotification()
            if (repeatsOnDay[i] && i + 1 != today) {
                repeatedDayStartDelay.add(getStartDelayForDay(i + 1, task.getStartTimeUtc()));
            }
        }
        return repeatedDayStartDelay;
    }

    // gets the amount of time between now and the first time a tasks that repeats
    // on a specific will run
    private static long getStartDelayForDay(int day, long startTime) {
        int dayOffset = Math.abs(getToday() - day);
        return ((dayOffset - 1) * MILLIS_IN_24_HOURS) + getMillisLeftInToday();
    }

    private static int getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private static long getMillisLeftInToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() - System.currentTimeMillis();
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
