package com.example.notesapp.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.notesapp.AlarmReceiver;
import com.example.notesapp.DataModelClass.AlarmData;

import java.util.Calendar;

public class AlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleAlarm(Context context, AlarmData alarm) {
        if (alarm == null) return;

        // parse date dd/MM/yyyy
        String[] d = alarm.date.split("/");
        // parse time HH:mm
        String[] t = alarm.time.split(":");

        int day = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]) - 1;
        int year = Integer.parseInt(d[2]);

        int hour = Integer.parseInt(t[0]);
        int minute = Integer.parseInt(t[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long triggerAt = cal.getTimeInMillis();

        // if the time already passed, you can decide: schedule next day OR keep as-is.
        // Here we schedule it for the next day if it's already past:
        if (triggerAt < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            triggerAt = cal.getTimeInMillis();
        }
        System.out.println("Alarm schedular"+triggerAt);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm_id", alarm.id);
        intent.putExtra("title", alarm.title);
        intent.putExtra("desc", "Alarm: " + alarm.title);
        intent.putExtra("ringtone", alarm.ringtone);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleSnooze(Context context, AlarmData alarm, int minutesFromNow) {
        if (alarm == null) return;
        long triggerAt = System.currentTimeMillis() + minutesFromNow * 60L * 1000L;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm_id", alarm.id);
        intent.putExtra("title", alarm.title);
        intent.putExtra("desc", "Snoozed alarm: " + alarm.title);
        intent.putExtra("ringtone", alarm.ringtone);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, int alarmId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                am.cancel(pi);
            }
            pi.cancel();
        }
    }
}
