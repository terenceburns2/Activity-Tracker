package com.example.activitytracker.record;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.AppContainer;
import com.example.activitytracker.R;
import com.example.activitytracker.ViewModelFactory;

import java.util.Calendar;

/**
 * Handles the UI for starting an activity recording.
 */
public class RecordFragment extends Fragment implements FinishedDialog.DialogListener {

    private RecordViewModel mViewModel;

    private AnimationDrawable recordingDrawable;
    private ImageView recordingAnimationView;
    private ViewGroup recordingViewGroup;

    private TextView paceView;
    private TextView distView;

    private View stopButton;
    private ImageView recordButton;

    private int longAnimationDuration;

    private final int PERMISSION_REQUEST_ID = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get repository from custom application container
        AppContainer appContainer = ((ActivityTrackerApp) getActivity().getApplication()).appContainer;
        ViewModelFactory vmFactory = new ViewModelFactory(getContext(), appContainer.repository);
        mViewModel = new ViewModelProvider(this, vmFactory).get(RecordViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.record_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Store the clickable views and set click listeners
        recordButton = (ImageView) view.findViewById(R.id.recordButton);
        stopButton = (ImageView) view.findViewById(R.id.stopButton);
        recordButton.setOnClickListener(this::onStartRecord);
        stopButton.setOnClickListener(this::onStopRecord);

        // Store the rest of the views in the fragment
        recordingViewGroup = view.findViewById(R.id.recordingViewGroup);
        paceView = view.findViewById(R.id.paceView);
        distView = view.findViewById(R.id.distView);
        recordButton = view.findViewById(R.id.recordButton);
        recordingAnimationView = (ImageView) view.findViewById(R.id.recordingAnimationView);

        // Cache system's default medium animation time and initialise the animation for 'recording'
        longAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        recordingAnimationView.setBackgroundResource(R.drawable.running_animation);
        recordingDrawable = (AnimationDrawable) recordingAnimationView.getBackground();

        // Observe the LiveData fields. We pass in the root view to find the view components
        setObservers(view);

        // Maintain state during configuration change
        positionAndInitialiseViews();
    }


    public void hideViews() {
        recordingViewGroup.setVisibility(View.GONE);
    }


    public void setObservers(View view) {
        final Observer<Double> timeObserver = new Observer<Double>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onChanged(Double s) {
                TextView textView = view.findViewById(R.id.timeView);
                // Format time
                int seconds = mViewModel.getTimeElapsed().getValue().intValue() % 60;
                int minutes = (int) (mViewModel.getTimeElapsed().getValue().intValue() / 60) % 60;
                int hours = (int) mViewModel.getTimeElapsed().getValue().intValue() / 3600;
                // Format string and add to textView
                String sb = String.format("%02d", hours) + ":" +
                        String.format("%02d", minutes) +
                        ":" + String.format("%02d", seconds);
                textView.setText(sb);
            }
        };
        mViewModel.getTimeElapsed().observe(getViewLifecycleOwner(), timeObserver);

