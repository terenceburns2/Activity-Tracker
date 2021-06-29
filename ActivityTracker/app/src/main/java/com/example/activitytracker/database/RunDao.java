package com.example.activitytracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RunDao {

    @Insert
    long insert(Run run);

    @Delete
    void delete(Run run);

    @Transaction
    @Query("SELECT * FROM user_runs")
    LiveData<List<RunPaceEffort>> getRuns();

    @Transaction
    @Query("SELECT * FROM user_runs WHERE _ID = :run_id")
    LiveData<RunPaceEffort> getRunWithID(long run_id);

    @Query("SELECT MAX(distance) FROM user_runs")
    LiveData<Double> getFurthestDistance();

    @Query("SELECT MIN(avgPace) FROM user_runs WHERE avgPace != 0")
    LiveData<Double> getFastestPace();

    @Query("SELECT MAX(duration) FROM user_runs")
    LiveData<Double> getLongestDuration();

}
