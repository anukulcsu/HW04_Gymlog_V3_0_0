package com.example.hw04_gymlog_v300;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hw04_gymlog_v300.database.GymLogRepository;
import com.example.hw04_gymlog_v300.database.entities.GymLog;
import com.example.hw04_gymlog_v300.database.entities.User;
import com.example.hw04_gymlog_v300.databinding.ActivityMainBinding;
import com.example.hw04_gymlog_v300.database.viewmodels.GymLogAdapter;
import com.example.hw04_gymlog_v300.database.viewmodels.GymLogViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "DAC_GYMLOG";
    private static final String MAIN_ACTIVITY_USER_ID = "com.example.hw04_gymlog_v300.MAIN_ACTIVITY_USER_ID";
    static final int LOGGED_OUT = -1;

    private ActivityMainBinding binding;
    private GymLogRepository repository;
    private GymLogViewModel gymLogViewModel;
    private int loggedInUserId = -1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gymLogViewModel = new ViewModelProvider(this).get(GymLogViewModel.class);

        RecyclerView recyclerView = binding.logDisplayRecyclerView;
        final GymLogAdapter adapter = new GymLogAdapter(new GymLogAdapter.GymLogDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = GymLogRepository.getRepository(getApplication());
        loginUser();

        if (loggedInUserId == LOGGED_OUT) {
            Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(intent);
        } else {
            gymLogViewModel.getAllLogsByUserId(loggedInUserId).observe(this, gymLogs -> {
                adapter.submitList(gymLogs);
            });
        }

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay();
            }
        });
    }

    private void loginUser() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        loggedInUserId = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);

        if (loggedInUserId == LOGGED_OUT) {
            return;
        }

        repository.getUserByUserId(loggedInUserId).observe(this, user -> {
            if (user != null) {
                this.user = user;
                invalidateOptionsMenu();
            }
        });
    }

    private void getInformationFromDisplay() {
        String exercise = binding.exerciseInputEditText.getText().toString();
        String weightString = binding.weightInputEditText.getText().toString();
        String repsString = binding.repInputEditText.getText().toString();

        if (exercise.isEmpty()) {
            return;
        }

        double weight = 0.0;
        try {
            weight = Double.parseDouble(weightString);
        } catch (NumberFormatException e) {}

        int reps = 0;
        try {
            reps = Integer.parseInt(repsString);
        } catch (NumberFormatException e) {}

        GymLog log = new GymLog(exercise, weight, reps, loggedInUserId);
        gymLogViewModel.insert(log);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logout_menu_item);
        item.setVisible(true);
        if (user == null) {
            return false;
        }
        item.setTitle(user.getUsername());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                showLogoutDialog();
                return true;
            }
        });
        // FIXED: Must call super.onPrepareOptionsMenu, NOT openOptionsMenu
        return super.onPrepareOptionsMenu(menu);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setMessage("Log out?");
        alertBuilder.setPositiveButton("Log Out", (dialog, which) -> logout());
        alertBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertBuilder.create().show();
    }

    private void logout() {
        updateSharedPreference(LOGGED_OUT);
        loggedInUserId = LOGGED_OUT;
        user = null;
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
    }

    private void updateSharedPreference(int userId) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.preference_user_id_key), userId);
        editor.apply();
    }

    public static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }
}