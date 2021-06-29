package com.example.activitytracker;

import com.example.activitytracker.database.Repository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Container of dependencies used throughout the app
public class AppContainer {

    // Repository abstraction
    public Repository repository = new Repository();

    // Thread pool for background tasks
    public ExecutorService executorService = Executors.newFixedThreadPool(4);
}
