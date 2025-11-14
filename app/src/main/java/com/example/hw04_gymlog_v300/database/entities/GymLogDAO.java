package com.example.hw04_gymlog_v300.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hw04_gymlog_v300.database.entities.AppDataBase;
import com.example.hw04_gymlog_v300.database.entities.GymLog;
import java.util.List;

@Dao
public interface GymLogDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GymLog gymLog);

    @Query("SELECT * FROM " + AppDatabase.GYM_LOG_TABLE + " ORDER BY date DESC")
    List<GymLog> getAllRecords();

    @Query("SELECT * FROM " + AppDatabase.GYM_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    List<GymLog> getRecordsByUserId(int loggedInUserId);

    @Query("SELECT * FROM " + AppDatabase.GYM_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    LiveData<List<GymLog>> getRecordsByUserIdLiveData(int loggedInUserId);
}