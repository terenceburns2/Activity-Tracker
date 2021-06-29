package com.example.activitytracker.contentprovider;

import android.net.Uri;

public final class ActTrackerContract {

    public static final String AUTHORITY = "com.example.activitytracker.contentprovider.ActTrackerProvider";

    public static final Uri RUNS_URI = Uri.parse("content://"+AUTHORITY+"/user_runs");
    public static final Uri EFFORTS_URI = Uri.parse("content://"+AUTHORITY+"/user_effort");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/*");

   public static final String _ID = "_ID";

    public static final String RUN_EFFORT_ID = "fk_effort_id";
    public static final String DISTANCE = "distance";
    public static final String DURATION = "duration";
    public static final String TIMESTAMP = "timestamp";
    public static final String AVG_PACE = "avgPace";

    public static final String EFFORT = "effort";

    public static final String CONTENT_TYPE_RUNS_SINGLE = "vnd.android.cursor.item/vnd.com." +
            "example.activitytracker.user_runs";
    public static final String CONTENT_TYPE_RUNS_MULTIPLE = "vnd.android.cursor.dir/vnd.com." +
            "example.activitytracker.user_runs";
    public static final String CONTENT_TYPE_EFFORTS_SINGLE = "vnd.android.cursor.item/vnd.com." +
            "example.activitytracker.user_effort";
    public static final String CONTENT_TYPE_EFFORTS_MULTIPLE = "vnd.android.cursor.dir/vnd.com." +
            "example.activitytracker.user_effort";
}
