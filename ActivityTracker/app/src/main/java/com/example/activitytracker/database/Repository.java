package com.example.activitytracker.database;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.database.Effort;
import com.example.activitytracker.database.EffortDao;
import com.example.activitytracker.database.Run;
import com.example.activitytracker.database.RunDao;
import com.example.activitytracker.database.RunPaceEffort;
import com.example.activitytracker.database.TrackerDatabase;
import com.example.activitytracker.service.GPSService;
import com.example.activitytracker.service.ICallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static android.content.ContentValues.TAG;

public class Repository {
    private GPSService gpsService;
    private boolean bound;

    // LiveData so we can update the view automatically on change
    private final MutableLiveData<Double> timeElapsed = new MutableLiveData<>();
    private final MutableLiveData<Double> distTravelled = new MutableLiveData<>();
    private final MutableLiveData<Double> currentPace = new MutableLiveData<>();
    private ArrayList<Double> pacePerMile = new ArrayList<>();
    private String userEffort;
    private String date;

    // DAOs
    private RunDao runDao;
    private EffortDao effortDao;

    // Caches for db queries
    private LiveData<List<RunPaceEffort>> allRuns;

    // Reference to pool of threads
    private ExecutorService executorService;


    // Called when the main activity is created
    public void fetchDB(Context context) {
        // Get pool of threads
        executorService = ((ActivityTrackerApp) context.getApplicationContext())
                .appContainer
                .executorService;

        TrackerDatabase db = TrackerDatabase.getDatabase(context);

        runDao = db.runDao();
        effortDao = db.effortDao();

        allRuns = runDao.getRuns();

        // Pre-initialize
        timeElapsed.setValue(0.00);
        distTravelled.setValue(0.00);
        currentPace.setValue(0.00);
    }

    public LiveData<List<RunPaceEffort>> getAllRuns() {
        return allRuns;
    }

    public LiveData<RunPaceEffort> getRunWithID(long run_id) throws ExecutionException, InterruptedException {
        return executorService.submit(new Callable<LiveData<RunPaceEffort>>() {
            @Override
            public LiveData<RunPaceEffort> call() throws Exception {
                return runDao.getRunWithID(run_id);
            }
        }).get();
    }

    public LiveData<Double> getFurthestDistance() throws ExecutionException, InterruptedException {
        return executorService.submit(new Callable<LiveData<Double>>() {
            @Override
            public LiveData<Double> call() throws Exception {
                return runDao.getFurthestDistance();
            }
        }).get();
    }

    public LiveData<Double> getFastestPace() throws ExecutionException, InterruptedException {
        return executorService.submit(new Callable<LiveData<Double>>() {
            @Override
            public LiveData<Double> call() throws Exception {
                return runDao.getFastestPace();
            }
        }).get();
    }

    public LiveData<Double> getLongestDuration() throws ExecutionException, InterruptedException {
        return executorService.submit(new Callable<LiveData<Double>>() {
            @Override
            public LiveData<Double> call() throws Exception {
                return runDao.getLongestDuration();
            }
        }).get();
    }

    public void insertRun() {
        executorService.execute(() -> {
            Effort effort = new Effort(userEffort);
            long effort_id = effortDao.insert(effort);
            // If the user doesn't reach a mile distance, then set to current calculated pace
            if (pacePerMile.size() == 0)
                pacePerMile.add(currentPace.getValue());
            // Store average pace
            double avgPace = 0.0;
            for (int i = 0; i < pacePerMile.size(); i++)
                avgPace += pacePerMile.get(i);
            avgPace = avgPace / pacePerMile.size();
            @SuppressLint("DefaultLocale")
            Run run = new Run(effort_id, distTravelled.getValue(), timeElapsed.getValue(), date, avgPace);
            runDao.insert(run);
        });

        // Erase pace array for next run (the other variables get replaced when another run starts)
        pacePerMile.clear();
    }

    public void deleteAll(RunPaceEffort runPaceEffort) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                runDao.delete(runPaceEffort.run);
                effortDao.delete(runPaceEffort.effort);
            }
        });
    }


    public void startGPSService(Context context) {
        Intent intent = new Intent(context, GPSService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection,
                Context.BIND_AUTO_CREATE);
        bound = true;
    }


    public void stopGPSService(Context context) {
        if (bound) {
            context.unbindService(serviceConnection);
            bound = false;
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSService.GPSBinder binder = (GPSService.GPSBinder) service;
            gpsService = binder.getService();
            binder.setCallBack(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            gpsService = null;
        }
    };


    // Callback to that is passed to our service so to set the progress of the track
    ICallBack callback = new ICallBack() {
        @Override
        public void updateTime(double elapsedTime) {
            if (timeElapsed.hasActiveObservers())
                timeElapsed.postValue(elapsedTime);
        }

        @Override
        public void updateDistance(double distanceTravelled) {
            if (distTravelled.hasActiveObservers())
                distTravelled.postValue(distanceTravelled);
        }

        @Override
        public void updatePace(double pace) {
            if (currentPace.hasActiveObservers())
                currentPace.postValue(pace);
        }

        @Override
        public void storeMilePace(double pace) {
            pacePerMile.add(pace);
        }
    };


    public boolean getServiceStatus() { return bound; }

    public MutableLiveData<Double> getElapsedTime() {
        return timeElapsed;
    }

    public MutableLiveData<Double> getDistTravelled() { return distTravelled; }

    public MutableLiveData<Double> getCurrentPace() { return currentPace; }

    public void setUserEffort(String effort) { userEffort = effort; }

    public void setDate(String date) { this.date = date; }

    public void setPause(boolean value) {
        if (bound) {
            gpsService.timer.setPause(value);
            gpsService.locationListener.setPause(value);
        }
    }
}
