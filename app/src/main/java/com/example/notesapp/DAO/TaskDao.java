package com.example.notesapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notesapp.DataModelClass.TaskData;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insertTask(TaskData task);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0")
    List<TaskData> getTasksForUser(int userId);
    @Query("select * from tasks where userId = :userId and isCompleted =1")
    List<TaskData> getCompleteTasks(int userId);

    @Query("UPDATE tasks SET isCompleted = 1 WHERE id = :taskId")
    void completeTask(int taskId);
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    TaskData getTaskById(int taskId);

    @Delete
    void deleteTask(TaskData task);

    @Query("UPDATE tasks SET isCompleted = 1 WHERE id = :taskId")
    void markTaskCompleted(int taskId);
}
