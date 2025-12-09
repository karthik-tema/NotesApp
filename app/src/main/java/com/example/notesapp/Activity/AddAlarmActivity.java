package com.example.notesapp.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.AppDataBase;
import com.example.notesapp.DataModelClass.AlarmData;
import com.example.notesapp.R;
import com.example.notesapp.SessionManager;
import com.example.notesapp.reminder.AlarmScheduler;

import java.util.Calendar;

public class AddAlarmActivity extends AppCompatActivity {

    EditText alarmTitle, alarmSnooze;
    TextView alarmDate, alarmTime;
    Button btnSave;

    String selectedDate = "";
    String selectedTime = "";
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        getSupportActionBar().hide();

        alarmTitle = findViewById(R.id.alarmTitle);
        alarmSnooze = findViewById(R.id.alarmSnooze);
        alarmDate = findViewById(R.id.alarmDate);
        alarmTime = findViewById(R.id.alarmTime);
        btnSave = findViewById(R.id.btnSaveAlarm);

        // 2. Check if editing existing alarm
        int editId = getIntent().getIntExtra("edit_alarm_id", -1);

        if (editId != -1) {
            new Thread(() -> {
                AlarmData a = AppDataBase.getInstance(this)
                        .alarmDao()
                        .getAlarmById(editId);

                runOnUiThread(() -> {
                    if (a != null) {
                        alarmTitle.setText(a.title);
                        alarmDate.setText(a.date);
                        alarmTime.setText(a.time);
                        alarmSnooze.setText(String.valueOf(a.snoozeMinutes));

                        btnSave.setText("Update Alarm"); // Optional
                    }
                });
            }).start();
        }

        userId = new SessionManager(this).getUserId();

        alarmDate.setOnClickListener(v -> openDatePicker());
        alarmTime.setOnClickListener(v -> openTimePicker());

        btnSave.setOnClickListener(v -> saveAlarm());
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            alarmDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {
        Calendar c = Calendar.getInstance();

        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedTime = hour + ":" + minute;
            alarmTime.setText(selectedTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void saveAlarm() {
        String title = alarmTitle.getText().toString().trim();
        String snoozeInput = alarmSnooze.getText().toString().trim();
        int snoozeMin = snoozeInput.isEmpty() ? 5 : Integer.parseInt(snoozeInput);

        if (title.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmData alarm = new AlarmData(
                userId,
                title,
                selectedDate,
                selectedTime,
                snoozeMin,
                true,  // alarm is ON
                false  // not completed
        );

        long id = AppDataBase.getInstance(this).alarmDao().insertAlarm(alarm);
        alarm.id = (int) id;

        //Add this line — REQUIRED for alarm ringing
        AlarmScheduler.scheduleAlarm(this, alarm);

        Toast.makeText(this, "Alarm Saved & Scheduled!", Toast.LENGTH_SHORT).show();
        finish();

    }
}
