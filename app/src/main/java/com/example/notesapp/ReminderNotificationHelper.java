package com.example.notesapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderNotificationHelper {

    private static final String CHANNEL_ID = "reminder_channel";

    // 1. Show instant notification
    public static void showInstantNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(manager);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // 2. Schedule reminder
    @SuppressLint("ScheduleExactAlarm")
    /*public static void ScheduleReminder(Context context,
                                        int reminderId,
                                        String date,
                                        String time,
                                        String title,
                                        String desc) {

        String[] d = date.split("/");   // dd/MM/yyyy
        String[] t = time.split(":");   // HH:mm  (FIXED!)

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(d[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(d[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d[0]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(t[1]));
        cal.set(Calendar.SECOND, 0);

        // If selected time is already passed, schedule it for next day
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Use exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }*/

    private static void createChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, "Reminder Alerts",
                            NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(channel);
        }
    }
}
