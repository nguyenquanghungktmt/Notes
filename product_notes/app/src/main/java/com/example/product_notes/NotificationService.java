package com.example.product_notes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationService extends JobService {
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.product_notes.ACTION_UPDATE_NOTIFICATION";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static int NOTIFICATION_ID = 0;
    private NotificationManager mNotifyManager;
    private static final String TAG = "myTag";

//    private JobAsyncTask mJobAsyncTask;


    boolean jobCanceled = false;

    private static int jobId;
    private String title;
    private String content;
    private int incomingTime;
    private int duringTime;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
//        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Start Job ");

        jobId = jobParameters.getExtras().getInt("jobId");
        incomingTime = jobParameters.getExtras().getInt("incomingTime");
        duringTime = jobParameters.getExtras().getInt("duringTime");
        title = jobParameters.getExtras().getString("title", "Not receive title");
        content = jobParameters.getExtras().getString("content", "Not receive title");

        Log.d(TAG, "Start Job on " + jobId + incomingTime + duringTime + title + content);

        doInBackground(jobParameters);
        return true;
    }

    private void doInBackground(JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < incomingTime; i++) {
                    if (jobCanceled) return;
                    Log.d(TAG, "run: " + jobId + " " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                sendNotification(jobId, title, content);

                for (int i = 0; i < duringTime; i++) {
                    if (jobCanceled) return;
                    Log.d(TAG, "run: " + jobId + " " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

//                cancelNotification();

//                jobFinished(jobParameters, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Stop Job " + jobId);
        cancelNotification(jobId);
//        stopSelf();
        jobCanceled = true;
        return true;
    }

    public void sendNotification(int id, String title, String content) {
//        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
//        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
//                (this, jobId, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification notifyBuilder = getNotificationBuilder(id, title, content);

        //icon in notification
//        notifyBuilder.addAction(R.drawable.ic_cancel, "Cancel", updatePendingIntent);
        startForeground(jobId, notifyBuilder);
//        mNotifyManager.notify(jobId, notifyBuilder.build());
        //setNotificationButtonState(false, true, true);
    }


    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Note Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Note App");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private Notification getNotificationBuilder(int id, String title, String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notify = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setColor(Color.RED)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_notes_notification)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setAutoCancel(true)
                .build();
        return notify;
    }

    public void cancelNotification(int id) {
        mNotifyManager.cancel(id);
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        jobScheduler.cancelAll();
        //setNotificationButtonState(true, false, false);
    }

}