package com.example.notesapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notesapp.DAO.AlarmDao;
import com.example.notesapp.DAO.ProfileDao;
import com.example.notesapp.DAO.ReminderDao;
import com.example.notesapp.DAO.TaskDao;
import com.example.notesapp.DAO.UserDao;
import com.example.notesapp.DataModelClass.AlarmData;
import com.example.notesapp.DataModelClass.ProfileData;
import com.example.notesapp.DataModelClass.ReminderData;
import com.example.notesapp.DataModelClass.TaskData;
import com.example.notesapp.DataModelClass.UserData;

@Database(entities = {UserData.class, TaskData.class, ReminderData.class, AlarmData.class, ProfileData.class}, version = 5, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract ReminderDao reminderDao();
    public abstract AlarmDao alarmDao();
    public  abstract ProfileDao profileDao();


    private static volatile AppDataBase INSTANCE;

    public static synchronized AppDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "UserDB")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
