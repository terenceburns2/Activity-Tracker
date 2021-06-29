package com.example.activitytracker.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.AppContainer;
import com.example.activitytracker.R;
import com.example.activitytracker.ViewModelFactory;

import java.util.concurrent.ExecutionException;

/**
 * Fragment displaying top achievements of the user.
 */
public class ProfileFragment extends Fragment {
    private ProfileViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get repository from custom application container
        AppContainer appContainer = ((ActivityTrackerApp) getActivity().getApplication()).appContainer;
        ViewModelFactory vmFactory = new ViewModelFactory(getContext(), appContainer.repository);
        mViewModel = new ViewModelProvider(this, vmFactory).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView furthestDistView = view.findViewById(R.id.furthestDist);
        TextView fastestPaceView = view.findViewById(R.id.fastestPace);
        TextView longestDurationView = view.findViewById(R.id.longestDuration);

        LiveData<Double> furthestDistance = null;
        LiveData<Double> fastestPace = null;
        LiveData<Double> longestDuration = null;
        try {
            furthestDistance = mViewModel.getFurthestDistance();
            fastestPace = mViewModel.getFastestPace();
            longestDuration = mViewModel.getLongestDuration();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        furthestDistance.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                if (aDouble != null)
                    furthestDistView.setText(String.format("%.2f", aDouble));
            }
        });

        fastestPace.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                if (aDouble != null)
                    fastestPaceView.setText(String.format("%.2f", aDouble));
            }
        });

        longestDuration.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                if (aDouble != null) {
                    // Format time
                    double durationInMS = aDouble;
                    int seconds = (int) (durationInMS % 60);
                    int minutes = (int) ((durationInMS / 60) % 60);
                    int hours = (int) (durationInMS / 3600);
                    @SuppressLint("DefaultLocale") String durationString = String.format("%02d", hours) + ":" +
                            String.format("%02d", minutes) +
                            ":" + String.format("%02d", seconds);

                    longestDurationView.setText(durationString);
                }
            }
        });
    }
}