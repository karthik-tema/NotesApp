package com.example.notesapp.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.AppDataBase;
import com.example.notesapp.R;
import com.example.notesapp.SessionManager;
import com.example.notesapp.DataModelClass.TaskData;

public class AddTaskActivity extends AppCompatActivity {

    EditText titleEd, descEd;
    Button addBtn;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        getSupportActionBar().hide();

        TextView toolbar=findViewById(R.id.toolbartitle);
        toolbar.setText("Add Task");

        titleEd = findViewById(R.id.edTitle);
        descEd = findViewById(R.id.edDesc);
        addBtn = findViewById(R.id.btnAdd);

        userId = new SessionManager(this).getUserId();

        addBtn.setOnClickListener(v -> {

            String title = titleEd.getText().toString().trim();
            String desc = descEd.getText().toString().trim();

            TaskData task = new TaskData(userId, title, desc);

            AppDataBase.getInstance(this).taskDao().insertTask(task);

            Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();

            finish(); // go back to Home
        });




    }
}
