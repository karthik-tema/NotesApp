    package com.example.notesapp.Activity;

    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.notesapp.AppDataBase;
    import com.example.notesapp.R;
    import com.example.notesapp.SessionManager;
    import com.example.notesapp.DataModelClass.TaskData;

    public class TaskDetailActivity extends AppCompatActivity {

        TextView detailTitle, detailDesc;
        Button btnCompleteTask;
        int taskId;
        int userId;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_task_detail);

            getSupportActionBar().hide();
            TextView title=findViewById(R.id.toolbartitle);
            title.setText("Home");

            detailTitle = findViewById(R.id.detailTitle);
            detailDesc = findViewById(R.id.detailDesc);
            btnCompleteTask = findViewById(R.id.btnCompleteTask);

            // Receive data
            taskId = getIntent().getIntExtra("taskId", -1);
            userId = new SessionManager(this).getUserId();

            // Load task from DB
            TaskData task = AppDataBase.getInstance(this)
                    .taskDao()
                    .getTaskById(taskId);

            if (task != null) {
                detailTitle.setText(task.title);
                detailDesc.setText(task.description);
            }

            btnCompleteTask.setOnClickListener(v -> {
                AppDataBase.getInstance(this).taskDao().completeTask(taskId);
                finish();
            });

        }
    }
