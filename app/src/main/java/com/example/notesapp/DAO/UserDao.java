package com.example.notesapp.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notesapp.DataModelClass.UserData;

import java.util.List;

@Dao
public interface UserDao {
@Insert
long insertUser(UserData userData);
@Query("SELECT * FROM users where email = :emaill LIMIT 1")
    UserData getUserByEmail(String emaill);
@Query("select * from users")
    List<UserData> getAllUsers();
@Query("select * from users where email =:email and password = :password limit 1")
    UserData logindata(String email,String password);


}
