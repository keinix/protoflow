package io.keinix.protoflow.scheduling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import androidx.work.Worker;

public class NotificationWorker extends Worker {

    private static final String NOTIFICATION_CHANNEL_PROTOFLOW = "NOTIFICATION_CHANNEL_PROTOFLOW";

    //TODO: get pending intent and use play button in notification
    @NonNull
    @Override
    public Result doWork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotificationChannel();
        }
        showNotification();
        return Result.SUCCESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_PROTOFLOW,
                NOTIFICATION_CHANNEL_PROTOFLOW, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_PROTOFLOW)
                .setContentTitle(getNotificationTitle())
                .setAutoCancel(true)
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
}
