package com.example.notesapp;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.DataModelClass.AlarmData;
import com.example.notesapp.reminder.AlarmScheduler;

public class AlarmRingingActivity extends AppCompatActivity {
    Ringtone ringtone;
    int alarmId;
    AlarmData alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);
        getSupportActionBar().hide();


        TextView titleTv = findViewById(R.id.alarmTitle);
        Button btnStop = findViewById(R.id.btnStopAlarm);
        Button btnSnooze = findViewById(R.id.btnSnoozeAlarm);


        alarmId = getIntent().getIntExtra("alarm_id", -1);
        String title = getIntent().getStringExtra("title");
        String ringtoneUri = getIntent().getStringExtra("ringtone");

        titleTv.setText(title != null ? title : "Alarm");
        Uri uri = (ringtoneUri != null && !ringtoneUri.isEmpty()) ? Uri.parse(ringtoneUri) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            ringtone = RingtoneManager.getRingtone(this, uri);
            ringtone.setStreamType(AudioManager.STREAM_ALARM);
            ringtone.play();
        } catch (Exception e) {
            // fallback to default
            ringtone = null;
        }
        btnStop.setOnClickListener(v -> stopAlarm());
        btnSnooze.setOnClickListener(v -> snoozeAlarm());


    }
    private void stopAlarm(){
        if(ringtone !=null && ringtone.isPlaying()) ringtone.stop();

        new Thread(()->{
            AppDataBase dataBase=AppDataBase.getInstance(this);
            dataBase.alarmDao().markAlarmCompleted(alarmId);

            AlarmScheduler.cancelAlarm(this,alarmId);
            runOnUiThread(this::finish);
        }).start();

    }

    private void snoozeAlarm() {
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();

        // get alarm info and schedule snooze
        new Thread(() -> {
            AppDataBase db = AppDataBase.getInstance(this);
            AlarmData a = db.alarmDao().getAlarmById(alarmId);
            int snoozeMinutes = (a != null) ? a.snoozeMinutes : 5;
            AlarmScheduler.scheduleSnooze(this, a, snoozeMinutes);
            runOnUiThread(this::finish);
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
    }

}