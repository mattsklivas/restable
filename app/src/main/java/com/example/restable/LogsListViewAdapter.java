package com.example.restable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static java.lang.String.*;

public class LogsListViewAdapter extends ArrayAdapter<SleepData> {

    private static final String TAG = "LogsListViewAdapter";

    // Instance variables

    protected TextView startDateTextView;
    protected TextView durationTextView;
    protected TextView startTimeTextView;
    protected TextView endTimeTextView;
    protected TextView sleepScoreTextView;

    private Context context;
    private int resource;

    // Constructor
    public LogsListViewAdapter(@NonNull Context context, int resource, @NonNull List<SleepData> objects) {
        super(context, resource, objects);

        Log.d(TAG, "Constructor called");

        this.context = context;
        this.resource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView called");

        // Set variables
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(getItem(position).getStartTime()), ZoneId.systemDefault());
        LocalDateTime stopTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(getItem(position).getEndTime()), ZoneId.systemDefault());
        Log.i(TAG, "startTime" + startTime.toString());
        Log.i(TAG, "stopTime" + stopTime.toString());
        Scores scores = new Scores(getItem(position));

        Duration duration = Duration.between(startTime, stopTime);
        Log.i(TAG, "duration" + String.format(Locale.getDefault(), "%d:%tM", duration.toHours(), duration.toMinutes() - duration.toHours()*60));

        // Show the listView for the selected Log
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        // Get views
        startDateTextView = convertView.findViewById(R.id.startDateTextViewLogs);
        durationTextView = convertView.findViewById(R.id.hoursTextViewLogs);
        startTimeTextView = convertView.findViewById(R.id.startTimeTextViewLogs);
        endTimeTextView = convertView.findViewById(R.id.endTimeTextViewLogs);
        sleepScoreTextView = convertView.findViewById(R.id.sleepScoreTextViewLogs);

        // Set views
        startTimeTextView.setText(startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        durationTextView.setText(String.format(Locale.getDefault(), "%dh %dm", duration.toHours(), duration.toMinutes() - duration.toHours()*60));
        startDateTextView.setText(format(Locale.getDefault(), "%s %d", startTime.getMonth().toString(), startTime.getDayOfMonth()));
        endTimeTextView.setText(stopTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        sleepScoreTextView.setText(String.format("Total Score: %1$s%2$s", scores.getScore(), "/10"));

        return convertView;
    }
}
