package com.example.notesapp.DataModelClass;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class ProfileData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String fullName;
    public String mobile;
    public String address;
    public String currentAddress;
    public String imageUri;


    public ProfileData(int userId, String fullName, String mobile, String address, String currentAddress,String imageUri) {
        this.userId = userId;
        this.fullName = fullName;
        this.mobile = mobile;
        this.address = address;
        this.currentAddress = currentAddress;
        this.imageUri=imageUri;
    }
}
