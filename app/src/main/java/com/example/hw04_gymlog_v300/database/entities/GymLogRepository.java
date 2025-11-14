package com.example.hw04_gymlog_v300.database.entities;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.hw04_gymlog_v300.database.GymLogDAO;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GymLogRepository {
    private final GymLogDAO mGymLogDao;
    private final UserDAO mUserDao;
    private static volatile GymLogRepository repository;

    private GymLogRepository(Application application) {
        AppDataBase db = AppDataBase.getDatabase(application);
        mGymLogDao = db.gymLogDao();
        mUserDao = db.userDAO();
    }

    public static GymLogRepository getRepository(final Application application) {
        if (repository != null) {
            return repository;
        }
        Future<GymLogRepository> future = AppDataBase.databaseWriteExecutor.submit(
                new Callable<GymLogRepository>() {
                    @Override
                    public GymLogRepository call() throws Exception {
                        repository = new GymLogRepository(application);
                        return repository;
                    }
                }
        );
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(MainActivity.TAG, "Problem getting Gym log repository. thread error");
        }
        return null;
    }

    public void insertGymLog(GymLog gymLog) {
        AppDataBase.databaseWriteExecutor.execute(() -> {
            mGymLogDao.insert(gymLog);
        });
    }

    public void insertUser(User... user) {
        AppDataBase.databaseWriteExecutor.execute(() -> {
            mUserDao.insert(user);
        });
    }

    public LiveData<List<GymLog>> getAllLogsByUserIdLiveData(int loggedInUserId) {
        return mGymLogDao.getRecordsByUserId(loggedInUserId);
    }

    public LiveData<User> getUserByUsername(String username) {
        return mUserDao.getUserByUsername(username);
    }

    public LiveData<User> getUserByUserId(int userId) {
        return mUserDao.getUserByUserId(userId);
    }
}