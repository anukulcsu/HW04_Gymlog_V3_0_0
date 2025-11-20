package com.example.hw04_gymlog_v300.database.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.hw04_gymlog_v300.database.AppDataBase;
import com.example.hw04_gymlog_v300.database.typeconverters.LocalDateTypeConverter;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = AppDataBase.GYM_LOG_TABLE)
@TypeConverters(LocalDateTypeConverter.class)
public class GymLog {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String exercise;
    private double weight;
    private int reps;
    private LocalDateTime date;
    private int userId;

    public GymLog(String exercise, double weight, int reps, int userId) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
        this.userId = userId;
        this.date = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getExercise() { return exercise; }
    public void setExercise(String exercise) { this.exercise = exercise; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Exercise: " + exercise + "\n" +
                "Weight: " + weight + "\n" +
                "Reps: " + reps + "\n" +
                "Date: " + date + "\n" +
                "=-=-=-=-=-=-=\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exercise, weight, reps, date, userId);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymLog gymLog = (GymLog) o;
        return id == gymLog.id && Double.compare(gymLog.weight, weight) == 0 && reps == gymLog.reps && userId == gymLog.userId && exercise.equals(gymLog.exercise) && date.equals(gymLog.date);
    }
}
