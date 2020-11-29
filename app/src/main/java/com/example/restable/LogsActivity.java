package com.example.restable;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    private static final String TAG = "LogsActivity";
    private String userID;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private ArrayList<SleepData> sleepSessions;
    private ArrayList<String> sleepLogKeys;
    protected ListView listView;
    protected LogsListViewAdapter adapter;
    protected ProgressBar progressBar;
    protected TextView noSessions;
    protected ConstraintLayout rootLayout;
    protected AnimationDrawable animDrawable;

    //SharedPreference for setting theme
    SharedPref sharedpref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.NightTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        Log.d(TAG, "onCreate called");

        // Add animated background gradient
        rootLayout = findViewById(R.id.logs_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();

        // Add custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_log);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Text saying no sleep sessions saved
        noSessions = findViewById(R.id.noSessionsTextView);

        // Progress bar
        progressBar = findViewById(R.id.progressBarLogs);

        // Get sleep sessions from db to be displayed in list view
        sleepSessions = new ArrayList<>();
        sleepLogKeys = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userID = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Sessions").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    sleepSessions.add(d.getValue(SleepData.class));
                    sleepLogKeys.add(d.getKey());
                    Log.i(TAG, "Loaded " + d.getKey() + " from database");
                }
                if (sleepSessions.size() == 0)
                    noSessions.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                configureListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Read from firebase database failed");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LogsActivity.this, "Error reading database. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    // Configure and display the ListView
    private void configureListView() {
        Log.d(TAG, "configureListView called");

        listView = findViewById(R.id.logsListView);
        adapter = new LogsListViewAdapter(this, R.layout.logs_list, sleepSessions);
        listView.setAdapter(adapter); // Populate ListView

        // Handle pressing on a ListView item to view sleep session
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick for clicking ListView item " + position);

                // Get time information
                SleepData sleepData = sleepSessions.get(position);
                String key = sleepLogKeys.get(position);
                Intent intent = new Intent(LogsActivity.this, ViewLogActivity.class);
                intent.putExtra("sleepData", sleepData);
                intent.putExtra("key", key);
                Log.i(TAG, "Starting ViewLogActivity");
                startActivity(intent);
            }
        });
    }


}
