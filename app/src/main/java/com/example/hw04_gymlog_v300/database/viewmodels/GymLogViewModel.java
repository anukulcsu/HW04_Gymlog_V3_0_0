package com.example.hw04_gymlog_v300.database.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hw04_gymlog_v300.database.entities.GymLog;
import com.example.hw04_gymlog_v300.database.entities.GymLogRepository;

import java.util.List;


public class GymLogViewModel extends AndroidViewModel {

    private GymLogRepository mRepository;

    public GymLogViewModel(Application application) {
        super(application);
        mRepository = GymLogRepository.getRepository(application);
    }

    public LiveData<List<GymLog>> getAllLogsByUserId(int userId) {
        return mRepository.getAllLogsByUserIdLiveData(userId);
    }

    public void insert(GymLog gymLog) {
        mRepository.insert(gymLog);
    }
}