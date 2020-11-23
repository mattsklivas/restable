package com.example.restable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;


public class RecActivity  extends BlunoLibrary {

    //Instance variables
    private static final String TAG = "RecActivity";
    protected Button buttonScan;
    protected TextView serialReceivedText;
    protected TextView statusText;
    protected Button stopButton;
    private StringBuilder receivedData;
    private Boolean connected = false;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = LocalDateTime.now();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        Log.d(TAG, "onCreate called");

        //onCreateProcess from BlunoLibrary
        onCreateProcess();

        //Get permissions to connect to the device and start serial data processing
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        serialBegin(115200);

        serialReceivedText=(TextView) findViewById(R.id.serialReceivedText); //View Received data
        statusText=(TextView) findViewById(R.id.textStatus); //Main recording message
        receivedData = new StringBuilder(); //StringBuilder used to obtain the serial output
        buttonScan = (Button) findViewById(R.id.buttonScan); //Initialize the button used for scanning the device
        stopButton = findViewById(R.id.buttonStop); //Button used to enter ResultsActivity

        //Initialize buttonScan
        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Ensure permissions are properly obtained from the user to scan for devices
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED){
                    boolean requestCheck = ActivityCompat.shouldShowRequestPermissionRationale(RecActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if (requestCheck){
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }else {
                        new AlertDialog.Builder(RecActivity.this)
                                .setTitle("Permission Required")
                                .setMessage("Please enable location permission to use this application.")
                                .setNeutralButton("I Understand", null)
                                .show();
                    }
                }else {
                    buttonScanOnClickProcess(); //Alert Dialog for selecting the BLE device
                }
            }
        });

        //Setup stopButton
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Stop Button pressed");
                Log.d(TAG, "stopButton onclick called");
                Intent intent = new Intent(v.getContext(), ResultsActivity.class);
                endTime = LocalDateTime.now();

                //Store the received data if the user connected to the device
                if (connected) {
                    //Parse and store the received data in separate ArrayLists
                    Log.d(TAG, "received data being stored");
                    SleepData parsedData = new SleepData(receivedData, startTime, endTime);
                    intent.putExtra("sleepData", parsedData);
                }
                else {
                    Log.d(TAG, "dummy data being stored");
                    //Dummy sensor data from hardware
                    double[] humidity = {53.5, 53.6, 53.6, 53.5, 53.6, 53.6, 53.5, 53.6, 53.5, 53.6, 53.5, 53.5, 53.5, 53.6, 53.6, 53.5, 53.5, 53.5, 53.5, 53.5, 53.6, 53.5, 53.6, 53.7, 53.6, 53.6, 53.7};
                    double[] temp = {23.6, 23.7, 23.7, 23.6, 23.7, 23.7, 23.6, 23.7, 23.6, 23.7, 23.6, 23.6, 23.6, 23.7, 23.7, 23.6, 23.6, 23.6, 23.6, 23.6, 23.7, 23.6, 23.7, 23.7, 23.6, 23.6, 23.7};
                    double[] sound = {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                    double[] motion = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0};

                    //Initialize ArrayLists
                    ArrayList<Float> humidityData = new ArrayList<>();
                    ArrayList<Float> tempData = new ArrayList<>();
                    ArrayList<Float> soundData = new ArrayList<>();
                    ArrayList<Float> motionData = new ArrayList<>();

                    //Store dummy sensor data in their respective ArrayLists
                    for (int i = 0; i < humidity.length; i++){
                        humidityData.add((float) humidity[i]);
                        tempData.add((float) temp[i]);
                        soundData.add((float) sound[i]);
                        motionData.add((float) motion[i]);
                    }

                    SleepData parsedData = new SleepData(humidityData, tempData, soundData, motionData, startTime, endTime);
                    intent.putExtra("sleepData", parsedData);
                }
                //Store dummy sensor data in the ArrayLists so developers without access to hardware can work on the app
                Log.i(TAG, "starting ResultsActivity");
                startActivity(intent);
            }
        });
    }

    protected void onResume(){
        super.onResume();
        Log.d(TAG, "RecActivity onResume");
        onResumeProcess();	//onResumeProcess from BlunoLibrary
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);	//onActivityResultProcess from BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess(); //onPauseProcess from BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess(); //onStopProcess from BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();	//onDestroyProcess from BlunoLibrary
    }

    //Function which deals with changed connection states
    @Override
    public void onConnectionStateChange(connectionStateEnum theConnectionState) {
        Log.d(TAG, "onConnectionStateChange called for state " + theConnectionState);
        switch (theConnectionState) {
            case isConnected:
                buttonScan.setText(R.string.disconnect);
                Toast.makeText(RecActivity.this, "Device connected!", Toast.LENGTH_SHORT).show();
                statusText.setText(R.string.restable_now_recording);
                connected = true;
                break;
            case isConnecting:
                Toast.makeText(RecActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
                break;
            case isToScan:
                buttonScan.setText(R.string.connect);
                break;
            case isScanning:
                Toast.makeText(RecActivity.this, "Scanning for devices...", Toast.LENGTH_SHORT).show();
                break;
            case isDisconnecting:
                Toast.makeText(RecActivity.this, "Device disconnected", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    //Function which handles received serial data
    @Override
    public void onSerialReceived(String theString) {
        Log.i(TAG, theString + "from sensor");
        //Append the text into the EditText and print it to to the Scrollview (for debugging purposes)
        serialReceivedText.append(theString);
        ((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);

        //Store the received data in receivedData for ResultsActivity
        receivedData.append(theString);
    }

}
