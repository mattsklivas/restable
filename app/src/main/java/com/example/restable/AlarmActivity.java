package com.example.restable;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlarmActivity  extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";

    //Instance variables
    protected Button recButton;
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //Setup activity views
        recButton = findViewById(R.id.buttonRec);

        //Setup recButton
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRecActivity();
            }
        });

        //Setup list view of suggested sleep times
        configureListView();
    }

    //Go to RecActivity
    protected void goToRecActivity() {
        Intent intent = new Intent(this, RecActivity.class);
        startActivity(intent);
    }

    protected void configureListView() {
        Log.d(TAG, "configureListView called");

        List<AlarmTime> alarmTimes = getSuggestedSleepTimes();

        listView = findViewById(R.id.alarmListView);
        AlarmListViewAdapter adapter = new AlarmListViewAdapter(this, R.layout.alarm_list, alarmTimes);
        listView.setAdapter(adapter);
    }

    protected List<AlarmTime> getSuggestedSleepTimes() {
        Log.d(TAG, "getSuggestedSleepTimes called");

        int startAtHour = 6; // Start 6 hours from now
        int timePeriods = 7; // 30 min periods starting 6 hours from now
        LocalDateTime currentTime = LocalDateTime.now(); // Current time

        List<AlarmTime> alarmTimes = new ArrayList<>(timePeriods);
        String[] durations = new String[]{"6", "6.5", "7", "7.5", "8", "8.5", "9"};
        String[] ratings = new String[]{"OK", "OK", "Good", "Good", "Good", "Good", "Good"};

        for (int i = 0; i < timePeriods; i++) {
            alarmTimes.add(i, new AlarmTime(currentTime.plusMinutes(startAtHour * 60 + 30 * i), durations[i], ratings[i]));
        }

        return alarmTimes;
    }

}
