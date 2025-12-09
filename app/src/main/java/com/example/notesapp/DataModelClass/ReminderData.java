package com.example.notesapp.DataModelClass;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class ReminderData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String title;
    public String description;
    public String date;
    public String time;
    public boolean isCompleted;

    public ReminderData(int userId, String title, String description, String date, String time) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.isCompleted = false;
    }
}
