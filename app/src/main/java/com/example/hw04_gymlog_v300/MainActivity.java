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

    public static final String MAIN_ACTIVITY_USER_ID = "com.example.hw04_gymlog_v300.MAIN_ACTIVITY_USER_ID";
    public static final int LOGGED_OUT = -1;
    public static final String TAG = "DAC_GYM_LOG";

    private ActivityMainBinding binding;
    private String mExercise = "";
    private double mWeight = 0.0;
    private int mReps = 0;
    private GymLogRepository mRepository;
    private int mLoggedInUserId = LOGGED_OUT;
    private User mUser;

    private GymLogViewModel mGymLogViewModel;
    private GymLogAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mRepository = GymLogRepository.getRepository(getApplication());

        loginUser();

        if (mLoggedInUserId == LOGGED_OUT) {
            Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(intent);
            finish();
            return;
        }

        RecyclerView recyclerView = binding.logDisplayRecyclerView;
        mAdapter = new GymLogAdapter(new GymLogAdapter.GymLogDiff());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mGymLogViewModel = new ViewModelProvider(this).get(GymLogViewModel.class);

        mGymLogViewModel.getAllLogsByUserId(mLoggedInUserId).observe(this, logs -> {
            mAdapter.submitList(logs);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.log_out_menu_item);

        if (mUser != null) {
            item.setTitle(mUser.getUsername());
        } else {
            item.setTitle("Logged Out");
        }
        item.setVisible(true);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showLogoutDialog();
                return true;
            }
        });
        return true;
    }

    public static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }

    private void loginUser() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mLoggedInUserId = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);

        if (mLoggedInUserId == LOGGED_OUT) {
            mLoggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }

        if (mLoggedInUserId == LOGGED_OUT) {
            return;
        }

        updateSharedPreference();

        LiveData<User> userObserver = mRepository.getUserById(mLoggedInUserId);
        userObserver.observe(this, user -> {
            if (user != null) {
                mUser = user;
                invalidateOptionsMenu();
            }
        });
    }

    private void updateSharedPreference() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.preference_user_id_key), mLoggedInUserId);
        editor.apply();
    }

    private void logout() {
        mLoggedInUserId = LOGGED_OUT;
        updateSharedPreference();
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);

        Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
        startActivity(intent);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Log Out");
        alertBuilder.setMessage("Log Out?");

        alertBuilder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    private void insertGymLogRecord() {
        if (mExercise.isEmpty()) {
            return;
        }
        GymLog log = new GymLog(mExercise, mReps, mWeight, mLoggedInUserId);
        mGymLogViewModel.insert(log);
    }

    private void getInformationFromDisplay() {
        mExercise = binding.exerciseInputEditText.getText().toString();

        try {
            mWeight = Double.parseDouble(binding.weightInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "Error reading value from weight Edit text");
            mWeight = 0.0;
        }

        try {
            mReps = Integer.parseInt(binding.repInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "Error reading value from reps Edit text");
            mReps = 0;
        }
    }
}