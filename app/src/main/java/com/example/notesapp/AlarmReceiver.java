package com.example.notesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarm_id", -1);
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String ringtone = intent.getStringExtra("ringtone");
        System.out.println("AlarmReceiver "+alarmId);
        AlarmNotificationHelper.showAlarmNotification(context, alarmId, title, desc);

// Launch ringing Activity (full screen)
        Intent ringIntent = new Intent(context, AlarmRingingActivity.class);
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ringIntent.putExtra("alarm_id", alarmId);
        ringIntent.putExtra("title", title);
        ringIntent.putExtra("ringtone", ringtone);
        context.startActivity(ringIntent);

    }
}
