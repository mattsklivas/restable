package com.example.restable;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.graph.ImmutableValueGraph;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.type.DateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private ArrayList<String > timearrayhumidity;
    private ArrayList<String > timearraytemperature;
    private ArrayList<String > timearraysound;
    private ArrayList<String > timearraymotion;

    protected LocalDateTime stopTime;
    protected LocalDateTime startTime;
    protected DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a");

    protected TextView start_Time;
    protected TextView stop_Time;
    protected TextView average_Temp;
    protected TextView average_Humid;
    protected TextView time_Slept;

    protected Button done_button;
    protected Button save_button;
    protected Button otherReturnButton;

    private Duration duration;

    //protected ConstraintLayout rootLayout;
    //protected AnimationDrawable animDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        // Add animated background gradient
//        rootLayout = (ConsrtaintLayout) findViewById(R.id.results_layout);
//        animDrawable = (AnimationDrawable) rootLayout.getBackground();
//        animDrawable.setEnterFadeDuration(10);
//        animDrawable.setExitFadeDuration(5000);
//        animDrawable.start();

        done_button = findViewById(R.id.done_button);
        save_button = findViewById(R.id.save_button);
        otherReturnButton = findViewById(R.id.return_button);

        start_Time = findViewById(R.id.start_time);
        stop_Time = findViewById(R.id.stop_time);
        average_Temp = findViewById(R.id.average_temp);
        average_Humid = findViewById(R.id.average_humidity);
        time_Slept = findViewById(R.id.time_slept);

        sleepData = (SleepData) getIntent().getSerializableExtra("sleepData");

        if(sleepData == null)
        {
            setContentView(R.layout.activity_no_result);
            Log.d(TAG, "onCreate called");

            otherReturnButton = findViewById(R.id.return_button);

            otherReturnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "other_done_button onClick called");
                    Intent intent = new Intent(ResultsActivity.this, RecActivity.class);
                    startActivity(intent);
                }
            });
        }

        else{

            humidityData = sleepData.getHumidityData();
            tempData = sleepData.getTempData();
            soundData = sleepData.getSoundData();
            motionData = sleepData.getMotionData();
            startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getStartTime()), ZoneId.systemDefault());
            stopTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getEndTime()), ZoneId.systemDefault());

            setContentView(R.layout.activity_results);
            Log.d(TAG, "onCreate called");

            done_button = findViewById(R.id.done_button);
            save_button = findViewById(R.id.save_button);

            start_Time = findViewById(R.id.start_time);
            stop_Time = findViewById(R.id.stop_time);
            average_Temp = findViewById(R.id.average_temp);
            average_Humid = findViewById(R.id.average_humidity);
            time_Slept = findViewById(R.id.time_slept);

            databaseReference = FirebaseDatabase.getInstance().getReference("Sessions");

            System.out.println("Dummy data if user hasn't connected to the hardware:");
            System.out.println("tempData:" + tempData);
            System.out.println("humidityData:" + humidityData);
            System.out.println("soundData:" + soundData);
            System.out.println("motionData:" + motionData);

            humiditychart = findViewById(R.id.line_chart_humidity);
            humiditychart.setDrawBorders(true);
            humiditychart.setBorderColor(Color.BLUE);;
            humiditychart.getDescription().setEnabled(false);
            humiditychart.getXAxis().setDrawGridLines(false);
            humiditychart.setDragEnabled(false);
            humiditychart.setScaleEnabled(false);
            humiditychart.getAxisRight().setEnabled(false);
            humiditychart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            humiditychart.getLegend().setEnabled(false);

            tempchart = findViewById(R.id.line_chart_temp);
            tempchart.setDrawBorders(true);
            tempchart.setBorderColor(Color.BLUE);
            tempchart.getDescription().setEnabled(false);
            tempchart.setDragEnabled(false);
            tempchart.setScaleEnabled(false);
            tempchart.getXAxis().setDrawGridLines(false);
            tempchart.getAxisRight().setEnabled(false);
            tempchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            tempchart.getLegend().setEnabled(false);

            soundchart = findViewById(R.id.line_chart_sound);
            soundchart.setDrawBorders(true);
            soundchart.setBorderColor(Color.BLUE);
            soundchart.setDragEnabled(false);
            soundchart.setScaleEnabled(false);
            soundchart.getDescription().setEnabled(false);
            soundchart.getXAxis().setDrawGridLines(false);
            soundchart.getAxisRight().setEnabled(false);
            soundchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            soundchart.getLegend().setEnabled(false);

            motionchart = findViewById(R.id.line_chart_motion);
            motionchart.setDrawBorders(true);
            motionchart.setBorderColor(Color.BLUE);
            motionchart.setDragEnabled(false);
            motionchart.setScaleEnabled(false);
            motionchart.getDescription().setEnabled(false);
            motionchart.getXAxis().setDrawGridLines(false);
            motionchart.getAxisRight().setEnabled(false);
            motionchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            motionchart.getLegend().setEnabled(false);

            timearrayhumidity = PeriodicDateTimeProducer(startTime,stopTime,humidityData.size());
            timearraytemperature = PeriodicDateTimeProducer(startTime,stopTime,tempData.size());
            timearraysound = PeriodicDateTimeProducer(startTime,stopTime,soundData.size());
            timearraymotion = PeriodicDateTimeProducer(startTime,stopTime,motionData.size());

            setData(humidityData,humiditychart,humidity,timearrayhumidity);
            setData(tempData,tempchart,temperature,timearraytemperature);
            setData(soundData,soundchart,sound,timearraysound);
            setData(motionData,motionchart,motion,timearraymotion);

            duration = Duration.between(startTime, stopTime);

            start_Time.setText(String.format("Start Time %s", startTime.format(DATE_TIME_FORMATTER)));
            stop_Time.setText(String.format("Stop Time %s", stopTime.format(DATE_TIME_FORMATTER)));
            average_Temp.setText(String.format("Average Temperature (°C): %s", calculateAverage(tempData)));
            average_Humid.setText(String.format("Average Humidity (RH %%): %s", calculateAverage(humidityData)));
            time_Slept.setText(String.format(Locale.getDefault(), "Time Slept: %d Hours %d Minutes", duration.toHours(), duration.toMinutes()));

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

        //Setup doneButton
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "done_button onClick called");
                Intent main_intent = new Intent(ResultsActivity.this, MainActivity.class);
                Log.i(TAG, "Starting MainActivity");
                startActivity(main_intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    protected void setData(ArrayList<Float> data, LineChart chart, String name,ArrayList<String> TimeArray){
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (int x = 1; x < data.size()-1; x++)
        {
                dataVals.add(new Entry(x, data.get(x)));
        }



        LineDataSet lineDataSet = new LineDataSet(dataVals, name + " Data Set");
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.GRAY);
        lineDataSet.setDrawCircles(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(5,true);
        xAxis.setTextSize(5);
        xAxis.setValueFormatter(new MyXAxisValueformatter(TimeArray));
        //xAxis.setValueFormatter(new MyXAxisValueformatter(formattedStartDateTime));  // start,end,initial,final value

        LineData linedata = new LineData(dataSets);

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

    private ArrayList<String> PeriodicDateTimeProducer(LocalDateTime start, LocalDateTime end, int num_cuts) {
        ArrayList<String> results = new ArrayList<String>(num_cuts);
        long duration = Duration.between(start, end).getSeconds();
        long delta = duration/(num_cuts-1);

        for (int x = 0; x < num_cuts; x++)
        {

            results.add(start.plusSeconds(x*delta).format(DATE_TIME_FORMATTER));
        }

        return results;

    }

   private class MyXAxisValueformatter implements IAxisValueFormatter {

        private ArrayList<String> timearray;

        MyXAxisValueformatter(ArrayList<String> timearray){
            super();
            this.timearray = timearray;
        }


       @Override
       public String getFormattedValue(float value, AxisBase axis){
            return timearray.get((int)value);
        }
    }
}