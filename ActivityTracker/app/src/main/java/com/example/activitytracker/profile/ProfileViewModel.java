package com.example.activitytracker.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.activitytracker.database.Repository;

import java.util.concurrent.ExecutionException;

public class ProfileViewModel extends ViewModel {

    private Repository repository;

    public ProfileViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<Double> getFurthestDistance() throws ExecutionException, InterruptedException {
        return repository.getFurthestDistance();
    }

    public LiveData<Double> getFastestPace() throws ExecutionException, InterruptedException {
        return repository.getFastestPace();
    }

    public LiveData<Double> getLongestDuration() throws ExecutionException, InterruptedException {
        return repository.getLongestDuration();
    }
}