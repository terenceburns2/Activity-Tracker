package com.example.activitytracker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.main.MainActivity;
import com.example.activitytracker.R;

import java.util.concurrent.ExecutorService;

public class GPSService extends Service {
    // Interface given to clients
    private final IBinder binder = new GPSBinder();

    // System service to get location updates
    private LocationManager locationManager;
    public GPSLocationListener locationListener;

    public Timer timer;

    private ICallBack callback;

    // Notification
    private final String CHANNEL_ID = "100";
    private final int NOTIFICATION_ID = 1;


    // Worker thread
    public class Timer extends Thread {
        public boolean running = true;
        public boolean paused = false;

        long startTime;
        long endTime;
        long pausedStartTime;
        long elapsedTime;
        long pausedTime;
        long totalTime;
        long accumulatedPausedTime = 0;

        public Timer() {
            this.start();
            startTime = SystemClock.elapsedRealtime();
        }

        public void run() {
            while(this.running) {
                // Race condition?
                try {Thread.sleep(10);} catch(Exception e) {return;}
                if (callback != null) {
                    endTime = SystemClock.elapsedRealtime();
                    if (paused) {
                        pausedTime = endTime - pausedStartTime;
                    } else {
                        elapsedTime = endTime - startTime;
                        totalTime = (elapsedTime - accumulatedPausedTime) / 1000;
                        callback.updateTime(totalTime);
                    }
                }
            }
        }

        public void setPause(boolean value) {
            if (value) {
                // Point in time the timer is paused
                pausedStartTime = SystemClock.elapsedRealtime();
                paused = true;
            } else {
                accumulatedPausedTime += pausedTime;
                paused = false;
            }
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Create notification
        createNotificationChannel();

        // Set notification tap action
        Intent recordFragIntent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                recordFragIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_run_circle)
                .setContentTitle("Activity Tracker")
                .setContentText("Tracking in progress...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public class GPSBinder extends Binder {
        public GPSService getService() {
            return GPSService.this;
        }

        public void setCallBack(ICallBack callBack) {
            callback = callBack;
        }
    }


    public class GPSLocationListener implements LocationListener {
        boolean paused = false;
        boolean initialStart = true;

        double METERS_TO_MILES = 0.000621371;
        double SECSPERMET_TO_MINSPERMIL = 26.82224;

        double pace = 0;
        double distanceTravelledMeters;

        Location previousLocationPoint;
        double previousRoundedDist = 0;

        ExecutorService executorService = ((ActivityTrackerApp) getApplicationContext())
                .appContainer
                .executorService;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (initialStart) {
                        previousLocationPoint = location;
                        initialStart = false;
                    }

                    if (!paused) {
                        // Calculate distance from previous location to new
                        distanceTravelledMeters += previousLocationPoint.distanceTo(location);
                        // Convert to miles
                        double distanceTravelledMiles = distanceTravelledMeters * METERS_TO_MILES;
                        // Update distance
                        callback.updateDistance(distanceTravelledMiles);

                        // Retrieve pace and convert into min/mile. Stop undefined behaviour by division by 0
                        if (distanceTravelledMeters != 0)
                            pace = (timer.totalTime / distanceTravelledMeters) * SECSPERMET_TO_MINSPERMIL;
                        // Get decimal
                        double decimal = ((pace - Math.floor(pace)) * 60) / 100;
                        // Form correct pace
                        double newPace = Math.floor(pace) + decimal;
                        callback.updatePace(newPace);
                        // Every one mile, store into repository array list
                        // Checks to see if it has increased by a mile
                        if (Math.floor(distanceTravelledMiles) != previousRoundedDist)
                            callback.storeMilePace(newPace);
                        previousRoundedDist = Math.floor(distanceTravelledMiles);
                    }
                    // Store current location for the next iteration
                    previousLocationPoint = location;
                }
            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }

        public void setPause(Boolean value) {
            paused = value;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new GPSLocationListener();

        // Start listening and feed in location updates to listener
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
        } catch(SecurityException e) {
            System.out.println("Error: " + e);
        }
        // Start timer for user's activity
        timer = new Timer();

    }

    @Override
    public void onDestroy() {
        // Kill thread and clean up
        timer.running = false;
        timer = null;
        // Stop notifying listener
        locationManager.removeUpdates(locationListener);
        locationListener = null;
        // Clear callback
        callback = null;
        super.onDestroy();
    }
}
