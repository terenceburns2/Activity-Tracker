package com.example.activitytracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.activitytracker.database.Effort;

import java.util.List;

@Dao
public interface EffortDao {

    @Insert
    long insert(Effort effort);

    @Delete
    void delete(Effort effort);

    @Query("SELECT * FROM user_effort")
    LiveData<List<Effort>> getEfforts();
}
