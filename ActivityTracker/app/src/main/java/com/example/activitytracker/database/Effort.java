package com.example.activitytracker.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;


@Entity(tableName = "user_effort")
public class Effort {

    @PrimaryKey(autoGenerate = true)
    private long _ID;

    private String effort;

    public Effort(String effort) {
        this.effort = effort;
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public long get_ID() {
        return _ID;
    }

    public String getEffort() {
        return effort;
    }
}
