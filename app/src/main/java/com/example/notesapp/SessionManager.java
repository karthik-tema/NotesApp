package com.example.notesapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "SESSION";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USER_NAME = "USER_NAME";

    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save user id
    public void saveUser(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Save user name
    public void saveUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    // Get user Id
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // Get user name
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Unknown User");
    }

    // Logout
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
