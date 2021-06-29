package com.example.activitytracker.feed;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.AppContainer;
import com.example.activitytracker.R;
import com.example.activitytracker.ViewModelFactory;
import com.example.activitytracker.database.RunPaceEffort;

import java.util.concurrent.ExecutionException;

public class BreakDownActivity extends AppCompatActivity {

    BreakDownViewModel mViewModel;
    LiveData<RunPaceEffort> run_entry = null;
    long run_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_down);

        // Get repository from custom application container
        AppContainer appContainer = ((ActivityTrackerApp) this.getApplication()).appContainer;
        ViewModelFactory vmFactory = new ViewModelFactory(this, appContainer.repository);
        mViewModel = new ViewModelProvider(this, vmFactory).get(BreakDownViewModel.class);

        run_id = getIntent().getLongExtra("run_id", 0);
        try {
            run_entry = mViewModel.getRunWithID(run_id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TextView distanceView = findViewById(R.id.breakdown_distance);
        TextView durationView = findViewById(R.id.breakdown_duration);
        TextView ratingView = findViewById(R.id.breakdown_rating);
        TextView paceView = findViewById(R.id.breakdown_pace);

        run_entry.observe(this, new Observer<RunPaceEffort>() {
            @Override
            public void onChanged(RunPaceEffort runAndAllPaces) {
                // Get duration and format
                double durationInMS = runAndAllPaces.run.getDuration();
                // Format time
                int seconds = (int) (durationInMS % 60);
                int minutes = (int) (durationInMS / 60) % 60;
                int hours = (int) durationInMS / 3600;
                @SuppressLint("DefaultLocale") String durationString = String.format("%02d", hours) + ":" +
                        String.format("%02d", minutes) +
                        ":" + String.format("%02d", seconds);


                distanceView.setText(String.format("%.2f", runAndAllPaces.run.getDistance()));
                durationView.setText(durationString);
                ratingView.setText(runAndAllPaces.effort.getEffort());
                paceView.setText(String.format("%.2f", runAndAllPaces.run.getAvgPace()));
            }
        });
    }

    public void onDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.deleteAll(run_entry.getValue());
                        run_entry.removeObservers(BreakDownActivity.this);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                }).create().show();
    }

}