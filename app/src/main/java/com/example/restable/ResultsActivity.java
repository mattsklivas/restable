package com.example.restable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    protected TextView start_Time;
    protected TextView stop_Time;
    protected TextView average_Temp;
    protected TextView average_Humid;
    protected TextView time_Slept;

    protected Button done_button;
    protected Button save_button;

    private Duration duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        done_button = (Button) findViewById(R.id.done_button);
        save_button = (Button) findViewById(R.id.save_button);

        start_Time = (TextView) findViewById(R.id.start_time);
        stop_Time = (TextView) findViewById(R.id.stop_time);
        average_Temp = (TextView) findViewById(R.id.average_temp);
        average_Humid = (TextView) findViewById(R.id.average_humidity);
        time_Slept = (TextView) findViewById(R.id.time_slept);

        sleepData = (SleepData) getIntent().getSerializableExtra("sleepData");
        humidityData = sleepData.getHumidityData();
        tempData = sleepData.getTempData();
        soundData = sleepData.getSoundData();
        motionData = sleepData.getMotionData();
        startTime = sleepData.getStartTime();
        stopTime = sleepData.getEndTime();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Sessions");

        System.out.println("Dummy data if user hasn't connected to the hardware:");
        System.out.println("tempData:" + tempData);
        System.out.println("humidityData:" + humidityData);
        System.out.println("soundData:" + soundData);
        System.out.println("motionData:" + motionData);

        humiditychart = (LineChart) findViewById(R.id.line_chart_humidity);
        humiditychart.setDragEnabled(true);
        humiditychart.setScaleEnabled(true);
        humiditychart.getDescription().setEnabled(false);
        humiditychart.getXAxis().setDrawGridLines(false);
        humiditychart.getAxisRight().setEnabled(false);
        humiditychart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        tempchart = (LineChart) findViewById(R.id.line_chart_temp);
        tempchart.setDragEnabled(true);
        tempchart.setScaleEnabled(true);
        tempchart.getDescription().setEnabled(false);
        tempchart.getXAxis().setDrawGridLines(false);
        tempchart.getAxisRight().setEnabled(false);
        tempchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        soundchart = (LineChart) findViewById(R.id.line_chart_sound);
        soundchart.setDragEnabled(true);
        soundchart.setScaleEnabled(true);
        soundchart.getDescription().setEnabled(false);
        soundchart.getXAxis().setDrawGridLines(false);
        soundchart.getAxisRight().setEnabled(false);
        soundchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        motionchart = (LineChart) findViewById(R.id.line_chart_motion);
        motionchart.setDragEnabled(true);
        motionchart.setScaleEnabled(true);
        motionchart.getDescription().setEnabled(false);
        motionchart.getXAxis().setDrawGridLines(false);
        motionchart.getAxisRight().setEnabled(false);
        motionchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        setData(tempData,tempchart,temperature);
        setData(humidityData,humiditychart,humidity);
        setData(soundData,soundchart,sound);
        setData(motionData,motionchart,motion);

        duration = Duration.between(startTime, stopTime);

        start_Time.setText("Start Time " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        stop_Time.setText("Stop Time " + stopTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        average_Temp.setText("Average Temperature (°C): " + calculateAverage(tempData));
        average_Humid.setText("Average Humidity (RH %): " + calculateAverage(humidityData));
        time_Slept.setText("Time Slept: " + duration.toHours() + "Hours " + duration.toMinutes() + "Minutes");

        //Setup doneButton
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main_intent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(main_intent);
            }
        });

        //Setup saveButton
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String owner = user.getUid();
                DatabaseReference dbRefPush = databaseReference.child(owner).push();
                dbRefPush.setValue(sleepData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!

                                //Store the ArrayLists in the Intent
                                Intent intent = new Intent(v.getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                Toast.makeText(ResultsActivity.this, "Database write failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
    
    protected void setData(ArrayList<Float> data, LineChart chart, String name ){

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int x = 0; x < data.size(); x++)
        {
            yValues.add(new Entry(x, data.get(x)));
        }

        LineDataSet set = new LineDataSet(yValues, name + " Data Set");
        set.setDrawValues(false);

        set.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData linedata = new LineData(dataSets);

        chart.setData(linedata);
    }

    private String calculateAverage(ArrayList <Float> marks) {
        Float sum = (float) 0;
        if(!marks.isEmpty()) {
            Float average;
            for (Float mark : marks) {
                sum += mark;
            }
            average = sum / marks.size();
            String strDouble = String.format("%.2f", average);
             return strDouble;
        }
        return "0";
    }

    @Override
    public void onBackPressed() {
        // Do Nothing
    }
}