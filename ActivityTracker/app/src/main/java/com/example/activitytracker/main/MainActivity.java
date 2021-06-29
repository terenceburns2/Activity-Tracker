package com.example.activitytracker.main;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.AppContainer;
import com.example.activitytracker.R;
import com.example.activitytracker.feed.FeedFragment;
import com.example.activitytracker.profile.ProfileFragment;
import com.example.activitytracker.record.RecordFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// TODO: Refactor to make extendable and reusable...

/**
 * This activity handles the starting of the activity tracking.
 */
public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise the database and its DAOs within the repository.
        // It is called here, so we can pass the context.
        AppContainer appContainer = ((ActivityTrackerApp) this.getApplication()).appContainer;
        appContainer.repository.fetchDB(this);

        // Initialize bottom navigation
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.record_page);

        // Add all fragments if not already done so
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, ProfileFragment.class, null, "profileFrag")
                    .add(R.id.fragment_container, FeedFragment.class, null, "feedFrag")
                    .add(R.id.fragment_container, RecordFragment.class, null, "recordFrag")
                    .commit();
        }

        // Handle fragment changes
        // Show selected fragment and hide every other fragment not selected
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.profile_page:
                    getSupportFragmentManager().beginTransaction()
                            .show(getSupportFragmentManager().findFragmentByTag("profileFrag"))
                            .commit();

                    getSupportFragmentManager().beginTransaction()
                            .hide(getSupportFragmentManager().findFragmentByTag("feedFrag"))
                            .hide(getSupportFragmentManager().findFragmentByTag("recordFrag"))
                            .commit();
                    break;
                case R.id.feed_page:
                    getSupportFragmentManager().beginTransaction()
                            .show(getSupportFragmentManager().findFragmentByTag("feedFrag"))
                            .commit();

                    getSupportFragmentManager().beginTransaction()
                            .hide(getSupportFragmentManager().findFragmentByTag("profileFrag"))
                            .hide(getSupportFragmentManager().findFragmentByTag("recordFrag"))
                            .commit();
                    break;
                case R.id.record_page:
                    getSupportFragmentManager().beginTransaction()
                            .show(getSupportFragmentManager().findFragmentByTag("recordFrag"))
                            .commit();

                    getSupportFragmentManager().beginTransaction()
                            .hide(getSupportFragmentManager().findFragmentByTag("feedFrag"))
                            .hide(getSupportFragmentManager().findFragmentByTag("profileFrag"))
                            .commit();
                    break;
            }
            return true;
        });

    }

}