package com.example.activitytracker.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.activitytracker.database.TrackerDatabase;

public class ActTrackerProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ActTrackerContract.AUTHORITY, "user_runs", 1);
        uriMatcher.addURI(ActTrackerContract.AUTHORITY, "user_runs/#", 2);
        uriMatcher.addURI(ActTrackerContract.AUTHORITY, "user_effort", 3);
        uriMatcher.addURI(ActTrackerContract.AUTHORITY, "user_effort/#", 4);
        uriMatcher.addURI(ActTrackerContract.AUTHORITY, "/*", 5);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if (uri == null)
            throw new IllegalArgumentException("URI cannot be null");

        TrackerDatabase db = TrackerDatabase.getDatabase(getContext());
        String query;

        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));

        switch (uriMatcher.match(uri)) {
            case 2:
                selection = "_ID = " + uri.getLastPathSegment();
            case 1:
                query = SQLiteQueryBuilder.buildQueryString(false, "user_runs", projection,
                        selection, null, null, sortOrder, null);
                return db.query(query, selectionArgs);
            case 4:
                selection = "_ID = " + uri.getLastPathSegment();
            case 3:
                query = SQLiteQueryBuilder.buildQueryString(false, "user_effort",
                        projection, selection, null, null, sortOrder, null);
                return db.query(query, selectionArgs);
            case 5:
                query = "SELECT user_runs._ID, user_effort.effort, user_runs.distance, " +
                        "user_runs.duration, user_runs.timestamp, user_runs.avgPace FROM user_runs " +
                        "INNER JOIN user_effort ON user_runs.fk_effort_id = user_effort._ID";
                return db.query(query, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        String contentType;

        if (uri.getLastPathSegment() == null) {
            if (uri.getPathSegments().get(1).equals("user_runs")) {
                contentType = ActTrackerContract.CONTENT_TYPE_RUNS_MULTIPLE;
            } else {
                contentType = ActTrackerContract.CONTENT_TYPE_EFFORTS_MULTIPLE;
            }
        } else {
            if (uri.getPathSegments().get(1).equals("user_effort")) {
                contentType = ActTrackerContract.CONTENT_TYPE_RUNS_SINGLE;
            } else {
                contentType = ActTrackerContract.CONTENT_TYPE_EFFORTS_SINGLE;
            }
        }

        return contentType;
    }


    // No reason for other applications to edit database entries or add new entries outside the
    // scope of the app
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
