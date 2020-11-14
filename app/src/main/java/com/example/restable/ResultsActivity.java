package com.example.restable;

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

    ArrayList<Float> humidityData;
    ArrayList<Float> tempData;
    ArrayList<Float> soundData;
    ArrayList<Float> motionData;

    LocalDateTime stopTime;
    LocalDateTime startTime;

    TextView start_Time;
    TextView stop_Time;
    TextView average_Temp;
    TextView average_Humid;
    TextView time_Slept;

    Button done_button;

    Duration duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stopTime = LocalDateTime.now();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        done_button = (Button) findViewById(R.id.done_button);

        start_Time = (TextView) findViewById(R.id.start_time);
        stop_Time = (TextView) findViewById(R.id.stop_time);
        average_Temp = (TextView) findViewById(R.id.average_temp);
        average_Humid = (TextView) findViewById(R.id.average_humidity);
        time_Slept = (TextView) findViewById(R.id.time_slept);

        tempData = (ArrayList<Float>) getIntent().getSerializableExtra("tempData");
        humidityData = (ArrayList<Float>) getIntent().getSerializableExtra("humidityData");
        soundData = (ArrayList<Float>) getIntent().getSerializableExtra("soundData");
        motionData = (ArrayList<Float>) getIntent().getSerializableExtra("motionData");

        startTime =(LocalDateTime) getIntent().getSerializableExtra("start time");

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

        setData(tempData,tempchart,temperature,startTime,stopTime);
        setData(humidityData,humiditychart,humidity,startTime,stopTime);
        setData(soundData,soundchart,sound,startTime,stopTime);
        setData(motionData,motionchart,motion,startTime,stopTime);

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
    }
    
    protected void setData(ArrayList<Float> data, LineChart chart, String name, LocalDateTime startTime, LocalDateTime stopTime){

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int x = 0; x < data.size(); x++)
        {
            if(x ==0)
            {
                yValues.add(new Entry(startTime.format(x, data.get(x)));
            }
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
}