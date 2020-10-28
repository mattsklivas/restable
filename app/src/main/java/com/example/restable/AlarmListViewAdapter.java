package com.example.restable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class AlarmListViewAdapter extends ArrayAdapter<AlarmTime> {

    private static final String TAG = "AlarmLiveViewAdapter";

    // Instance variables
    TextView wakeTimeTextView;
    TextView durationTextView;
    TextView ratingTextView;
    ImageView alarmClockImageView;

    private Context context;
    private int resource;

    // Constructor for adapter
    public AlarmListViewAdapter(@NonNull Context context, int resource,
                                @NonNull List<AlarmTime> objects) {
        super(context, resource, objects);
        Log.d(TAG, "Constructor called");

        this.context = context;
        this.resource = resource;
    }

    // Set up the ListView view
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView called");

        // Set variables
        LocalDateTime wakeTime = Objects.requireNonNull(getItem(position)).getWakeTime();
        String duration = Objects.requireNonNull(getItem(position)).getDuration();
        String rating = Objects.requireNonNull(getItem(position)).getRating();

        // Show the listView for the selected course
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        // Get views from alarm_list xml
        wakeTimeTextView = (TextView) convertView.findViewById(R.id.wakeTimeTextView);
        durationTextView = (TextView) convertView.findViewById(R.id.hoursTextView);
        ratingTextView = (TextView) convertView.findViewById(R.id.ratingTextView);
        alarmClockImageView = (ImageView) convertView.findViewById(R.id.alarmClockImageView);

        // Set the text
        wakeTimeTextView.setText(wakeTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        durationTextView.setText(duration);
        ratingTextView.setText(rating);

        return convertView;
    }
}
