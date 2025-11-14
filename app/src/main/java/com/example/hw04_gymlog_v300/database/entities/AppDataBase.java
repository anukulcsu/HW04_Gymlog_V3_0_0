package com.example.hw04_gymlog_v300.database.entities;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hw04_gymlog_v300.database.GymLogDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {GymLog.class, User.class}, version = 4, exportSchema = false)
@TypeConverters({LocalDateTypeConverter.class})
public abstract class AppDataBase extends RoomDatabase {

    public static final String GYM_LOG_TABLE = "gymLogTable";
    public static final String USER_TABLE = "userTable";
    public static final String DATABASE_NAME = "gym_log_database";

    public abstract GymLogDAO gymLogDao();
    public abstract UserDAO userDAO();

    private static volatile AppDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                Log.i(MainActivity.TAG, "DATABASE CREATED");
                UserDAO dao = INSTANCE.userDAO();
                dao.deleteAll();

                User admin = new User("admin1", "admin1");
                admin.setAdmin(true);

                User testUser = new User("testuser1", "testuser1");

                dao.insert(admin, testUser);
            });
        }
    };
}
