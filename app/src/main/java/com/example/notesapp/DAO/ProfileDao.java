package com.example.notesapp.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notesapp.DataModelClass.ProfileData;

import java.util.List;

@Dao
public interface ProfileDao {
    @Insert
    long insertUser(ProfileData profileData);
    @Update
    void updateUser(ProfileData profileData);
    @Query("select * from profile where userId =:userId")
    ProfileData getProfileData(int userId);


}
