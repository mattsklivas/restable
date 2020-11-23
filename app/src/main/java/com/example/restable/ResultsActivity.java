package com.example.restable;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private LineChart humiditychart;
    private LineChart tempchart;
    private LineChart soundchart;
    private LineChart motionchart;

    private String humidity = "Humidity";
    private String temperature = "Temperature";
    private String sound = "Sound";
    private String motion = "Motion";
    private DatabaseReference databaseReference;
    private SleepData sleepData;

    private ArrayList<Float> humidityData;
    private ArrayList<Float> tempData;
    private ArrayList<Float> soundData;
    private ArrayList<Float> motionData;

    private ArrayList<String > timearrayhumidity;
    private ArrayList<String > timearraytemperature;
    private ArrayList<String > timearraysound;
    private ArrayList<String > timearraymotion;

    protected LocalDateTime stopTime;
    protected LocalDateTime startTime;
    protected DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a");

    protected TextView start_Time;
    protected TextView stop_Time;
    protected TextView average_Temp;
    protected TextView average_Humid;
    protected TextView time_Slept;

    protected TextView scoreTot;
    protected TextView scoreH;
    protected TextView scoreT;
    protected TextView scoreM;
    protected TextView scoreS;

    protected Button done_button;
    protected Button save_button;
    protected Button otherReturnButton;

    private Duration duration;

    //protected ConstraintLayout rootLayout;
    //protected AnimationDrawable animDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        // Add animated background gradient
//        rootLayout = (ConsrtaintLayout) findViewById(R.id.results_layout);
//        animDrawable = (AnimationDrawable) rootLayout.getBackground();
//        animDrawable.setEnterFadeDuration(10);
//        animDrawable.setExitFadeDuration(5000);
//        animDrawable.start();

        done_button = findViewById(R.id.done_button);
        save_button = findViewById(R.id.save_button);
        otherReturnButton = findViewById(R.id.return_button);

        start_Time = findViewById(R.id.start_time);
        stop_Time = findViewById(R.id.stop_time);
        average_Temp = findViewById(R.id.average_temp);
        average_Humid = findViewById(R.id.average_humidity);
        time_Slept = findViewById(R.id.time_slept);



        sleepData = (SleepData) getIntent().getSerializableExtra("sleepData");

        if(sleepData == null)
        {
            setContentView(R.layout.activity_no_result);
            Log.d(TAG, "onCreate called");

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

        else{

            humidityData = sleepData.getHumidityData();
            tempData = sleepData.getTempData();
            soundData = sleepData.getSoundData();
            motionData = sleepData.getMotionData();
            startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getStartTime()), ZoneId.systemDefault());
            stopTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sleepData.getEndTime()), ZoneId.systemDefault());



            setContentView(R.layout.activity_results);
            Log.d(TAG, "onCreate called");

            done_button = findViewById(R.id.done_button);
            save_button = findViewById(R.id.save_button);

            start_Time = findViewById(R.id.start_time);
            stop_Time = findViewById(R.id.stop_time);
            average_Temp = findViewById(R.id.average_temp);
            average_Humid = findViewById(R.id.average_humidity);
            time_Slept = findViewById(R.id.time_slept);

            scoreTot =(TextView) findViewById(R.id.scoreTotal);
            scoreH =(TextView) findViewById(R.id.scoreHum);
            scoreT =(TextView) findViewById(R.id.scoreTemp);
            scoreM =(TextView) findViewById(R.id.scoreMot);
            scoreS=(TextView) findViewById(R.id.scoreSound);
            databaseReference = FirebaseDatabase.getInstance().getReference("Sessions");

