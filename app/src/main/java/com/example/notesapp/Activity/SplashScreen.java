package com.example.notesapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.R;
import com.example.notesapp.SessionManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        // Make splash fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Delay for 2 seconds
        new Handler().postDelayed(() -> {

            SessionManager sessionManager = new SessionManager(getApplicationContext());
            int userId = sessionManager.getUserId();

            if (userId != -1) {
                // User already logged in
                startActivity(new Intent(SplashScreen.this, HomePage.class));
            } else {
                // No session → go to login screen
                startActivity(new Intent(SplashScreen.this, LoginPage.class));
            }

            finish(); // close splash

        }, 2000);  // Splash duration
    }
}
