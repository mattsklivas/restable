package com.example.restable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

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
    }
}