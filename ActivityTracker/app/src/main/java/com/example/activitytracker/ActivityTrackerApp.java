package com.example.activitytracker;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Custom application class so that dependencies can be used across entire application
public class ActivityTrackerApp extends Application {

    // Holds the dependencies
    public AppContainer appContainer = new AppContainer();

}