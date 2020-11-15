package com.example.restable;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlarmActivity  extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "AlarmActivity";

    //Instance variables
    protected Button recButton;
    protected Button customAlarmButton;
    protected ListView listView;
    private List<AlarmTime> alarmTimes;
    private AlarmListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        /* Insert app bar and enable back button to MainActivity */
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

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
                // Show user TimePicker dialog to enter time
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker for custom alarm");
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

    // Display ListView of suggested sleep times
    protected void configureListView() {
        Log.d(TAG, "configureListView called");

        final List<AlarmTime> alarmTimes = getSuggestedSleepTimes(); // Get the suggested times

        listView = findViewById(R.id.alarmListView);
        adapter = new AlarmListViewAdapter(this, R.layout.alarm_list,
                alarmTimes);
        listView.setAdapter(adapter); // Populate ListView

        // Handle pressing on a ListView item to set Alarm
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.d(TAG, "onItemClick for clicking ListView item " + position);

            // Get time information
            int hour = alarmTimes.get(position).getWakeTime().getHour();
            int minute = alarmTimes.get(position).getWakeTime().getMinute();

            goToRecActivity(); // Go to RecActivity
            setAlarm(hour, minute); // Set alarm
            }
        });
    }

    // Get the recommended wake times
    protected List<AlarmTime> getSuggestedSleepTimes() {
        Log.d(TAG, "getSuggestedSleepTimes called");

        int startAtHour = 4; // Start 6 hours from now
        int timePeriods = 11; // 11 30 min periods starting 6 hours from now
        LocalDateTime currentTime = LocalDateTime.now(); // Current time

        alarmTimes = new ArrayList<>(timePeriods);
        String[] durations = new String[]{"4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9"};
        String[] ratings = new String[]{"Unhealthy", "Unhealthy", "Unhealthy", "Unhealthy",
                                        "Decent", "Decent", "Healthy", "Healthy", "Healthy", "Healthy", "Healthy"};

        // Populate alarmTimes list
        for (int i = 0; i < timePeriods; i++) {
            alarmTimes.add(i, new AlarmTime(currentTime.plusMinutes(startAtHour * 60 + 30 * i),
                    durations[i], ratings[i]));
        }

        return alarmTimes;
    }

    // When custom time is inputted by user, set the alarm and go to RecActivity
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        goToRecActivity();
        setAlarm(hourOfDay, minute);
    }

    // Set the alarm with the provided time
    protected void setAlarm(int hour, int minute) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM); // Intent to set alarm
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        Log.i(TAG, "Setting alarm for " + hour + ":" + String.format(Locale.getDefault(),
                "%02d", minute));
        startActivity(intent); // Set alarm
    }
}
