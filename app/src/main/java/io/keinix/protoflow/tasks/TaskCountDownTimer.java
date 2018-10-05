package io.keinix.protoflow.tasks;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ohoussein.playpause.PlayPauseView;

import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Task;

public class TaskCountDownTimer {

    public static final String BUNDLE_IS_COUNTING_DOWN = "BUNDLE_IS_COUNTING_DOWN";
    public static final String BUNDLE_COUNT_DOWN_STATUS_IN_MILLIS = "BUNDLE_COUNT_DOWN_STATUS_IN_MILLIS";
    public static final String BUNDLE_MILLIS_ELAPSED = "BUNDLE_MILLIS_ELAPSED";

    private boolean isCountingDown;
    private CountDownTimer mCountDownTimer;
    private long countDownStatusInMillis = 0;
    private long millisElapsed;

    private PlayPauseView playButton;
    private ProgressBar progressBar;
    private Task mTask;
    private TextView durationTextView;
    private int timerId;

    public TaskCountDownTimer(Task task, PlayPauseView playButton, ProgressBar progressBar,  TextView durationTextView) {
        this.playButton = playButton;
        this.progressBar = progressBar;
        mTask = task;
        timerId = task.getId();
        this.durationTextView = durationTextView;
    }

    public void toggleCountDown() {
        playButton.toggle();
        if (isCountingDown) {
            mCountDownTimer.cancel();
        } else {
            if (countDownStatusInMillis > 0) {
                startCountDown(countDownStatusInMillis);
            } else {
                startCountDown(mTask.getDurationInMinutes());
            }
        }
        isCountingDown = !isCountingDown;
    }

    private void startCountDown(int durationMinutes) {
        mCountDownTimer = new CountDownTimer(durationMinutes * 60000, 1000) {

            @Override
            public void onTick(long l) {
                millisElapsed += 1000;
                countDownStatusInMillis = l;
                long minutes = (l / 1000) / 60;
                long seconds = (l / 1000) % 60;
                progressBar.setProgress((int) calculatePercentRemaining());
                String secondsString = Long.toString(seconds);
                secondsString = secondsString.length() > 1 ? secondsString : 0 + secondsString;
                String timeRemaining = String.format("%s:%s", minutes, secondsString);
                durationTextView.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                durationTextView.setText(R.string.timer_finished);
                playNotificationSound();
                resetCountDown();
            }
        }.start();
    }

    private void resetCountDown() {
        playButton.toggle();
        countDownStatusInMillis = 0;
        millisElapsed = 0;
        isCountingDown = false;
    }

    private void startCountDown(long durationInMillis) {
        mCountDownTimer = new CountDownTimer(durationInMillis, 1000) {

            @Override
            public void onTick(long l) {
                millisElapsed += 1000;
                countDownStatusInMillis = l;
                long minutes = (l / 1000) / 60;
                long seconds = (l / 1000) % 60;
                progressBar.setProgress((int) calculatePercentRemaining());
                String secondsString = Long.toString(seconds);
                secondsString = secondsString.length() > 1 ? secondsString : 0 + secondsString;
                String timeRemaining = String.format("%s:%s", minutes, secondsString);
                durationTextView.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                durationTextView.setText(R.string.timer_finished);
                playNotificationSound();
                resetCountDown();
            }
        }.start();
    }

    private void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(playButton.getContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long calculatePercentRemaining() {
        long total = mTask.getDurationInMinutes() * 60000;
        return  millisElapsed * 100 / total;
    }

    public int getTimerId() {
        return timerId;
    }

    public void restoreTimer(Bundle bundle) {
        isCountingDown = bundle.getBoolean(BUNDLE_IS_COUNTING_DOWN);
        countDownStatusInMillis = bundle.getLong(BUNDLE_COUNT_DOWN_STATUS_IN_MILLIS);
        millisElapsed = bundle.getLong(BUNDLE_MILLIS_ELAPSED);
        if (isCountingDown) startCountDown(countDownStatusInMillis);
    }
}
