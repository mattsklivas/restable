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

import java.util.ArrayList;


public class RecActivity  extends BlunoLibrary {

    //Instance variables
    private DatabaseReference databaseReference;
    private static final String TAG = "RecActivity";
    protected Button buttonScan;
    protected TextView serialReceivedText;
    protected TextView statusText;
    protected Button stopButton;
    protected StringBuilder receivedData;
    protected Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Sessions");

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
            public void onClick(final View v) {
                final Intent intent = new Intent(v.getContext(), ResultsActivity.class);

                //Store the received data if the user connected to the device
                if (connected) {
                    //Parse and store the received data in separate ArrayLists
                    final SleepData parsedData = new SleepData(receivedData, ServerValue.TIMESTAMP);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String owner = user.getUid();
                    DatabaseReference dbRefPush = databaseReference.child(owner).push();
                    final String key = dbRefPush.getKey();
                    dbRefPush.setValue(parsedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Write was successful!

                                    //Store the ArrayLists in the Intent
                                    Intent intent = new Intent(v.getContext(), ResultsActivity.class);

                                    //TODO Remove below block, implement RecActivity DB lookup
                                    ArrayList<Float> humidityData = new ArrayList<>(parsedData.getHumidityData());
                                    ArrayList<Float> tempData = new ArrayList<>(parsedData.getTempData());
                                    ArrayList<Float> soundData = new ArrayList<>(parsedData.getSoundData());
                                    ArrayList<Float> motionData = new ArrayList<>(parsedData.getMotionData());
                                    intent.putExtra("humidityData", humidityData);
                                    intent.putExtra("tempData", tempData);
                                    intent.putExtra("soundData", soundData);
                                    intent.putExtra("motionData", motionData);

                                    intent.putExtra("key", key);

                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    Toast.makeText(RecActivity.this, "Database write failed", Toast.LENGTH_LONG).show();
                                }
                            });


                }
                //Store dummy sensor data in the ArrayLists so developers without access to hardware can work on the app
                else {
                    //Dummy sensor data from hardware
                    double[] humidity = {53.5, 53.6, 53.6, 53.5, 53.6, 53.6, 53.5, 53.6, 53.5, 53.6, 53.5, 53.5, 53.5, 53.6, 53.6, 53.5, 53.5, 53.5, 53.5, 53.5, 53.6, 53.5, 53.6, 53.7, 53.6, 53.6, 53.7};
                    double[] temp = {23.6, 23.7, 23.7, 23.6, 23.7, 23.7, 23.6, 23.7, 23.6, 23.7, 23.6, 23.6, 23.6, 23.7, 23.7, 23.6, 23.6, 23.6, 23.6, 23.6, 23.7, 23.6, 23.7, 23.7, 23.6, 23.6, 23.7};
                    double[] sound = {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                    double[] motion = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0};

                    //Initialize ArrayLists
                    final ArrayList<Float> humidityData = new ArrayList<>();
                    final ArrayList<Float> tempData = new ArrayList<>();
                    final ArrayList<Float> soundData = new ArrayList<>();
                    final ArrayList<Float> motionData = new ArrayList<>();

                    //Store dummy sensor data in their respective ArrayLists
                    for (int i = 0; i < humidity.length; i++){
                        humidityData.add((float) humidity[i]);
                        tempData.add((float) temp[i]);
                        soundData.add((float) sound[i]);
                        motionData.add((float) motion[i]);
                    }
                    SleepData parsedData = new SleepData(humidityData, tempData, soundData, motionData, ServerValue.TIMESTAMP);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String owner = user.getUid();
                    DatabaseReference dbRefPush = databaseReference.child(owner).push();
                    final String key = dbRefPush.getKey();
                    dbRefPush.setValue(parsedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Write was successful!

                                    //Store the ArrayLists in the Intent
                                    Intent intent = new Intent(v.getContext(), ResultsActivity.class);

                                    //TODO Remove below block, implement RecActivity DB lookup
                                    intent.putExtra("humidityData", humidityData);
                                    intent.putExtra("tempData", tempData);
                                    intent.putExtra("soundData", soundData);
                                    intent.putExtra("motionData", motionData);

                                    intent.putExtra("key", key);

                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    Toast.makeText(RecActivity.this, "Database write failed", Toast.LENGTH_LONG).show();
                                }
                            });


                }

            }
        });
    }

    protected void onResume(){
        super.onResume();
        Log.i(TAG, "RecActivity onResume");
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
        switch (theConnectionState) {
            case isConnected:
                buttonScan.setText("Disconnect");
                Toast.makeText(RecActivity.this, "Device connected!", Toast.LENGTH_SHORT).show();
                statusText.setText("Restable now recording...");
                connected = true;
                break;
            case isConnecting:
                Toast.makeText(RecActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
                break;
            case isToScan:
                buttonScan.setText("Connect");
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
        //Append the text into the EditText and print it to to the Scrollview (for debugging purposes)
        serialReceivedText.append(theString);
        ((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);

        //Store the received data in receivedData for ResultsActivity
        receivedData.append(theString);
    }

}
