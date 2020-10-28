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

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlarmListViewAdapter extends ArrayAdapter<AlarmTime> {

    private Context context;
    private int resource;

    private static final String TAG = "AlarmLiveViewAdapter";

    TextView wakeTimeTextView;
    TextView durationTextView;
    TextView ratingTextView;
    TextView alarmSetTextView;
    ImageView alarmClockImageView;

    public AlarmListViewAdapter(@NonNull Context context, int resource, @NonNull List<AlarmTime> objects) {
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
        int hour = Objects.requireNonNull(getItem(position)).getWakeTime().getHour();
        int minute = Objects.requireNonNull(getItem(position)).getWakeTime().getMinute();
        String duration = Objects.requireNonNull(getItem(position)).getDuration();
        String rating = Objects.requireNonNull(getItem(position)).getRating();

        // Show the listView for the selected course
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        // Get views from alarm_list xml
        wakeTimeTextView = (TextView) convertView.findViewById(R.id.wakeTimeTextView);
        durationTextView = (TextView) convertView.findViewById(R.id.hoursTextView);
        ratingTextView = (TextView) convertView.findViewById(R.id.ratingTextView);
        alarmSetTextView = (TextView) convertView.findViewById(R.id.setTextView);
        alarmClockImageView = (ImageView) convertView.findViewById(R.id.alarmClockImageView);

        // Set the text
        wakeTimeTextView.setText(String.format(Locale.CANADA, "%d:%s", hour, String.format(Locale.CANADA, "%02d", minute)));
        durationTextView.setText(duration);
        ratingTextView.setText(rating);

        return convertView;
    }
}
