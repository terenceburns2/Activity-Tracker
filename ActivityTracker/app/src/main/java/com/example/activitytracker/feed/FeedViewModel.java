package com.example.activitytracker.feed;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.activitytracker.database.Repository;
import com.example.activitytracker.database.RunPaceEffort;

import java.util.List;

public class FeedViewModel extends ViewModel {
    private Repository repository;

    // Caches
    private LiveData<List<RunPaceEffort>> allRuns;

    public FeedViewModel(Repository repository) {
        this.repository = repository;
        allRuns = repository.getAllRuns();
    }

    public LiveData<List<RunPaceEffort>> getAllRuns() {
        return allRuns;
    }
}