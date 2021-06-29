package com.example.activitytracker.record;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.activitytracker.database.Repository;


public class RecordViewModel extends ViewModel {
    private final float posOfRecButton = 590f;
    private final float posOfRecButtonLand = 300f;
    private RecordingState state;
    private final Repository repository;

    public enum RecordingState {
        RECORDING,
        PAUSED,
        STOPPED
    }

    public RecordViewModel(Repository repository) {
        this.repository = repository;
        this.state = RecordingState.STOPPED;
    }

    public void setState(RecordingState state) {
        this.state = state;
    }

    public RecordingState getState() {
        return this.state;
    }

    public float getPosOfRecButton() {
        return posOfRecButton;
    }

    public float getPosOfRecButtonLand() {
        return posOfRecButtonLand;
    }

    public void startService(Context context) {
        repository.startGPSService(context);
    }

    public void stopService(Context context) {
        repository.stopGPSService(context);
    }

    public boolean getServiceStatus() {
        return repository.getServiceStatus();
    }

    public MutableLiveData<Double> getTimeElapsed() {
        return repository.getElapsedTime();
    }

    public MutableLiveData<Double> getDistTravelled() {
        return repository.getDistTravelled();
    }

    public MutableLiveData<Double> getCurrentPace() { return repository.getCurrentPace(); }

    public void setPause(boolean value) {
        repository.setPause(value);
    }

    public void setUserEffort(String effort) { repository.setUserEffort(effort); }

    public void setDate(String date) { repository.setDate(date); }

    public void insertRun() {
        repository.insertRun();
    }
}