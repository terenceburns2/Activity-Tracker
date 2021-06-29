package com.example.activitytracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Run.class, Effort.class}, version = 17, exportSchema = false)
public abstract class TrackerDatabase extends RoomDatabase {

    public abstract RunDao runDao();
    public abstract EffortDao effortDao();

    // Singleton pattern
    private static volatile TrackerDatabase INSTANCE;

    public static TrackerDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (TrackerDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TrackerDatabase.class,
                        "activity_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }

}
