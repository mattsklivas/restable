package com.example.restable;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //Instance variables
    protected Button alarmButton;
    protected Button logsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup activity views
        alarmButton = findViewById(R.id.buttonSleep);
        logsButton = findViewById(R.id.buttonLogs);

        //Go to AlarmActivity
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlarmActivity();
            }
        });

        //Go to LogsActivity
        logsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogsActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //Go to AlarmActivity
    protected void goToAlarmActivity() {
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
    }

    //Go to LogsActivity
    protected void goToLogsActivity() {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }
}