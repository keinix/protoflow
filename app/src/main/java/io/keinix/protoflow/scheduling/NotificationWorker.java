package io.keinix.protoflow.scheduling;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateFormat;

import java.util.Calendar;

import androidx.work.Worker;
import io.keinix.protoflow.R;
import io.keinix.protoflow.tasks.TasksActivity;

public class NotificationWorker extends Worker {

    private static final String NOTIFICATION_CHANNEL_PROTOFLOW = "NOTIFICATION_CHANNEL_PROTOFLOW";

    //TODO: get pending intent and use play button in notification
    @NonNull
    @Override
    public Result doWork() {
        buildNotificationChannel();
        showNotification();
        return Result.SUCCESS;
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void buildNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel;
            channel = new NotificationChannel(NOTIFICATION_CHANNEL_PROTOFLOW,
                    NOTIFICATION_CHANNEL_PROTOFLOW, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_PROTOFLOW)
                .setContentTitle(getNotificationTitle())
                .setContentText(getTaskStartTimeStamp())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(getApplicationContext())
                .notify(getTaskId(), notificationBuilder.build());
    }

    private String getNotificationTitle() {
        return getInputData().getString(NotificationScheduler.INPUT_TITLE);
    }

    private int getTaskId() {
        return getInputData().getInt(NotificationScheduler.INPUT_ID, 0);
    }

    private String getTaskStartTimeStamp() {
        long startTime = getInputData().getLong(NotificationScheduler.INPUT_START_TIME, 0);
        boolean is24Hour = DateFormat.is24HourFormat(getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return parseStartTimeForTimeStamp(hour, minute, is24Hour);
    }

    private String parseStartTimeForTimeStamp(int hour, int minute, boolean is24HourClock) {
        String timeSuffix = "";
        if (!is24HourClock) {
            timeSuffix = hour < 12 ? "AM" : "PM";
            if (hour > 12) {
                hour -= 12;
            } else if (hour == 0) {
                hour = 12;
            }
        }
        return String.format("Start: %s:%02d %s", hour, minute, timeSuffix);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
        return PendingIntent.getActivity(getApplicationContext(),
                TasksActivity.REQUEST_CODE_NOTIFIATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
