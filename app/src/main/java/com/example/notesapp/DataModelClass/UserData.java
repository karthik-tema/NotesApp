package com.example.notesapp.DataModelClass;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserData {
    @PrimaryKey(autoGenerate = true)
    public int userId;

    private String name;
    private String email;
    private String password;
    private String rePassword;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public UserData(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
