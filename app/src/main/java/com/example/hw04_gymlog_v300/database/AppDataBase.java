package com.example.hw04_gymlog_v300.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.hw04_gymlog_v300.MainActivity;
import com.example.hw04_gymlog_v300.database.entities.GymLog;
import com.example.hw04_gymlog_v300.database.entities.User;
import com.example.hw04_gymlog_v300.database.typeconverters.LocalDateTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {GymLog.class, User.class}, version = 1, exportSchema = false)
@TypeConverters(LocalDateTypeConverter.class)
public abstract class AppDataBase extends RoomDatabase {
    public static final String GYM_LOG_TABLE = "gym_log_table";
    public static final String USER_TABLE = "user_table";
    private static final String DATABASE_NAME = "GymLog.db";
    private static volatile AppDataBase instance;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract GymLogDao gymLogDao();
    public abstract UserDao userDao();

    public static AppDataBase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDataBase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                Log.i(MainActivity.TAG, "DATABASE CREATED");
                UserDao dao = instance.userDao();
                dao.deleteAll();
                User admin = new User("admin1", "admin1");
                admin.setAdmin(true);
                dao.insert(admin);
                User testUser = new User("testuser1", "testuser1");
                dao.insert(testUser);
            });
        }
    };
}
