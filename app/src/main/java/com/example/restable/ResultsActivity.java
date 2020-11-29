package com.example.restable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.color.MaterialColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";
    protected DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a");

    //Defining the four Line Chart.
    protected LineChart humidityChart, tempChart , soundChart, motionChart;

    //Defining the four strings thats needed for the labeling of the charts.
    protected String humidity = "Humidity", temperature = "Temperature", sound = "Sound", motion = "Motion";

    protected DatabaseReference databaseReference;
    protected SleepData sleepData;

    //Defining the ArrayList which we will be storing the importing data.
    protected ArrayList<Float> humidityData, tempData, soundData, motionData;

    //Defining the ArrayList which we will be storing the formatted time for each charts.
    protected ArrayList<String > timeArray;

    //Defining The start and stop time LocalDateTime Oojects.
    protected LocalDateTime stopTime, startTime;

    //Defining TextView of activity_results.xml
    protected TextView start_Time, stop_Time , average_Temp, average_Humid, time_Slept, scoreTot, scoreH , scoreT, scoreM, scoreS,
                        recTitle, humidTitle, tempTitle, soundTitle, motionTitle;

    //Defining ImageView for optimal temperature/humidity conditions
    protected ImageView condImage;

    // Defining the EditText for the notes
    protected EditText notes;

    //Defining Button of activity_results.xml
    protected Button done_button, save_button, otherReturnButton;

    //Defining Duration to find out how long a person has slept.
    protected Duration duration;

    //SharedPreference for setting theme
    SharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()) {
            setTheme(R.style.NightTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        //Linking the Buttons to the activity_results.xml ids
        done_button = findViewById(R.id.done_button);
        save_button = findViewById(R.id.save_button);
        otherReturnButton = findViewById(R.id.return_button);

        //Linking the TextView to the activity_results.xml ids
        start_Time = findViewById(R.id.start_time);
        stop_Time = findViewById(R.id.stop_time);
        average_Temp = findViewById(R.id.average_temp);
        average_Humid = findViewById(R.id.average_humidity);
        time_Slept = findViewById(R.id.time_slept);

        sleepData = (SleepData) getIntent().getSerializableExtra("sleepData");

        //If there is no data found from sleep data, This will send the user to the no result activity xml page.
        if(sleepData == null)
        {
            setContentView(R.layout.activity_no_result);
            Log.d(TAG, "Activity No Result called");

            //Other Return Button defined  in this page for the user to go back to Rec Activity.
            otherReturnButton = findViewById(R.id.return_button);

            otherReturnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "other_done_button onClick called");
                    Intent intent = new Intent(ResultsActivity.this, RecActivity.class);
                    startActivity(intent);
                }
            });
        }

        //otherwise proceeds on filling up all the charts and ArrayList with the necessary data
        else{

            setContentView(R.layout.activity_results);
            Log.d(TAG, "onCreate called");

            // Linking the EditText to the activity_results.xml id
            notes = (EditText) findViewById(R.id.notesText);

            humidityData = sleepData.getHumidityData();
            tempData = sleepData.getTempData();
            soundData = sleepData.getSoundData();
            motionData = sleepData.getMotionData();
            startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getStartTime()), ZoneId.systemDefault());
            stopTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getEndTime()), ZoneId.systemDefault());

            done_button = findViewById(R.id.done_button);
            save_button = findViewById(R.id.save_button);

            start_Time = findViewById(R.id.start_time);
            stop_Time = findViewById(R.id.stop_time);
            average_Temp = findViewById(R.id.average_temp);
            average_Humid = findViewById(R.id.average_humidity);
            time_Slept = findViewById(R.id.time_slept);
            recTitle = findViewById(R.id.recTitle);
            humidTitle = findViewById(R.id.humidTitle);
            tempTitle = findViewById(R.id.tempTitle);
            motionTitle = findViewById(R.id.motionTitle);
            soundTitle = findViewById(R.id.soundTitle);

            //Setup the optimal temp/humidity conditions icon
            condImage = findViewById(R.id.imageOptCond);
            condImage.setAlpha(0.9f);
            if(sharedpref.loadNightModeState()) {
                condImage.setImageResource(R.drawable.opt_cond_alt);
            }
            else {
                condImage.setImageResource(R.drawable.opt_cond);
            }

            scoreTot =(TextView) findViewById(R.id.scoreTotal);
            //scoreH =(TextView) findViewById(R.id.scoreHum);
            //scoreT =(TextView) findViewById(R.id.scoreTemp);
            //scoreM =(TextView) findViewById(R.id.scoreMot);
            //scoreS=(TextView) findViewById(R.id.scoreSound);
            databaseReference = FirebaseDatabase.getInstance().getReference("Sessions");

            humidityChart = findViewById(R.id.line_chart_humidity);
            tempChart = findViewById(R.id.line_chart_temp);
            soundChart = findViewById(R.id.line_chart_sound);
            motionChart = findViewById(R.id.line_chart_motion);

            timeArray = PeriodicDateTimeProducer(startTime,stopTime,humidityData.size());

            setData(humidityData,humidityChart,humidity,timeArray);
            setData(tempData,tempChart,temperature,timeArray);
            setData(soundData,soundChart,sound,timeArray);
            setData(motionData,motionChart,motion,timeArray);

            humidityChart = findViewById(R.id.line_chart_humidity);
            tempChart = findViewById(R.id.line_chart_temp);
            soundChart = findViewById(R.id.line_chart_sound);
            motionChart = findViewById(R.id.line_chart_motion);

            configureGraph(humidityChart);
            configureGraph(tempChart);
            configureGraph(soundChart);
            configureGraph(motionChart);

            timeArray = PeriodicDateTimeProducer(startTime,stopTime,humidityData.size());

            setData(humidityData,humidityChart,humidity,timeArray);
            setData(tempData,tempChart,temperature,timeArray);
            setData(soundData,soundChart,sound,timeArray);
            setData(motionData,motionChart,motion,timeArray);

            duration = Duration.between(startTime, stopTime);


            start_Time.setText(String.format("Start Time %s", startTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
            stop_Time.setText(String.format("Stop Time %s", stopTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
            average_Temp.setText(String.format("Average\nTemperature: %1$s%2$s", calculateAverage(tempData),"°C"));
            average_Humid.setText(String.format("Average\nHumidity: %1$s%2$s", calculateAverage(humidityData), "%"));
            time_Slept.setText(String.format(Locale.getDefault(), "Time Slept: %d Hours %d Minutes", duration.toHours(), duration.toMinutes()));

            score();

        }

        //Setup doneButton
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "done_button onClick called");
                Intent main_intent = new Intent(ResultsActivity.this, MainActivity.class);
                Log.i(TAG, "Starting MainActivity");
                startActivity(main_intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //Setup saveButton
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "save_button onClick called");
                Log.i(TAG, "saving to firebase database");

                // Extract notes text to be saved and add to SleepData
                String notesText = notes.getText().toString();
                sleepData.setNotes(notesText);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String owner = user.getUid();
                DatabaseReference dbRefPush = databaseReference.child(owner).push();
                dbRefPush.setValue(sleepData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!
                                Log.i(TAG, "Write to firebase database successful");
                                //Store the ArrayLists in the Intent
                                Intent intent = new Intent(v.getContext(), MainActivity.class);
                                Log.i(TAG, "Starting MainActivity");
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                Log.e(TAG, "Write to firebase database failed");
                                Toast.makeText(ResultsActivity.this, "Database write failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    //Configuration of each Chart on the activity.
    protected  void configureGraph(LineChart chart){

        // Get theme color
        @SuppressLint("RestrictedApi")
        int themeColor = MaterialColors.getColor(ResultsActivity.this, R.attr.colorMain, Color.BLACK);

        chart.setDrawBorders(true);
        chart.setBorderColor(themeColor);
        chart.getXAxis().setTextColor(themeColor);
        chart.setExtraRightOffset(20.0f);
        chart.getXAxis().setYOffset(5.0f);
        chart.getAxisLeft().setTextColor(themeColor);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getLegend().setEnabled(false);

    }

    //Configuration of Data going into the chart using LineData Set.
    protected void setData(ArrayList<Float> data, LineChart chart, String name,ArrayList<String> TimeArray){
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (int x = 1; x < data.size()-1; x++)
        {
                dataVals.add(new Entry(x, data.get(x)));
        }

        LineDataSet lineDataSet = new LineDataSet(dataVals, name + " Data Set");
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);

        // Get theme color
        @SuppressLint("RestrictedApi")
        int themeColor = MaterialColors.getColor(ResultsActivity.this, R.attr.colorMain, Color.BLACK);

        lineDataSet.setColor(themeColor);
        lineDataSet.setDrawCircles(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(6,true);
        xAxis.setTextSize(8);
        xAxis.setValueFormatter(new MyXAxisValueformatter(TimeArray));

        LineData linedata = new LineData(dataSets);

        chart.setData(linedata);
        chart.invalidate();
    }

    //Calculates the Average of data
    protected String calculateAverage(ArrayList <Float> marks) {
        Float sum = (float) 0;
        if(!marks.isEmpty()) {
            float average;
            for (Float mark : marks) {
                sum += mark;
            }
            average = sum / marks.size();
            return String.format(Locale.getDefault(), "%.2f", average);
        }
        return "0";
    }

    //Prevents user to go back from Result Activity.
    @Override
    public void onBackPressed() {
        Context context = getApplicationContext();
        CharSequence text = "Returning isn't permitted";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Creates ArrayList<Strings of the timestamp
    protected ArrayList<String> PeriodicDateTimeProducer(LocalDateTime start, LocalDateTime end, int num_cuts) {

        ArrayList<String> results = new ArrayList<String>(num_cuts);
        long duration = Duration.between(start, end).getSeconds();
        long delta = duration/(num_cuts-1);

        for (int x = 0; x < num_cuts; x++)
        {

            results.add(start.plusSeconds(x*delta).format(DATE_TIME_FORMATTER));
        }

        return results;

    }

    protected void score(){

        float score, scrHum,scrTmp,scrSound,scrMotion;
        float avg_hum = calculateAveragefloat(humidityData);
        float avg_temp = calculateAveragefloat(tempData);
        float avgSound = calculateAveragefloat(soundData);
        float avgMotion = calculateAveragefloat(motionData);
        float spikeSound = 0;
        float spikeMotion = 0;
        float spikeOverTotalSoundPoint;
        float spikeOverTotalMotionPoint;


        for (int i=0;i<soundData.size();i++){

            if(soundData.get(i)>65) spikeSound++;

        }

        spikeOverTotalSoundPoint = spikeSound / soundData.size();//calculate the number of t

        for (int i=0;i<motionData.size();i++) if(motionData.get(i)>50) spikeMotion++;

        spikeOverTotalMotionPoint = spikeMotion / motionData.size();//calculate the number of t

        System.out.println("Suyash test avghum "+avg_hum+" avg temp "+avg_temp+" avg motion "+avgMotion+" avgSound "+avgSound+" spike percentage sound"+spikeOverTotalSoundPoint +" spike percentage motion "+spikeOverTotalMotionPoint);

        /*The ideal humidity for sleep is between 30 and 50 percent.1 Anything higher (which is common during the summer in many parts of the country)
        can make it difficult to sleep for two reasons: comfort and congestion. High humidity prevents moisture from evaporating
        off your body, which can make you hot and sweaty. This might mean hours of tossing, turning, and flipping the pillow to find the coolest spots on the bed, which is never a good way to sleep.2
        https://www.breatheright.com/causes-of-congestion/how-humidity-affects-sleep.html#:~:text=The%20ideal%20humidity%20for%20sleep,make%20you%20hot%20and%20sweaty.
         */

        /*The best relative humidity for sleeping and other indoor activities has been debated.
        According to the Environmental Protection Agency, the best indoor relative humidity falls between 30% and 50%,
        and it should never exceed 60%. Other studies suggest 40% to 60% is a better range. Regardless,
         60% seems to be the agreed-upon threshold for indoor humidity.
         https://www.sleepfoundation.org/bedroom-environment/humidity-and-sleep
         */

        /*The best bedroom temperature for sleep is approximately 65 degrees Fahrenheit (18.3 degrees Celsius).
        This may vary by a few degrees from person to person, but most doctors recommend keeping the thermostat set between 60 to 67 degrees
        Fahrenheit (15.6 to 19.4 degrees Celsius) for the most comfortable sleep.
        https://www.sleepfoundation.org/bedroom-environment/best-temperature-for-sleep#:~:text=The%20best%20bedroom%20temperature%20for,for%20the%20most%20comfortable%20sleep.
         */

        if (40<=avg_hum && avg_hum<=50)     scrHum=(float)2.5;
        else if ( 30<=avg_hum && avg_hum<=60)   scrHum=(float)2.0;
        else if ( 25<=avg_hum && avg_hum<=62)   scrHum=(float)1.0;
        else scrHum=(float)0;

        if (17<=avg_temp && avg_temp<=18.5)     scrTmp=(float)2.5;
        else if ( 15<=avg_temp && avg_temp <=19.5)      scrTmp=(float)2.0;
        else if ( 12<=avg_temp && avg_temp<=24)     scrTmp=(float)1.0;
        else scrTmp=(float)0;

        if (0.0<=spikeOverTotalSoundPoint&& 0.05<=spikeOverTotalSoundPoint)     scrSound=(float)2.5;
        else if (0.05<=spikeOverTotalSoundPoint&& 0.15<=spikeOverTotalSoundPoint)   scrSound=(float)2.0;
        else if (0.15<=spikeOverTotalSoundPoint&& 0.40<=spikeOverTotalSoundPoint)   scrSound=(float)1.0;
        else {scrSound=(float)0;}

        if (0.0<=spikeOverTotalMotionPoint&& 0.01<=spikeOverTotalMotionPoint) {scrMotion=(float)2.5;}
        else if (0.0<=spikeOverTotalMotionPoint&& 0.03<=spikeOverTotalMotionPoint){scrMotion=(float)2.0;}
        else if (0.03<=spikeOverTotalMotionPoint&& 0.20<=spikeOverTotalMotionPoint){scrMotion=(float)1.0;}
        else scrMotion=(float)0;


        score=scrHum+scrTmp+scrSound+scrMotion;
        System.out.println("Total Score: "+ score+"Humidity score: "+scrHum+"Temperature score: "+scrTmp+"Sound score: "+scrSound+"Motion score: "+scrMotion);

        scoreTot.setText(String.format("Total Score: %1$s%2$s", score, "/10"));
        //scoreH.setText("Humidity score: "+ scrHum);
        //scoreT.setText("Temperature score: "+scrTmp);
        //scoreS.setText("Sound score: "+ scrSound);
        //scoreM.setText("Motion score: "+scrMotion);

    }

    //Calculates the average of a data set.
    protected float calculateAveragefloat(ArrayList <Float> marks) {
        Float sum = (float) 0;
        if(!marks.isEmpty()) {
            float average;
            for (Float mark : marks) {
                sum += mark;
            }
            average = sum / marks.size();
            return average;
        }
        return 0;
    }

    //Changes the X Axis format from data number to time format.
   protected class MyXAxisValueformatter implements IAxisValueFormatter {

        private ArrayList<String> timearray;

        MyXAxisValueformatter(ArrayList<String> timearray){
            super();
            this.timearray = timearray;
        }


       @Override
       public String getFormattedValue(float value, AxisBase axis){
            return timearray.get((int)value);
        }
    }
}