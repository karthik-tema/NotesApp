package com.example.notesapp.Activity;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.AppDataBase;
import com.example.notesapp.EncryptionUtil;
import com.example.notesapp.R;
import com.example.notesapp.SessionManager;
import com.example.notesapp.DataModelClass.UserData;
import com.google.android.material.textfield.TextInputEditText;

public class LoginPage extends AppCompatActivity {

    TextInputEditText loginPassword, loginEmail;
    Button loginButton;
    TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getSupportActionBar().hide();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.btnLogin);
        signupLink = findViewById(R.id.signupRedirect);

        loginButton.setOnClickListener(v -> {

            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty()) {
                loginEmail.setError("Please enter email");
                return;
            }
            if (password.isEmpty()) {
                loginPassword.setError("Please enter password");
                return;
            }
            // comment added

            // Background thread
            new Thread(() -> {

                String encEmail = EncryptionUtil.encrypt(email);
                String encPassword = EncryptionUtil.encrypt(password);

                UserData user = AppDataBase.getInstance(getApplicationContext())
                        .userDao()
                        .logindata(encEmail, encPassword);


                if (user != null) {
                    // Login success
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Login Successful!", LENGTH_SHORT).show();
                        SessionManager session = new SessionManager(getApplicationContext());
                        session.saveUser(user.userId);
                        session.saveUserName(user.getName());


                        // Navigate to home or dashboard
                        Intent i = new Intent(LoginPage.this, HomePage.class);
                        startActivity(i);
                        finish();
                    });
                } else {
                    // Login failed
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Invalid email or password!", LENGTH_SHORT).show()
                    );
                }

            }).start();

        });
//new comment
        //new comment
        //new comment
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(intent);

            }
        });
    }

}
