package com.example.activitytracker.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.activitytracker.database.Repository;
import com.example.activitytracker.database.RunPaceEffort;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BreakDownViewModel extends ViewModel {

    private final Repository repository;
    private LiveData<List<RunPaceEffort>> allRuns;


    public BreakDownViewModel(Repository repository) {
        this.repository = repository;
        this.allRuns = repository.getAllRuns();
    }

    public LiveData<RunPaceEffort> getRunWithID(long run_id) throws ExecutionException, InterruptedException {
        return repository.getRunWithID(run_id);
    }

    public void deleteAll(RunPaceEffort runPaceEffort) {
        repository.deleteAll(runPaceEffort);
    }
}
