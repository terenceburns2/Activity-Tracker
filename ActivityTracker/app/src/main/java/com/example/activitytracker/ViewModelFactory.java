package com.example.activitytracker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.activitytracker.database.Repository;
import com.example.activitytracker.feed.BreakDownViewModel;
import com.example.activitytracker.feed.FeedViewModel;
import com.example.activitytracker.profile.ProfileViewModel;
import com.example.activitytracker.record.RecordViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Repository repository;


    public ViewModelFactory(Context context, Repository repository) {
        this.repository = repository;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        Log.d("Debug", "Creating viewmodel");

        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(FeedViewModel.class)) {
            return (T) new FeedViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(BreakDownViewModel.class)) {
            return (T) new BreakDownViewModel(repository);
        }
        else {
            throw new IllegalArgumentException("Cannot assign ViewModel.");
        }
    }
}
