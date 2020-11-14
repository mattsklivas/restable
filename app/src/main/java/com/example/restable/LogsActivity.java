package com.example.restable;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LogsActivity extends AppCompatActivity {

    private String userID;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    ArrayList<SleepData> sleepSessions;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        /* Insert app bar and enable back button to MainActivity */
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Sessions").child(userID);


    }
}
