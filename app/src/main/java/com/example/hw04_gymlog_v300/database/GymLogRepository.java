package com.example.hw04_gymlog_v300.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.hw04_gymlog_v300.database.entities.GymLog;
import com.example.hw04_gymlog_v300.database.entities.User;
import java.util.List;

public class GymLogRepository {
    private final GymLogDao gymLogDao;
    private final UserDao userDao;
    private static GymLogRepository repository;

    private GymLogRepository(Application application) {
        AppDataBase db = AppDataBase.getDatabase(application);
        gymLogDao = db.gymLogDao();
        userDao = db.userDao();
    }

    public static GymLogRepository getRepository(Application application) {
        if (repository == null) {
            repository = new GymLogRepository(application);
        }
        return repository;
    }

    public LiveData<List<GymLog>> getAllLogsByUserId(int userId) {
        return gymLogDao.getRecordsByUserId(userId);
    }

    public void insertGymLog(GymLog gymLog) {
        AppDataBase.databaseWriteExecutor.execute(() -> {
            gymLogDao.insert(gymLog);
        });
    }

    public void insertUser(User... user) {
        AppDataBase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public LiveData<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public LiveData<User> getUserByUserId(int userId) {
        return userDao.getUserByUserId(userId);
    }
}