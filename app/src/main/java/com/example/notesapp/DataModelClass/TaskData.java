package com.example.notesapp.DataModelClass;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class TaskData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;       // Foreign key → stores which user created this
    public String title;
    public String description;
    public boolean isCompleted;

    public TaskData(int userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.isCompleted = false;
    }

}
