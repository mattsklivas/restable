package com.example.restable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";

    private LineChart humiditychart;
    private LineChart tempchart;
    private LineChart soundchart;
    private LineChart motionchart;

    private String humidity = "Humidity";
    private String temperature = "Temperature";
    private String sound = "Sound";
    private String motion = "Motion";
    private DatabaseReference databaseReference;
    private SleepData sleepData;

    private ArrayList<Float> humidityData;
    private ArrayList<Float> tempData;
    private ArrayList<Float> soundData;
    private ArrayList<Float> motionData;

    private LocalDateTime stopTime;
    private LocalDateTime startTime;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected TextView start_Time;
    protected TextView stop_Time;
    protected TextView average_Temp;
    protected TextView average_Humid;
    protected TextView time_Slept;

    protected Button done_button;
    protected Button save_button;
    protected Button otherReturnButton;

    private Duration duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Log.d(TAG, "onCreate called");

        done_button = findViewById(R.id.done_button);
        save_button = findViewById(R.id.save_button);
        otherReturnButton = findViewById(R.id.return_button);

        start_Time = findViewById(R.id.start_time);
        stop_Time = findViewById(R.id.stop_time);
        average_Temp = findViewById(R.id.average_temp);
        average_Humid = findViewById(R.id.average_humidity);
        time_Slept = findViewById(R.id.time_slept);

        sleepData = (SleepData) getIntent().getSerializableExtra("sleepData");
        assert sleepData != null;
        humidityData = sleepData.getHumidityData();
        tempData = sleepData.getTempData();
        soundData = sleepData.getSoundData();
        motionData = sleepData.getMotionData();
        startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getStartTime()), ZoneId.systemDefault());
        stopTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getEndTime()), ZoneId.systemDefault());

        databaseReference = FirebaseDatabase.getInstance().getReference("Sessions");

        System.out.println("Dummy data if user hasn't connected to the hardware:");
        System.out.println("tempData:" + tempData);
        System.out.println("humidityData:" + humidityData);
        System.out.println("soundData:" + soundData);
        System.out.println("motionData:" + motionData);

        humiditychart = findViewById(R.id.line_chart_humidity);
        humiditychart.setDragEnabled(true);
        humiditychart.setScaleEnabled(true);
        humiditychart.getDescription().setEnabled(false);
        humiditychart.getXAxis().setDrawGridLines(false);
        humiditychart.getAxisRight().setEnabled(false);
        humiditychart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        tempchart = findViewById(R.id.line_chart_temp);
        tempchart.setDragEnabled(true);
        tempchart.setScaleEnabled(true);
        tempchart.getDescription().setEnabled(false);
        tempchart.getXAxis().setDrawGridLines(false);
        tempchart.getAxisRight().setEnabled(false);
        tempchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        soundchart = findViewById(R.id.line_chart_sound);
        soundchart.setDragEnabled(true);
        soundchart.setScaleEnabled(true);
        soundchart.getDescription().setEnabled(false);
        soundchart.getXAxis().setDrawGridLines(false);
        soundchart.getAxisRight().setEnabled(false);
        soundchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        motionchart = findViewById(R.id.line_chart_motion);
        motionchart.setDragEnabled(true);
        motionchart.setScaleEnabled(true);
        motionchart.getDescription().setEnabled(false);
        motionchart.getXAxis().setDrawGridLines(false);
        motionchart.getAxisRight().setEnabled(false);
        motionchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        setData(tempData,tempchart,temperature,startTime,stopTime);
        setData(humidityData,humiditychart,humidity,startTime,stopTime);
        setData(soundData,soundchart,sound,startTime,stopTime);
        setData(motionData,motionchart,motion,startTime,stopTime);

        duration = Duration.between(startTime, stopTime);

        start_Time.setText(String.format("Start Time %s", startTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
        stop_Time.setText(String.format("Stop Time %s", stopTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
        average_Temp.setText(String.format("Average Temperature (°C): %s", calculateAverage(tempData)));
        average_Humid.setText(String.format("Average Humidity (RH %%): %s", calculateAverage(humidityData)));
        time_Slept.setText(String.format(Locale.getDefault(), "Time Slept: %d Hours %d Minutes", duration.toHours(), duration.toMinutes()));

        if(humidityData.size() == 0 && motionData.size() == 0 && soundData.size() == 0 && tempData.size() == 0)
        {
            setContentView(R.layout.activity_no_result);

            otherReturnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "other_done_button onClick called");
                    Intent intent = new Intent(ResultsActivity.this, RecActivity.class);
                    startActivity(intent);
                }
            });
        }

        //Setup doneButton
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "done_button onClick called");
                Intent main_intent = new Intent(ResultsActivity.this, MainActivity.class);
                Log.i(TAG, "Starting MainActivity");
                startActivity(main_intent);
            }
        });

        //Setup saveButton
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "save_button onClick called");
                Log.i(TAG, "saving to firebase database");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String owner = user.getUid();
                DatabaseReference dbRefPush = databaseReference.child(owner).push();
                dbRefPush.setValue(sleepData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!
                                Log.i(TAG, "Write to firebase database successful");
                                //Store the ArrayLists in the Intent
                                Intent intent = new Intent(v.getContext(), MainActivity.class);
                                Log.i(TAG, "Starting MainActivity");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                Log.e(TAG, "Write to firebase database failed");
                                Toast.makeText(ResultsActivity.this, "Database write failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    protected void setData(ArrayList<Float> data, LineChart chart, String name, LocalDateTime startTime, LocalDateTime stopTime){
        ArrayList<Entry> yValues = new ArrayList<>();
        final HashMap<Integer, String> labels = new HashMap<>();
        String formattedStartDateTime = startTime.format(formatter);
        String formattedStopDateTime = stopTime.format(formatter);
        for (int x = 0; x < data.size(); x++)
        {
            if(x == 0)
            {
                labels.put(x,formattedStartDateTime);
                yValues.add(new Entry(x, data.get(x)));
            }

            else if(x == data.size()-1)
            {
                labels.put(x,formattedStopDateTime);
                yValues.add(new Entry(x, data.get(x)));
            }
            else
                labels.put(x,Integer.toString(x));
            yValues.add(new Entry(x, data.get(x)));
        }

        LineDataSet set = new LineDataSet(yValues, name + " Data Set");
        set.setDrawValues(false);

        set.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData linedata = new LineData(dataSets);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(2){

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return labels.get((int)value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        chart.setData(linedata);
        chart.invalidate();
    }

    private String calculateAverage(ArrayList <Float> marks) {
        Float sum = (float) 0;
        if(!marks.isEmpty()) {
            float average;
            for (Float mark : marks) {
                sum += mark;
            }
            average = sum / marks.size();
            return String.format(Locale.getDefault(), "%.2f", average);
        }
        return "0";
    }

    @Override
    public void onBackPressed() {
        Context context = getApplicationContext();
        CharSequence text = "Returning isn't permitted";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}