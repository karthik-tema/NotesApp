package com.example.notesapp.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.notesapp.DataModelClass.ReminderData;
import com.example.notesapp.ReminderReceiver;

import java.util.Calendar;

public class ReminderScheduler {

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleReminder(Context context, ReminderData reminder) {

        // Parse date → "4/3/2025"
        String[] dateParts = reminder.date.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // 0-based
        int year = Integer.parseInt(dateParts[2]);

        // Parse time → "3:41"
        String[] timeParts = reminder.time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long triggerAtMillis = cal.getTimeInMillis();

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", reminder.title);
        intent.putExtra("desc", reminder.description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }
}
