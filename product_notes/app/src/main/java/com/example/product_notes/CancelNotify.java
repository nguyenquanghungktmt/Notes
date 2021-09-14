package com.example.product_notes;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Broadcast receiver for the alarm, which delivers the notification.
 */
public class CancelNotify extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    // Notification ID.
    private int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    private static final String TAG = "myTag";

    private String receiveTitle;
    private String receiveContent;

    /**
     * Called when the BroadcastReceiver receives an Intent broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NOTIFICATION_ID = intent.getIntExtra("NOTIFICATION_ID", 0);

        Log.d(TAG, "onReceive: Cancel " + NOTIFICATION_ID);

        mNotificationManager.cancel(NOTIFICATION_ID);
    }

}
