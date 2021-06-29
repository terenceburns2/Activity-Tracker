package com.example.activitytracker.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitytracker.R;
import com.example.activitytracker.database.RunPaceEffort;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<RunPaceEffort> runs;
    private ItemClickListener clickListener;

    // When instantiated, we pass in the cursor to get the data from.
    // LayoutInflator is what will allow us to inflate views for us
    RecyclerViewAdapter(Context context, List<RunPaceEffort> runs) {
        this.inflater = LayoutInflater.from(context);
        this.runs = runs;
    }

    // Inflate layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RunPaceEffort runAndPaces = runs.get(position);

        // Get id
        long run_id = runAndPaces.run.get_ID();
        holder.setRun_id(run_id);

        // Get all data for feed and format
        double distance = runAndPaces.run.getDistance();
        double durationInMS = runAndPaces.run.getDuration();
        // Format time
        int seconds = (int) (durationInMS % 60);
        int minutes = (int) (durationInMS / 60) % 60;
        int hours = (int) durationInMS / 3600;
        @SuppressLint("DefaultLocale") String durationString = String.format("%02d", hours) + ":" +
                String.format("%02d", minutes) +
                ":" + String.format("%02d", seconds);

        // Get date
        String date = runAndPaces.run.getTimestamp();

        // Get average pace
        double avgPace = runAndPaces.run.getAvgPace();

        holder.avgPaceView.setText(String.format("%.2f", avgPace) + " (min/mile)");
        holder.dateView.setText(date);
        holder.distanceView.setText(String.format("%.2f", distance) + " (miles)");
        holder.durationView.setText(durationString);
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        long run_id;

        TextView dateView;
        TextView distanceView;
        TextView avgPaceView;
        TextView durationView;

        ViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dateView);
            distanceView = itemView.findViewById(R.id.distanceView);
            avgPaceView = itemView.findViewById(R.id.avgPaceView);
            durationView = itemView.findViewById(R.id.durationView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClick(run_id);
            }
        }

        public void setRun_id(long run_id) {
            this.run_id = run_id;
        }
    }

    void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ItemClickListener {
        void itemClick(long run_id);
    }
}
