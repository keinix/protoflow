package io.keinix.protoflow.tasks;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ohoussein.playpause.PlayPauseView;

import io.keinix.protoflow.data.Task;

public class TaskCountDownTimer {

    private boolean isCountingDown;
    private CountDownTimer mCountDownTimer;
    private long countDownStatusInMillis = 0;
    private long millisElapsed;

    private PlayPauseView playButton;
    private ProgressBar progressBar;
    private Task mTask;
    private TextView durationTextView;


    private void toggleCountDown() {
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
                durationTextView.setText("finished");
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
                durationTextView.setText("finished");
                playNotificationSound();
            }
        }.start();
    }

    private void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long calculatePercentRemaining() {
        long total = mTask.getDurationInMinutes() * 60000;
        return  millisElapsed * 100 / total;
    }
}
