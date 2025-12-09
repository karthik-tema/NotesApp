package com.example.notesapp.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.notesapp.Adapters.AlarmAdapter;
import com.example.notesapp.Adapters.ReminderAdapter;
import com.example.notesapp.AppDataBase;
import com.example.notesapp.DataModelClass.AlarmData;
import com.example.notesapp.DataModelClass.ReminderData;
import com.example.notesapp.OverdueWorker;
import com.example.notesapp.R;
import com.example.notesapp.SessionManager;
import com.example.notesapp.Adapters.TaskAdapter;
import com.example.notesapp.DataModelClass.TaskData;
import com.example.notesapp.reminder.AlarmScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomePage extends AppCompatActivity {

    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    List<TaskData> taskList;
    FloatingActionButton addButton;
    ImageView menuBtn;
    TextView notes,reminder,alarm;
    int userId;
    int selectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getSupportActionBar().hide();
        startOverdueWorker();


        // ToolBar
        TextView title=findViewById(R.id.toolbartitle);
        title.setText("Home");

        notes=findViewById(R.id.notes);
        reminder=findViewById(R.id.reminder);
        alarm=findViewById(R.id.alaram);

        addButton=findViewById(R.id.btnAddTask);
        menuBtn=findViewById(R.id.menuIcon);
        recyclerView = findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userId = new SessionManager(this).getUserId();

        highlightTab(notes);
        loadTasks();

        notes.setOnClickListener(v -> {
            selectedTab = 0;
            highlightTab(notes);
            loadTasks();
        });

        reminder.setOnClickListener(v -> {
            selectedTab = 1;
            highlightTab(reminder);
            loadReminderList();
        });

        alarm.setOnClickListener(v -> {
            selectedTab = 2;
            highlightTab(alarm);
            loadAlarmList();   // ✔ load alarm list
        });

        addButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomePage.this, addButton);
            popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();

                if (id == R.id.menu_add_task) {
                    startActivity(new Intent(HomePage.this, AddTaskActivity.class));
                    return true;
                }

                if (id == R.id.menu_add_reminder) {
                    startActivity(new Intent(HomePage.this, AddReminderActivity.class));
                    return true;
                }

                if (id == R.id.menu_add_alarm) {
                    startActivity(new Intent(HomePage.this, AddAlarmActivity.class));
                    return true;
                }

                return false;
            });

            popup.show();
        });

        menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu =new PopupMenu(HomePage.this,menuBtn);
            popupMenu.getMenuInflater().inflate(R.menu.menu_option,popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item ->{
                int id =item.getItemId();
                if(id == R.id.profile){
                    startActivity(new Intent(HomePage.this, ProfilePageActivity.class));
                    return true;
                }else if(id == R.id.logout){
                    new SessionManager(this).logout();

                    Intent i = new Intent(HomePage.this, LoginPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                return false;
            });
            popupMenu.show();
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (selectedTab == 0) {
            loadTasks();
        } else if (selectedTab == 1) {
            loadReminderList();
        } else if (selectedTab == 2) {
            loadAlarmList();   // ✔ fix
        }

    }

    private void loadReminderList() {

        new Thread(() -> {

            AppDataBase db = AppDataBase.getInstance(this);

            List<ReminderData> pending = db.reminderDao().getPendingReminders(userId);
            List<ReminderData> completed = db.reminderDao().getCompletedReminders(userId);

            List<Object> finalList = new ArrayList<>();

            if (!pending.isEmpty()) {
                finalList.add("Upcoming Reminders");
                finalList.addAll(pending);
            }

            if (!completed.isEmpty()) {
                finalList.add("Completed Reminders");
                finalList.addAll(completed);
            }

            runOnUiThread(() -> {

                // ⭐ ALWAYS create NEW adapter
                ReminderAdapter adapter =
                        new ReminderAdapter(HomePage.this, finalList, id -> completeReminder(id));

                recyclerView.setAdapter(adapter);
            });

        }).start();
    }

    private void loadTasks() {

        new Thread(() -> {

            AppDataBase db = AppDataBase.getInstance(this);

            List<TaskData> pending = db.taskDao().getTasksForUser(userId);
            List<TaskData> completed = db.taskDao().getCompleteTasks(userId);

            List<Object> finalList = new ArrayList<>();

            if (!pending.isEmpty()) {
                finalList.add("Pending Tasks");
                finalList.addAll(pending);
            }

            if (!completed.isEmpty()) {
                finalList.add("Completed Tasks");
                finalList.addAll(completed);
            }

            runOnUiThread(() -> {

                // ⭐ ALWAYS create NEW Adapter when switching tab
                taskAdapter = new TaskAdapter(
                        finalList,
                        HomePage.this,
                        id -> markTaskCompleted(id),
                        id -> openTaskDetails(id)
                );

                recyclerView.setAdapter(taskAdapter);
            });

        }).start();
    }

    private void loadAlarmList() {
        new Thread(() -> {
            AppDataBase db = AppDataBase.getInstance(this);
            List<AlarmData> pending = db.alarmDao().getUpcomingAlarms(userId);
            List<AlarmData> completed = db.alarmDao().getCompletedAlarms(userId);

            List<Object> finalList = new ArrayList<>();
            if (!pending.isEmpty()) {
                finalList.add("Upcoming Alarms");
                finalList.addAll(pending);
            }
            if (!completed.isEmpty()) {
                finalList.add("Completed Alarms");
                finalList.addAll(completed);
            }

            runOnUiThread(() -> {
                // Create listener implementing all callbacks
                AlarmAdapter.OnAlarmActionListener listener = new AlarmAdapter.OnAlarmActionListener() {
                    @Override
                    public void onToggle(int alarmId, boolean isOn) {
                        // Update DB active state and schedule/cancel system alarm
                        new Thread(() -> {
                            AppDataBase.getInstance(HomePage.this).alarmDao().isActive(alarmId, isOn ? 1 : 0);
                            if (isOn) {
                                // load alarm details to schedule
                                AlarmData alarm = AppDataBase.getInstance(HomePage.this).alarmDao().getAlarmById(alarmId);
                                AlarmScheduler.scheduleAlarm(HomePage.this, alarm);
                            } else {
                                AlarmScheduler.cancelAlarm(HomePage.this, alarmId);
                            }
                            runOnUiThread(HomePage.this::loadAlarmList);
                        }).start();
                    }

                    @Override
                    public void onDelete(int alarmId) {
                        new Thread(() -> {
                            AlarmScheduler.cancelAlarm(HomePage.this, alarmId);
                            AppDataBase.getInstance(HomePage.this).alarmDao().deleteAlarmById(alarmId);
                            runOnUiThread(HomePage.this::loadAlarmList);
                        }).start();
                    }

                    @Override
                    public void onEdit(int alarmId) {
                        Intent i = new Intent(HomePage.this, AddAlarmActivity.class);
                        i.putExtra("edit_alarm_id", alarmId);
                        startActivity(i);
                    }

                    @Override
                    public void onCompleted(int alarmId) {
                        markAlarmCompleted(alarmId);
                    }
                };

                // Always create new adapter instance (keeps simple state)
                AlarmAdapter adapter = new AlarmAdapter(HomePage.this, finalList, listener);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    private void markAlarmCompleted(int alarmId) {
        new Thread(() -> {
            AppDataBase.getInstance(this).alarmDao().markAlarmCompleted(alarmId);
            runOnUiThread(this::loadAlarmList); // <-- NOTE: loadAlarmList (not loadAlarms)
        }).start();
    }



    private void highlightTab(TextView selectedTab) {

        // Reset all tabs
        notes.setBackgroundResource(R.drawable.rounded_box);
        reminder.setBackgroundResource(R.drawable.rounded_box);
        alarm.setBackgroundResource(R.drawable.rounded_box);

        notes.setTextColor(getColor(android.R.color.black));
        reminder.setTextColor(getColor(android.R.color.black));
        alarm.setTextColor(getColor(android.R.color.black));

        // Highlight selected tab
        selectedTab.setBackgroundResource(R.drawable.rounded_box_selected);
        selectedTab.setTextColor(getColor(android.R.color.white));
    }
    private void markTaskCompleted(int taskId) {
        new Thread(() -> {
            AppDataBase.getInstance(this).taskDao().markTaskCompleted(taskId);

            runOnUiThread(this::loadTasks); // refresh list
        }).start();
    }

    private void openTaskDetails(int taskId) {
        Intent intent = new Intent(HomePage.this, TaskDetailActivity.class);
        intent.putExtra("task_id", taskId);
        startActivity(intent);
    }
    private void completeReminder(int reminderId) {

        new Thread(() -> {

            AppDataBase.getInstance(HomePage.this)
                    .reminderDao()
                    .completeReminder(reminderId);

            runOnUiThread(this::loadReminderList);  // refresh UI

        }).start();
    }
private  void startOverdueWorker(){
    PeriodicWorkRequest periodicWorkRequest=new PeriodicWorkRequest.Builder(OverdueWorker.class,24, TimeUnit.HOURS).build();
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "OverdueWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest);

}

}