//            System.out.println("Dummy data if user hasn't connected to the hardware:");
//            System.out.println("tempData:" + tempData);
//            System.out.println("humidityData:" + humidityData);
//            System.out.println("soundData:" + soundData);
//            System.out.println("motionData:" + motionData);

            humiditychart = findViewById(R.id.line_chart_humidity);
            humiditychart.setDrawBorders(true);
            humiditychart.setBorderColor(Color.BLUE);;
            humiditychart.getDescription().setEnabled(false);
            humiditychart.getXAxis().setDrawGridLines(false);
            humiditychart.setDragEnabled(false);
            humiditychart.setScaleEnabled(false);
            humiditychart.getAxisRight().setEnabled(false);
            humiditychart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            humiditychart.getLegend().setEnabled(false);

            tempchart = findViewById(R.id.line_chart_temp);
            tempchart.setDrawBorders(true);
            tempchart.setBorderColor(Color.BLUE);
            tempchart.getDescription().setEnabled(false);
            tempchart.setDragEnabled(false);
            tempchart.setScaleEnabled(false);
            tempchart.getXAxis().setDrawGridLines(false);
            tempchart.getAxisRight().setEnabled(false);
            tempchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            tempchart.getLegend().setEnabled(false);

            soundchart = findViewById(R.id.line_chart_sound);
            soundchart.setDrawBorders(true);
            soundchart.setBorderColor(Color.BLUE);
            soundchart.setDragEnabled(false);
            soundchart.setScaleEnabled(false);
            soundchart.getDescription().setEnabled(false);
            soundchart.getXAxis().setDrawGridLines(false);
            soundchart.getAxisRight().setEnabled(false);
            soundchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            soundchart.getLegend().setEnabled(false);

            motionchart = findViewById(R.id.line_chart_motion);
            motionchart.setDrawBorders(true);
            motionchart.setBorderColor(Color.BLUE);
            motionchart.setDragEnabled(false);
            motionchart.setScaleEnabled(false);
            motionchart.getDescription().setEnabled(false);
            motionchart.getXAxis().setDrawGridLines(false);
            motionchart.getAxisRight().setEnabled(false);
            motionchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            motionchart.getLegend().setEnabled(false);

            timearrayhumidity = PeriodicDateTimeProducer(startTime,stopTime,humidityData.size());
            timearraytemperature = PeriodicDateTimeProducer(startTime,stopTime,tempData.size());
            timearraysound = PeriodicDateTimeProducer(startTime,stopTime,soundData.size());
            timearraymotion = PeriodicDateTimeProducer(startTime,stopTime,motionData.size());

            setData(humidityData,humiditychart,humidity,timearrayhumidity);
            setData(tempData,tempchart,temperature,timearraytemperature);
            setData(soundData,soundchart,sound,timearraysound);
            setData(motionData,motionchart,motion,timearraymotion);

            duration = Duration.between(startTime, stopTime);

            start_Time.setText(String.format("Start Time %s", startTime.format(DATE_TIME_FORMATTER)));
            stop_Time.setText(String.format("Stop Time %s", stopTime.format(DATE_TIME_FORMATTER)));
            average_Temp.setText(String.format("Average Temperature (°C): %s", calculateAverage(tempData)));
            average_Humid.setText(String.format("Average Humidity (RH %%): %s", calculateAverage(humidityData)));
            time_Slept.setText(String.format(Locale.getDefault(), "Time Slept: %d Hours %d Minutes", duration.toHours(), duration.toMinutes()));

            score();

