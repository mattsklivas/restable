package com.example.restable;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.PopupWindowCompat;

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
    protected ConstraintLayout rootLayout;
    protected AnimationDrawable animDrawable;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = LocalDateTime.now();
        super.onCreate(savedInstanceState);

        // Make activity fullscreen and set the content view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rec);
        Log.d(TAG, "onCreate called");

        // Hide system UI when visibility change is detected
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            fullScreenImmersive();
                        }
                    }
                });

        // Add custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add animated background gradient
        rootLayout = (ConstraintLayout) findViewById(R.id.rec_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

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
                //Store dummy sensor data in the ArrayLists so developers without access to hardware can work on the app
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
                Log.i(TAG, "starting ResultsActivity");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    //Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rec, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Turn off the screen display while recording sleep activity data when clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Make RecActivity background black
        View view = findViewById(R.id.rec_layout);
        view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_bg, null));

        //Setup the PopupWindow
        LinearLayout popupView = (LinearLayout) LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.rec_screen_overlay, null);
        popupView.setGravity(Gravity.CENTER);
        popupView.setBackgroundColor(Color.BLACK);
        int windowWidth = ViewGroup.LayoutParams.MATCH_PARENT;;
        int windowHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        mPopupWindow = new PopupWindow(popupView, windowWidth, windowHeight);
        mPopupWindow.setFocusable(false);

        //Display the PopupWindow
        showPopup(findViewById(R.id.rec_layout));

        //Handler for clicking the PopupWindow
        //Close PopupWindow and restore background gradient
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPopupWindow.dismiss();
                //Reset RecActivity gradient background
                View view = findViewById(R.id.rec_layout);
                view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_animation, null));
                return true;
            }
        });

        //Display toast message notifying the user that the screen has been turned off
        Toast.makeText(RecActivity.this, "Screen turned off. Tap again to view app.", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    //Enable fullscreen immersive mode if window focus has changed
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fullScreenImmersive();
        }
    }

    //Display the PopupWindow
    public void showPopup(View anchor) {
        mPopupWindow.setFocusable(false);
        mPopupWindow.update();

        PopupWindowCompat.showAsDropDown(mPopupWindow, anchor,
                -mPopupWindow.getWidth() / 2 + anchor.getWidth() / 2,
                -mPopupWindow.getHeight() - anchor.getHeight(), Gravity.CENTER);

        fullScreenImmersive();
        mPopupWindow.update();
    }

    // Method used to hide the system UI using a View object
    private void fullScreenImmersive(View view) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Method used to hide the system UI
    private void fullScreenImmersive() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
