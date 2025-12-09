package com.example.notesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.notesapp.Activity.HomePage;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");

        // Open Home Page when clicking notification
        Intent clickIntent = new Intent(context, HomePage.class);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, "Reminder Alerts",
                            NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
