package com.example.hw04_gymlog_v300.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.hw04_gymlog_v300.database.AppDataBase;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = AppDatabase.GYM_LOG_TABLE)
public class GymLog {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String exercise;
    private double weight;
    private int reps;
    private LocalDateTime date;
    private int userId;

    public GymLog(String exercise, int reps, double weight, int userId) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
        this.userId = userId;
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return exercise + "\n" +
                "Weight: " + weight + "\n" +
                "Reps: " + reps + "\n" +
                "Date: " + date.toString() + "\n" +
                "====================\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymLog gymLog = (GymLog) o;
        return Double.compare(weight, gymLog.weight) == 0 && reps == gymLog.reps && userId == gymLog.userId && Objects.equals(id, gymLog.id) && Objects.equals(exercise, gymLog.exercise) && Objects.equals(date, gymLog.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exercise, weight, reps, date, userId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}