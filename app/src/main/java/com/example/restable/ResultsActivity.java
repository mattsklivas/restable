package com.example.restable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    
    private LineChart humiditychart;
    private LineChart tempchart;
    private LineChart soundchart;
    private LineChart motionchart;
    
    ArrayList<Float> humidityData;
    ArrayList<Float> tempData;
    ArrayList<Float> soundData;
    ArrayList<Float> motionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        tempData = (ArrayList<Float>) getIntent().getSerializableExtra("tempData");
        humidityData = (ArrayList<Float>) getIntent().getSerializableExtra("humidityData");
        soundData = (ArrayList<Float>) getIntent().getSerializableExtra("soundData");
        motionData = (ArrayList<Float>) getIntent().getSerializableExtra("motionData");
        System.out.println("Dummy data if user hasn't connected to the hardware:");
        System.out.println("tempData:" + tempData);
        System.out.println("humidityData:" + humidityData);
        System.out.println("soundData:" + soundData);
        System.out.println("motionData:" + motionData);

        humiditychart = (LineChart) findViewById(R.id.line_chart_humidity);
        humiditychart.setDragEnabled(true);
        humiditychart.setScaleEnabled(false);

        tempchart = (LineChart) findViewById(R.id.line_chart_temp);
        tempchart.setDragEnabled(true);
        tempchart.setScaleEnabled(false);

        soundchart = (LineChart) findViewById(R.id.line_chart_sound);
        soundchart.setDragEnabled(true);
        soundchart.setScaleEnabled(false);

        motionchart = (LineChart) findViewById(R.id.line_chart_motion);
        motionchart.setDragEnabled(true);
        motionchart.setScaleEnabled(false);

        setData(tempData, tempchart);
        setData(humidityData ,humiditychart);
        setData(soundData, soundchart);
        setData(motionData, motionchart);

    }
    
    protected void setData(ArrayList<Float> data, LineChart chart ){

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int x = 0; x < data.size(); x++)
        {
            yValues.add(new Entry(x, data.get(x)));
        }

        LineDataSet set = new LineDataSet(yValues, "Humidity Data Set 1");

        set.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData linedata = new LineData(dataSets);

        chart.setData(linedata);
    }
}