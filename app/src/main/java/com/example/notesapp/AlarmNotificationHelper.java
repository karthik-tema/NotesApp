package com.example.notesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.media.RingtoneManager;

public class AlarmNotificationHelper {
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarms";

    /**
     * Show an alarm notification and attach a full-screen intent to open AlarmRingingActivity.
     * This will also ensure the notification channel uses the alarm sound on O+ devices.
     */
    public static void showAlarmNotification(Context context, int alarmId, String title, String text) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return; // safety

        // 1) Create / update channel with alarm sound on O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSoundUri == null) {
                alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(alarmSoundUri, audioAttributes);
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(channel);
        }

        // 2) Intent to open ringing activity when user taps notification
        Intent tapIntent = new Intent(context, AlarmRingingActivity.class);
        tapIntent.putExtra("alarm_id", alarmId);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent tapPending = PendingIntent.getActivity(
                context,
                alarmId, // unique per alarm
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        System.out.println("tapPending "+alarmId);
        // 3) Full-screen intent (opens immediately on high-priority)
        Intent fullScreenIntent = new Intent(context, AlarmRingingActivity.class);
        fullScreenIntent.putExtra("alarm_id", alarmId);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent fullScreenPending = PendingIntent.getActivity(
                context,
                alarmId + 100000, // different requestCode from tapPending
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        System.out.println("fullScreenPending "+alarmId);

        // 4) Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title == null ? "Alarm" : title)
                .setContentText(text == null ? "" : text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(tapPending)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Attach the full screen intent so Activity can appear over the lockscreen
                .setFullScreenIntent(fullScreenPending, true);

        // 5) Show notification
        manager.notify(alarmId, builder.build());
    }
}
