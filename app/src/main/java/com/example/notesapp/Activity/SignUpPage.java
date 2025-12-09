package com.example.notesapp.Activity;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.AppDataBase;
import com.example.notesapp.EncryptionUtil;
import com.example.notesapp.R;
import com.example.notesapp.DataModelClass.UserData;

public class SignUpPage extends AppCompatActivity {
    Button submit_btn;
    EditText signUpName,signUpEmail,signUpPassword,signUpRePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_up_page);
        getSupportActionBar().hide();

        signUpName=findViewById(R.id.name);
        signUpEmail=findViewById(R.id.email);
        signUpPassword=findViewById(R.id.password);
        signUpRePassword=findViewById(R.id.reEnterPassword);
        submit_btn=findViewById(R.id.button1);

        submit_btn.setOnClickListener(v -> {

            String sname = signUpName.getText().toString().trim();
            String semail = EncryptionUtil.encrypt(signUpEmail.getText().toString().trim());
            String spassword = EncryptionUtil.encrypt(signUpPassword.getText().toString().trim());
            String sRePassword = EncryptionUtil.encrypt(signUpRePassword.getText().toString().trim());

            // Validation
            if (sname.isEmpty()) {
                signUpName.setError("Please Enter Name");
                return;
            }
            if (semail.isEmpty()) {
                signUpEmail.setError("Please Enter Email");
                return;
            }
            if (spassword.isEmpty()) {
                signUpPassword.setError("Please Enter Password");
                return;
            }
            if (sRePassword.isEmpty()) {
                signUpRePassword.setError("Please Re-Enter Password");
                return;
            }
            if (!spassword.equals(sRePassword)) {
                signUpRePassword.setError("Passwords do not match");
                return;
            }

            // RUN IN BACKGROUND THREAD
            new Thread(() -> {

                // Check duplicate email
                UserData existingUser = AppDataBase.getInstance(getApplicationContext()).userDao().getUserByEmail(semail);

                if (existingUser != null) {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Email Already Registered!", LENGTH_SHORT).show()
                    );
                    return;
                }

                // Insert user
                UserData newUser = new UserData(sname, semail, spassword);
                AppDataBase.getInstance(getApplicationContext()).userDao().insertUser(newUser);

                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Signup Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(getApplicationContext(), LoginPage.class);
                    startActivity(intent);
                    finish();

                    // Move to next page if needed
                    // startActivity(new Intent(SignUpPage.this, LoginPage.class));
                    // finish();
                });

            }).start();

        });

    }
}