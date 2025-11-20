package com.example.hw04_gymlog_v300.database.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.hw04_gymlog_v300.database.GymLogRepository;
import com.example.hw04_gymlog_v300.database.entities.GymLog;
import java.util.List;

public class GymLogViewModel extends AndroidViewModel {
    private final GymLogRepository repository;

    public GymLogViewModel(Application application) {
        super(application);
        repository = GymLogRepository.getRepository(application);
    }

    public LiveData<List<GymLog>> getAllLogsByUserId(int userId) {
        return repository.getAllLogsByUserId(userId);
    }

    public void insert(GymLog gymLog) {
        repository.insertGymLog(gymLog);
    }
}