package com.example.notesapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notesapp.DataModelClass.AlarmData;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert
    long insertAlarm(AlarmData alarm);

    @Update
    void updateAlarm(AlarmData alarm);

    @Delete
    void deleteAlarm(AlarmData alarm);

    @Query("select * from alarms where userId = :userId and isCompleted = 0 order by date, time")
    List<AlarmData> getUpcomingAlarms(int userId);

    @Query("select * from alarms where userId = :userId and isCompleted = 1 order by date, time")
    List<AlarmData> getCompletedAlarms(int userId);

    @Query("update alarms set isCompleted =1 where id = :id")
    void markAlarmCompleted(int id);

    @Query("select * from alarms where id = :id")
    AlarmData getAlarmById(int id);

    @Query("UPDATE alarms SET isActive = :active WHERE id = :alarmId")
    void isActive(int alarmId, int active); // active: 1 or 0

    @Query("DELETE FROM alarms WHERE id = :alarmId")
    void deleteAlarmById(int alarmId);
}