        final Observer<Double> distObserver = new Observer<Double>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Double s) {
                TextView textView = view.findViewById(R.id.distView);
                textView.setText(String.format("%.2f", mViewModel.getDistTravelled().getValue()));
            }
        };
        mViewModel.getDistTravelled().observe(getViewLifecycleOwner(), distObserver);

        final Observer<Double> paceObserver = new Observer<Double>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Double s) {
                TextView textView = view.findViewById(R.id.paceView);
                textView.setText(String.format("%.2f", mViewModel.getCurrentPace().getValue()));
            }
        };
        mViewModel.getCurrentPace().observe(getViewLifecycleOwner(), paceObserver);
    }


    public void positionAndInitialiseViews() {
        if (mViewModel.getState() == RecordViewModel.RecordingState.STOPPED) {
            hideViews();
        } else if (mViewModel.getState() == RecordViewModel.RecordingState.RECORDING ||
                mViewModel.getState() == RecordViewModel.RecordingState.PAUSED) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recordButton.setTranslationY(mViewModel.getPosOfRecButton());
                recordingAnimationView.setTranslationY(mViewModel.getPosOfRecButton());
            } else {
                recordButton.setTranslationY(mViewModel.getPosOfRecButtonLand());
                recordingAnimationView.setTranslationY(mViewModel.getPosOfRecButtonLand());
            }
            // Only set the animation if the state is recording
            if (mViewModel.getState() == RecordViewModel.RecordingState.RECORDING) {
                recordButton.setImageResource(R.drawable.ic_pause);
                recordingDrawable.start();
            }
        }
    }


    public void onStartRecord(View view) {
        if (ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            initialiseRecord();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("In order to track activities, the location must be accessed.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, which) ->
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_ID));
            alertDialog.show();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_ID);
        }

    }

    public void initialiseRecord() {
        if (mViewModel.getState() == RecordViewModel.RecordingState.STOPPED) {
            // Set recording state
            mViewModel.setState(RecordViewModel.RecordingState.RECORDING);
            // Set button to pause button
            recordButton.setImageResource(R.drawable.ic_pause);
            // Start animation to indicate recording
            recordingDrawable.start();
            // Set translation of record button and move to bottom of screen
            moveRecordButton();
            // Cross fade the distance, pace and time views
            crossFade();
            // Start service
            mViewModel.startService(getActivity().getApplication());
        } else if (mViewModel.getState() == RecordViewModel.RecordingState.RECORDING) {
            // Set to pause state
            mViewModel.setState(RecordViewModel.RecordingState.PAUSED);
            // Set to play icon
            recordButton.setImageResource(R.drawable.ic_run);
            // Stop animation
            recordingDrawable.stop();
            // Pause service timer
            mViewModel.setPause(true);
        } else {  // The state is 'paused'
            // Set to recording state
            mViewModel.setState(RecordViewModel.RecordingState.RECORDING);
            // Set pause icon
            recordButton.setImageResource(R.drawable.ic_pause);
            // Start animation
            recordingDrawable.start();
            // Start service timer
            mViewModel.setPause(false);
        }
    }


    public void onStopRecord(View view) {
        mViewModel.setPause(true);
        DialogFragment dialog = new FinishedDialog();
        dialog.show(getChildFragmentManager(), "FinishedFragmentDialog");
    }


    // The class implements the interface defined in FinishedDialog. This is where we define what
    // happens after the user as either confirmed or cancelled the dialog
    @Override
    public void onDialogPositiveClick(FinishedDialog dialog) {
        // Save users selected (effort)
        mViewModel.setUserEffort(dialog.selectedEffort);
        // Store date/time
        mViewModel.setDate(String.valueOf(Calendar.getInstance().getTime()));
        // Reverse states:
        mViewModel.setState(RecordViewModel.RecordingState.STOPPED);
        //  Remove views but save data in db
        hideViews();
        // Move record button back to center screen
        moveRecordButton();
        //  Set icon to record icon
        recordButton.setImageResource(R.drawable.ic_run);
        //  Stop animation
        recordingDrawable.stop();
        //  Stop service
        mViewModel.stopService(getActivity().getApplication());
        // Reset view text
        distView.setText(R.string.initial_dist);
        paceView.setText(R.string.initial_pace);
        // Insert run into db
        mViewModel.insertRun();
    }


    @Override
    public void onDialogNegativeClick(FinishedDialog dialog) {
        mViewModel.setPause(false);
    }


    public void moveRecordButton() {
        int orientation = getResources().getConfiguration().orientation;

        if (mViewModel.getState() == RecordViewModel.RecordingState.RECORDING) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recordButton.animate().setDuration(700).translationY(mViewModel.getPosOfRecButtonLand());
                recordingAnimationView.animate().setDuration(700).translationY(mViewModel.getPosOfRecButtonLand());
            } else {
                recordButton.animate().setDuration(700).translationY(mViewModel.getPosOfRecButton());
                recordingAnimationView.animate().setDuration(700).translationY(mViewModel.getPosOfRecButton());
            }
        } else if (mViewModel.getState() == RecordViewModel.RecordingState.STOPPED) {
            recordButton.animate().setDuration(700).translationY(0);
            recordingAnimationView.animate().setDuration(700).translationY(0);
        }
    }


    private void crossFade() {
        recordingViewGroup.setAlpha(0f);
        recordingViewGroup.setVisibility(View.VISIBLE);

        recordingViewGroup.animate()
                .alpha(1f)
                .setDuration(longAnimationDuration)
                .setListener(null);
    }

    // Only when the user closes the app stop the service and save the current data
    @Override
    public void onDestroy() {
        if (!getActivity().isChangingConfigurations()) {
            if (mViewModel.getServiceStatus()) {
                mViewModel.setDate(String.valueOf(Calendar.getInstance().getTime()));
                mViewModel.stopService(getActivity().getApplication());
                mViewModel.insertRun();
            }
        }
        super.onDestroy();
    }
}