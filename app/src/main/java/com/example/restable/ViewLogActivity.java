package com.example.restable;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.graphics.Color;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.color.MaterialColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ViewLogActivity extends AppCompatActivity {

    private static final String TAG = "ViewLogActivity";
    protected DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a");

    //Defining the four Line Chart.
    protected LineChart humidityChart, tempChart, soundChart, motionChart;

    //Defining the four strings thats needed for the labeling of the charts.
    protected String humidity = "Humidity", temperature = "Temperature", sound = "Sound", motion = "Motion";

    //The Data coming from database , and DatabaseReference being the database itself.
    protected DatabaseReference databaseReference;
    protected SleepData sleepData;
    private Scores scores;

    //Defining the ArrayList which we will be storing the importing data.
    protected ArrayList<Float> humidityData, tempData, soundData, motionData;

    //Defining the ArrayList which we will be storing the formatted time for each charts.
    protected ArrayList<String> timeArray;

    private String key;
    private String notesText;

    //Defining The start and stop time LocalDateTime Oojects.
    protected LocalDateTime stopTime, startTime;

    //Defining TextView of activity_results.xml
    protected TextView start_Time, stop_Time, average_Temp, average_Humid, time_Slept, scoreTot, scoreH, scoreT, scoreM, scoreS,
            recTitle, humidTitle, tempTitle, soundTitle, motionTitle;

    //Defining ImageView for optimal temperature/humidity conditions
    protected ImageView condImage;

    protected EditText notes;

    //Defining Duration to find out how long a person has slept.
    protected Duration duration;

    protected ConstraintLayout rootLayout;
    protected AnimationDrawable animDrawable;

    //SharedPreference for setting theme
    SharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if (sharedpref.loadNightModeState()) {
            setTheme(R.style.NightTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);
        Log.d(TAG, "onCreate called");

        // Add animated background gradient
        rootLayout = findViewById(R.id.view_log_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        // Add custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_view_log);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        start_Time = findViewById(R.id.start_time_log);
        stop_Time = findViewById(R.id.stop_time_log);
        average_Temp = findViewById(R.id.average_temp_log);
        average_Humid = findViewById(R.id.average_humidity_log);
        time_Slept = findViewById(R.id.time_slept_log);
        recTitle = findViewById(R.id.recTitle);
        humidTitle = findViewById(R.id.humidTitle_log);
        tempTitle = findViewById(R.id.tempTitle_log);
        motionTitle = findViewById(R.id.motionTitle_log);
        soundTitle = findViewById(R.id.soundTitle_log);

        // Linking the EditText to the activity_results.xml id
        notes = (EditText) findViewById(R.id.notesText_log);

        sleepData = (SleepData) getIntent().getSerializableExtra("sleepData");
        assert sleepData != null;
        key = getIntent().getStringExtra("key");
        humidityData = sleepData.getHumidityData();
        tempData = sleepData.getTempData();
        soundData = sleepData.getSoundData();
        motionData = sleepData.getMotionData();
        notesText = sleepData.getNotes();
        scores = new Scores(sleepData);

        startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getStartTime()), ZoneId.systemDefault());
        stopTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getEndTime()), ZoneId.systemDefault());

        databaseReference = FirebaseDatabase.getInstance().getReference("Sessions");

        //Setup the optimal temp/humidity conditions icon
        condImage = findViewById(R.id.imageOptCond_log);
        condImage.setAlpha(0.9f);
        if (sharedpref.loadNightModeState()) {
            condImage.setImageResource(R.drawable.opt_cond_alt);
        } else {
            condImage.setImageResource(R.drawable.opt_cond);
        }

        scoreTot = (TextView) findViewById(R.id.scoreTotal_log);
        //scoreH =(TextView) findViewById(R.id.scoreHum);
        //scoreT =(TextView) findViewById(R.id.scoreTemp);
        //scoreM =(TextView) findViewById(R.id.scoreMot);
        //scoreS=(TextView) findViewById(R.id.scoreSound);
        databaseReference = FirebaseDatabase.getInstance().getReference("Sessions");

        humidityChart = findViewById(R.id.line_chart_humidity_log);
        tempChart = findViewById(R.id.line_chart_temp_log);
        soundChart = findViewById(R.id.line_chart_sound_log);
        motionChart = findViewById(R.id.line_chart_motion_log);

        timeArray = PeriodicDateTimeProducer(startTime, stopTime, humidityData.size());

        setData(humidityData, humidityChart, humidity, timeArray);
        setData(tempData, tempChart, temperature, timeArray);
        setData(soundData, soundChart, sound, timeArray);
        setData(motionData, motionChart, motion, timeArray);

        humidityChart = findViewById(R.id.line_chart_humidity_log);
        tempChart = findViewById(R.id.line_chart_temp_log);
        soundChart = findViewById(R.id.line_chart_sound_log);
        motionChart = findViewById(R.id.line_chart_motion_log);

        configureGraph(humidityChart);
        configureGraph(tempChart);
        configureGraph(soundChart);
        configureGraph(motionChart);

        notes.setText(notesText);

        duration = Duration.between(startTime, stopTime);

        start_Time.setText(String.format("Start Time %s", startTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
        stop_Time.setText(String.format("Stop Time %s", stopTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
        average_Temp.setText(String.format("Average\nTemperature: %1$s%2$s", calculateAverage(tempData), "°C"));
        average_Humid.setText(String.format("Average\nHumidity: %1$s%2$s", calculateAverage(humidityData), "%"));
        time_Slept.setText(String.format(Locale.getDefault(), "Time Slept: %d Hours %d Minutes", duration.toHours(), duration.toMinutes()));

        scoreTot.setText(String.format("Total Score: %1$s%2$s", scores.getScore(), "/10"));
        //scoreH.setText("Humidity score: "+ scores.getScrHum());
        //scoreT.setText("Temperature score: "+ scores.getScrTmp());
        //scoreS.setText("Sound score: "+ scores.getScrSound());
        //scoreM.setText("Motion score: "+ scores.getScrMotion());
    }

    //Configuration of Data going into the chart using LineData Set.
    protected void setData(ArrayList<Float> data, LineChart chart, String name, ArrayList<String> TimeArray) {
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (int x = 1; x < data.size() - 1; x++) {
            dataVals.add(new Entry(x, data.get(x)));
        }

        LineDataSet lineDataSet = new LineDataSet(dataVals, name + " Data Set");
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);

        // Get theme color
        @SuppressLint("RestrictedApi")
        int themeColor = MaterialColors.getColor(ViewLogActivity.this, R.attr.colorMain, Color.BLACK);

        lineDataSet.setColor(themeColor);
        lineDataSet.setDrawCircles(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(6, true);
        xAxis.setTextSize(8);
        xAxis.setValueFormatter(new MyXAxisValueformatter(TimeArray));

        LineData linedata = new LineData(dataSets);

        chart.setData(linedata);
        chart.invalidate();
    }

    private String calculateAverage(ArrayList<Float> marks) {
        Float sum = (float) 0;
        if (!marks.isEmpty()) {
            float average;
            for (Float mark : marks) {
                sum += mark;
            }
            average = sum / marks.size();
            return String.format(Locale.getDefault(), "%.2f", average);
        }
        return "0";
    }

    //Configuration of each Chart on the activity.
    protected void configureGraph(LineChart chart) {

        // Get theme color
        @SuppressLint("RestrictedApi")
        int themeColor = MaterialColors.getColor(ViewLogActivity.this, R.attr.colorMain, Color.BLACK);

        chart.setDrawBorders(true);
        chart.setBorderColor(themeColor);
        chart.getXAxis().setTextColor(themeColor);
        chart.setExtraRightOffset(20.0f);
        chart.getXAxis().setYOffset(5.0f);
        chart.getAxisLeft().setTextColor(themeColor);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getLegend().setEnabled(false);

    }

    //Creates ArrayList<Strings of the timestamp
    protected ArrayList<String> PeriodicDateTimeProducer(LocalDateTime start, LocalDateTime end, int num_cuts) {

        ArrayList<String> results = new ArrayList<String>(num_cuts);
        long duration = Duration.between(start, end).getSeconds();
        long delta = duration / (num_cuts - 1);

        for (int x = 0; x < num_cuts; x++) {

            results.add(start.plusSeconds(x * delta).format(DATE_TIME_FORMATTER));
        }

        return results;

    }

    //Changes the X Axis format from data number to time format.
    protected class MyXAxisValueformatter implements IAxisValueFormatter {

        private ArrayList<String> timearray;

        MyXAxisValueformatter(ArrayList<String> timearray) {
            super();
            this.timearray = timearray;
        }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return timearray.get((int) value);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_log_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_log) {
            Log.i(TAG, "Deleting log");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String owner = user.getUid();
            DatabaseReference dbRefPush = databaseReference.child(owner).child(key);
            dbRefPush.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            Log.i(TAG, "Log successfully deleted from Firebase for key " + key);
                            //Store the ArrayLists in the Intent
                            Intent intent = new Intent(ViewLogActivity.this, MainActivity.class);
                            Log.i(TAG, "Starting MainActivity");
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.e(TAG, "Delete of key " + key + " from Firebase failed");
                            Toast.makeText(ViewLogActivity.this, "Database delete failed", Toast.LENGTH_LONG).show();
                        }
                    });
            Intent intent = new Intent(ViewLogActivity.this, MainActivity.class);
            Log.i(TAG, "Starting MainActivity");
            startActivity(intent);
            Toast.makeText(ViewLogActivity.this, "Log deleted", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}