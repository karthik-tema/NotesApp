package com.example.notesapp.DataModelClass;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class AlarmData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String title;
    public String date;      // dd/MM/yyyy
    public String time;
    public boolean isActive;// HH:mm (24h)
    public boolean isCompleted;
    public int snoozeMinutes; // default 5
    public String ringtone;   // optional uri string

    public AlarmData(int id, int userId, String title, String date, String time,boolean isActive, boolean isCompleted, int snoozeMinutes, String ringtone) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.isActive=isActive;
        this.isCompleted = isCompleted;
        this.snoozeMinutes = snoozeMinutes;
        this.ringtone = ringtone;
    }
    @Ignore
    public AlarmData(int userId, String title, String selectedDate, String selectedTime, int snoozeMin, boolean isActive, boolean isCompleted) {
        this.userId=userId;
        this.title=title;
        this.date=selectedDate;
        this.time=selectedTime;
        this.snoozeMinutes=snoozeMin;
        this.isActive=isActive;
        this.isCompleted=isCompleted;

    }
}
