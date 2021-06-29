package com.example.activitytracker.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "user_runs")
public class Run {

    @PrimaryKey(autoGenerate = true)
    private long _ID;

    private long fk_effort_id;
    private double distance;
    private double duration;
    private String timestamp;
    private double avgPace;

    public Run(long fk_effort_id, double distance, double duration, String timestamp, double avgPace) {
        this.fk_effort_id = fk_effort_id;
        this.distance = distance;
        this.duration = duration;
        this.timestamp = timestamp;
        this.avgPace = avgPace;
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public long get_ID() {
        return _ID;
    }

    public long getFk_effort_id() {
        return fk_effort_id;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getAvgPace() { return avgPace; }

}
