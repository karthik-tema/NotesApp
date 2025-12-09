package com.example.notesapp.Activity;



import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import com.example.notesapp.DataModelClass.ReminderData;
import com.example.notesapp.AppDataBase;
import com.example.notesapp.R;
import com.example.notesapp.ReminderNotificationHelper;
import com.example.notesapp.SessionManager;
import com.example.notesapp.reminder.ReminderScheduler;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    EditText titleEd, descEd;
    TextView dateEd, timeEd;
    Button saveBtn;

    int userId;
    String selectedDate = "";
    String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        getSupportActionBar().hide();


        titleEd = findViewById(R.id.remTitleEd);
        descEd = findViewById(R.id.remDescEd);
        dateEd = findViewById(R.id.remDateEd);
        timeEd = findViewById(R.id.remTimeEd);
        saveBtn = findViewById(R.id.btnSaveReminder);

        userId = new SessionManager(this).getUserId();

        // Date Picker
        dateEd.setOnClickListener(v -> openDatePicker());

        // Time Picker
        timeEd.setOnClickListener(v -> openTimePicker());

        // Save Button Click
        saveBtn.setOnClickListener(v -> saveReminder());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = day + "/" + (month + 1) + "/" + year;
            dateEd.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {
        Calendar c = Calendar.getInstance();

        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedTime = String.format("%02d:%02d", hour, minute);
            timeEd.setText(selectedTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void saveReminder() {

        String title = titleEd.getText().toString().trim();
        String desc = descEd.getText().toString().trim();

        requestExactAlarmPermission();

        if (title.isEmpty() || desc.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        ReminderData reminder = new ReminderData(
                userId, title, desc, selectedDate, selectedTime
        );

        long id = AppDataBase.getInstance(this).reminderDao().insertReminder(reminder);
        reminder.id =(int) id;

        ReminderScheduler.scheduleReminder(this, reminder);

        Toast.makeText(this, "Reminder Added & Scheduled!", Toast.LENGTH_SHORT).show();
        finish();

        ReminderNotificationHelper.showInstantNotification(this,"Reminder Added",reminder.title);
     //   ReminderNotificationHelper.ScheduleReminder(this,reminder.id,selectedDate,selectedTime,reminder.title,reminder.description);

    }
    private void requestExactAlarmPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {

                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);

            }
        }
    }

}
