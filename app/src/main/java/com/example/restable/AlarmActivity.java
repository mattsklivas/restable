package com.example.restable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity  extends AppCompatActivity {

    //Instance variables
    protected Button recButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //Setup activity views
        recButton = findViewById(R.id.buttonRec);

        //Setup recButton
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRecActivity();
            }
        });
    }

    //Go to RecActivity
    protected void goToRecActivity() {
        Intent intent = new Intent(this, RecActivity.class);
        startActivity(intent);
    }
}
