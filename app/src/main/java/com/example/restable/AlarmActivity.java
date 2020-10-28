package com.example.restable;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlarmActivity  extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";

    //Instance variables
    protected Button recButton;
    protected Button customAlarmButton;
    protected ListView listView;
    List<AlarmTime> alarmTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //Setup activity views
        recButton = findViewById(R.id.buttonRec);
        customAlarmButton = findViewById(R.id.buttonCustomAlarm);

        //Setup recButton
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRecActivity();
            }
        });

        //Setup customAlarmButton
        customAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToClockApp();
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

    //Go to Clock app
    protected void goToClockApp() {
        Intent intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        startActivity(intent);
    }

    protected void configureListView() {
        Log.d(TAG, "configureListView called");

        final List<AlarmTime> alarmTimes = getSuggestedSleepTimes();

        listView = findViewById(R.id.alarmListView);
        AlarmListViewAdapter adapter = new AlarmListViewAdapter(this, R.layout.alarm_list, alarmTimes);
        listView.setAdapter(adapter);

        // Handle pressing on a ListView item to set Alarm
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick for clicking ListView item " + position);

                int hour = alarmTimes.get(position).getWakeTime().getHour();
                int minute = alarmTimes.get(position).getWakeTime().getMinute();

                if (!alarmTimes.get(position).isSet()) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                    intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    Log.i(TAG, "Setting alarm for " + hour + ":" + String.format(Locale.CANADA, "%02d", minute));
                    startActivity(intent);
                    view.findViewById(R.id.setTextView).setVisibility(View.VISIBLE);
                    alarmTimes.get(position).setSet(true);
                }
                else {
                    Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_TIME);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                    Log.i(TAG, "Dismissing alarm for " + hour + ":" + String.format(Locale.CANADA, "%02d", minute));
                    startActivity(intent);
                    Toast.makeText(AlarmActivity.this, "Alarm dismissed. Press back button to go back to Restable", Toast.LENGTH_LONG).show();
                    view.findViewById(R.id.setTextView).setVisibility(View.INVISIBLE);
                    alarmTimes.get(position).setSet(false);
                }
            }
        });
    }

    protected List<AlarmTime> getSuggestedSleepTimes() {
        Log.d(TAG, "getSuggestedSleepTimes called");

        int startAtHour = 6; // Start 6 hours from now
        int timePeriods = 7; // 30 min periods starting 6 hours from now
        LocalDateTime currentTime = LocalDateTime.now(); // Current time

        alarmTimes = new ArrayList<>(timePeriods);
        String[] durations = new String[]{"6", "6.5", "7", "7.5", "8", "8.5", "9"};
        String[] ratings = new String[]{"OK", "OK", "Good", "Good", "Good", "Good", "Good"};

        for (int i = 0; i < timePeriods; i++) {
            alarmTimes.add(i, new AlarmTime(currentTime.plusMinutes(startAtHour * 60 + 30 * i), durations[i], ratings[i]));
        }

        return alarmTimes;
    }

}