//            scoreTot.setText(score("total"));
//            scoreH.setText(score("humidity"));
//            scoreT.setText(score("temperature"));
//            scoreS.setText(score("sound"));
//            scoreM.setText(score("motion"));





            //score(humidityData, tempData,soundData,motionData,scoreTot,scoreH,scoreT,scoreM,scoreS);


            //Setup doneButton
            done_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "done_button onClick called");
                    Intent main_intent = new Intent(ResultsActivity.this, MainActivity.class);
                    Log.i(TAG, "Starting MainActivity");
                    startActivity(main_intent);
                }
            });

            //Setup saveButton
            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Log.d(TAG, "save_button onClick called");
                    Log.i(TAG, "saving to firebase database");

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

    protected void setData(ArrayList<Float> data, LineChart chart, String name,ArrayList<String> TimeArray){
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (int x = 1; x < data.size()-1; x++)
        {
                dataVals.add(new Entry(x, data.get(x)));
        }



        LineDataSet lineDataSet = new LineDataSet(dataVals, name + " Data Set");
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.GRAY);
        lineDataSet.setDrawCircles(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(5,true);
        xAxis.setTextSize(5);
        xAxis.setValueFormatter(new MyXAxisValueformatter(TimeArray));
        //xAxis.setValueFormatter(new MyXAxisValueformatter(formattedStartDateTime));  // start,end,initial,final value

        LineData linedata = new LineData(dataSets);

        chart.setData(linedata);
        chart.invalidate();
    }

    private String calculateAverage(ArrayList <Float> marks) {
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

    @Override
    public void onBackPressed() {
        Context context = getApplicationContext();
        CharSequence text = "Returning isn't permitted";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private ArrayList<String> PeriodicDateTimeProducer(LocalDateTime start, LocalDateTime end, int num_cuts) {
        ArrayList<String> results = new ArrayList<String>(num_cuts);
        long duration = Duration.between(start, end).getSeconds();
        long delta = duration/(num_cuts-1);

        for (int x = 0; x < num_cuts; x++)
        {

            results.add(start.plusSeconds(x*delta).format(DATE_TIME_FORMATTER));
        }

        return results;

    }
    //scoreTot,scoreH,scoreT,scoreM,scoreS
    //private void score(ArrayList <Float>humidityData,ArrayList <Float>tempData,ArrayList <Float>soundData,ArrayList <Float>motionData,TextView scoreTot,TextView scoreH,TextView scoreT,TextView scoreM,TextView scoreS){
   // private  String score(String string){
    private void score(){
        float score, scrHum,scrTmp,scrSound,scrMotion;
        float avg_hum=calculateAveragefloat(humidityData);
        float avg_temp=calculateAveragefloat(tempData);
        float avgSound= calculateAveragefloat(soundData);
        float avgMotion=calculateAveragefloat(motionData);
        float spikeSound=0;
        float spikeOverTotalpoint;


        for (int i=0;i<soundData.size();i++){
            if(soundData.get(i)>65){spikeSound++;}
        }

    spikeOverTotalpoint = spikeSound / soundData.size();//calculate the number of t

        System.out.println("Suyash test avghum "+avg_hum+" avg temp "+avg_temp+" avg motion "+avgMotion+" avgSound "+avgSound+" spike percentage "+spikeOverTotalpoint );

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
        if (40<=avg_hum && avg_hum<=50){scrHum=(float)2.5;}
        else if ( 30<=avg_hum && avg_hum<=60){scrHum=(float)2.0;;}
        else if ( 25<=avg_hum && avg_hum<=62){scrHum=(float)1.0;}
        else{scrHum=(float)0;}
/*The best bedroom temperature for sleep is approximately 65 degrees Fahrenheit (18.3 degrees Celsius).
This may vary by a few degrees from person to person, but most doctors recommend keeping the thermostat set between 60 to 67 degrees
Fahrenheit (15.6 to 19.4 degrees Celsius) for the most comfortable sleep.
https://www.sleepfoundation.org/bedroom-environment/best-temperature-for-sleep#:~:text=The%20best%20bedroom%20temperature%20for,for%20the%20most%20comfortable%20sleep.
 */
        if (17<=avg_temp && avg_temp<=18.5){scrTmp=(float)2.5;}
        else if ( 15<=avg_temp && avg_temp <=19.5){scrTmp=(float)2.0;}
        else if ( 12<=avg_temp && avg_temp<=24){scrTmp=(float)1.0;}
        else{scrTmp=(float)0;}

        if (0.0<=spikeOverTotalpoint&& 0.05<=spikeOverTotalpoint){scrSound=(float)2.5;}
        else if (0.05<=spikeOverTotalpoint&& 0.15<=spikeOverTotalpoint){scrSound=(float)2.0;}
        else if (0.15<=spikeOverTotalpoint&& 0.40<=spikeOverTotalpoint){scrSound=(float)1.0;}
        else {scrSound=(float)0;}

        if (0<=avgMotion&& 100<=avgMotion) {scrMotion=(float)2.5;}
    else if (-1<=avgMotion&& 120<=avgMotion){scrMotion=(float)2.0;}
    else if (-5<=avgMotion&& 1000<=avgMotion){scrMotion=(float)1.0;}
    else {scrMotion=(float)0;}


        score=scrHum+scrTmp+scrSound+scrMotion;
        //create textview and set text. also add to database
        // string fromat is not necessary
        System.out.println("Total score: "+ score+"Humidity score: "+scrHum+"Temperature score: "+scrTmp+"Sound score: "+scrSound+"Motion score: "+scrMotion);

//        if(string=="total"){return "Total score: "+ score;}
//        else if (string=="humidity"){return "Humidity score: "+ scrHum;}
//        else if (string=="temperature"){return"Temperature score: "+scrTmp;}
//        else if (string=="sound"){return "Sound score: "+ scrSound;}
//        else if (string=="motion"){return "Motion score: "+scrMotion;}
//        return "ERROR";

        scoreTot.setText("Total score: "+ score);
        scoreH.setText("Humidity score: "+ scrHum);
        scoreT.setText("Temperature score: "+scrTmp);
        scoreS.setText("Sound score: "+ scrSound);
        scoreM.setText("Motion score: "+scrMotion);

    }

    private float calculateAveragefloat(ArrayList <Float> marks) {
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
   private class MyXAxisValueformatter implements IAxisValueFormatter {

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