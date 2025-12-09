package com.example.notesapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.notesapp.DataModelClass.ReminderData;

import java.util.List;

public class OverdueWorker extends Worker {

    public OverdueWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDataBase db = AppDataBase.getInstance(getApplicationContext());
        List<ReminderData> pending = db.reminderDao().getPendingRemindersForOverDue();
        for (ReminderData r : pending) {
            ReminderNotificationHelper.showInstantNotification(getApplicationContext(), "Pending Reminder", r.title + "is still not completed");
        }
        return Result.success();
    }
}
