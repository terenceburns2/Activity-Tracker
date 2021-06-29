package com.example.activitytracker.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activitytracker.ActivityTrackerApp;
import com.example.activitytracker.AppContainer;
import com.example.activitytracker.R;
import com.example.activitytracker.ViewModelFactory;
import com.example.activitytracker.database.RunPaceEffort;

import java.util.List;

public class FeedFragment extends Fragment {
    private FeedViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get repository from custom application container
        AppContainer appContainer = ((ActivityTrackerApp) getActivity().getApplication()).appContainer;
        ViewModelFactory vmFactory = new ViewModelFactory(getContext(), appContainer.repository);
        mViewModel = new ViewModelProvider(this, vmFactory).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up adapter and populate views
        LiveData<List<RunPaceEffort>> runsAndPaces = mViewModel.getAllRuns();

        runsAndPaces.observe(getViewLifecycleOwner(), new Observer<List<RunPaceEffort>>() {
            @Override
            public void onChanged(List<RunPaceEffort> runsAndPaces) {
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), runsAndPaces);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(run_id -> {
                    Intent intent = new Intent(getContext(), BreakDownActivity.class);
                    intent.putExtra("run_id", run_id);
                    startActivity(intent);
                });
            }
        });

    }
}