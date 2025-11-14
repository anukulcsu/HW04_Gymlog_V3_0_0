package com.example.hw04_gymlog_v300;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw04_gymlog_v300.database.entities.GymLog;
import com.example.hw04_gymlog_v300.database.entities.GymLogRepository;
import com.example.hw04_gymlog_v300.database.entities.User;
import com.example.hw04_gymlog_v300.database.viewmodels.GymLogAdapter;
import com.example.hw04_gymlog_v300.database.viewmodels.GymLogViewModel;
import com.example.hw04_gymlog_v300.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int LOGGED_OUT = -1;
    private ActivityMainBinding binding;
    public static final String TAG = "DAC_GYM_LOG";
    public static final String MAIN_ACTIVITY_USER_ID = "com.example.hw04_gymlog_v300.MAIN_ACTIVITY_USER_ID";
    public static final String SHARED_PREFERENCE_USER_ID_KEY = "com.example.hw04_gymlog_v300.SHARED_PREFERENCE_USER_ID_KEY";
    public static final String SHARED_PREFERENCE_USER_ID_VALUE = "com.example.hw04_gymlog_v300.SHARED_PREFERENCE_USER_ID_VALUE";

    private String mExercise = "";
    private double mWeight = 0.0;
    private int mReps = 0;
    private int mLoggedInUserId = LOGGED_OUT;

    private GymLogRepository repository;
    private User user;

    private GymLogViewModel gymLogViewModel;
    private GymLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repository = GymLogRepository.getRepository(getApplication());
        loginUser();

        if (mLoggedInUserId == LOGGED_OUT) {
            Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(intent);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.logDisplayRecyclerView;
        adapter = new GymLogAdapter(new GymLogAdapter.GymLogDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gymLogViewModel = new ViewModelProvider(this).get(GymLogViewModel.class);
        gymLogViewModel.getAllLogsById(mLoggedInUserId).observe(this, gymLogs -> {
            adapter.submitList(gymLogs);
        });

        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay();
                insertGymLogRecord();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        updateSharedPreference();
    }

    private void loginUser() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mLoggedInUserId = sharedPreferences.getInt(getString(R.string.reference_user_id_key), LOGGED_OUT);

        if (mLoggedInUserId == LOGGED_OUT) {
            mLoggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }

        if (mLoggedInUserId == LOGGED_OUT) {
            return;
        }

        LiveData<User> userObserver = repository.getUserByUserId(mLoggedInUserId);
        userObserver.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User u) {
                if (u != null) {
                    user = u;
                    invalidateOptionsMenu();
                    updateSharedPreference();
                } else {
                    mLoggedInUserId = LOGGED_OUT;
                }
            }
        });
    }

    private void getInformationFromDisplay() {
        mExercise = binding.exerciseInputEditText.getText().toString();

        try {
            mWeight = Double.parseDouble(binding.weightInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error reading value from weight Edit text", e);
            mWeight = 0.0;
        }

        try {
            mReps = Integer.parseInt(binding.repInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error reading value from reps Edit text", e);
            mReps = 0;
        }
    }

    private void insertGymLogRecord() {
        if (mExercise.isEmpty()) {
            return;
        }
        GymLog log = new GymLog(mExercise, mWeight, mReps, mLoggedInUserId);
        gymLogViewModel.insert(log);
    }

    static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.log_out_menu_item);
        item.setVisible(true);

        if (user == null) {
            return false;
        }
        item.setTitle(user.getUsername());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                showLogoutDialog();
                return false;
            }
        });
        return true;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setMessage("Log out?");
        alertBuilder.setPositiveButton("Log out",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
        alertBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertBuilder.create().show();
    }

    private void logout() {
        mLoggedInUserId = LOGGED_OUT;
        updateSharedPreference();
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
    }

    private void updateSharedPreference() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(getString(R.string.reference_user_id_key), mLoggedInUserId);
        sharedPrefEditor.apply();
    }
}