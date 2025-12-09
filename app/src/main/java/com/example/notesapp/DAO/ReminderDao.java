package com.example.notesapp.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notesapp.DataModelClass.ReminderData;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    long insertReminder(ReminderData reminderData);
    @Query("select * from reminders where userId = :userId and isCompleted=0")
    List<ReminderData> getPendingReminders(int userId);
    @Query("select * from reminders where userId = :userId and isCompleted=1")
    List<ReminderData> getCompletedReminders(int userId);

    @Query("update reminders set isCompleted =1 where id = :id")
    void completeReminder(int id);
    @Query("select * from reminders where isCompleted=0")
    List<ReminderData> getPendingRemindersForOverDue();




}
