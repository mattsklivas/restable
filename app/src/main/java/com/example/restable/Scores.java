package com.example.restable;

import java.io.Serializable;
import java.util.ArrayList;

public class Scores implements Serializable {

    private final float scrHum;
    private final float scrTmp;
    private final float scrSound;
    private final float scrMotion;
    private final float score;

    public Scores(SleepData sleepData) {
        ArrayList<Float> humidityData = sleepData.getHumidityData();
        ArrayList<Float> tempData = sleepData.getTempData();
        ArrayList<Float> soundData = sleepData.getSoundData();
        ArrayList<Float> motionData = sleepData.getMotionData();

        float avg_hum = calculateAveragefloat(humidityData);
        float avg_temp = calculateAveragefloat(tempData);
        float avgSound = calculateAveragefloat(soundData);
        float avgMotion = calculateAveragefloat(motionData);
        float spikeSound = 0;
        float spikeMotion = 0;
        float spikeOverTotalSoundPoint;
        float spikeOverTotalMotionPoint;

        for (int i = 0; i < soundData.size(); i++) {

            if (soundData.get(i) > 65) spikeSound++;

        }

        spikeOverTotalSoundPoint = spikeSound / soundData.size();//calculate the number of t

        for (int i = 0; i < motionData.size(); i++) if (motionData.get(i) > 50) spikeMotion++;

        spikeOverTotalMotionPoint = spikeMotion / motionData.size();//calculate the number of t

        System.out.println("Suyash test avghum " + avg_hum + " avg temp " + avg_temp + " avg motion " + avgMotion + " avgSound " + avgSound + " spike percentage sound" + spikeOverTotalSoundPoint + " spike percentage motion " + spikeOverTotalMotionPoint);

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

        if (40 <= avg_hum && avg_hum <= 50) scrHum = (float) 2.5;
        else if (30 <= avg_hum && avg_hum <= 60) scrHum = (float) 2.0;
        else if (25 <= avg_hum && avg_hum <= 62) scrHum = (float) 1.0;
        else scrHum = (float) 0;

        if (17 <= avg_temp && avg_temp <= 18.5) scrTmp = (float) 2.5;
        else if (15 <= avg_temp && avg_temp <= 19.5) scrTmp = (float) 2.0;
        else if (12 <= avg_temp && avg_temp <= 24) scrTmp = (float) 1.0;
        else scrTmp = (float) 0;

        if (0.0 <= spikeOverTotalSoundPoint && 0.05 <= spikeOverTotalSoundPoint)
            scrSound = (float) 2.5;
        else if (0.05 <= spikeOverTotalSoundPoint && 0.15 <= spikeOverTotalSoundPoint)
            scrSound = (float) 2.0;
        else if (0.15 <= spikeOverTotalSoundPoint && 0.40 <= spikeOverTotalSoundPoint)
            scrSound = (float) 1.0;
        else {
            scrSound = (float) 0;
        }

        if (0.0 <= spikeOverTotalMotionPoint && 0.01 <= spikeOverTotalMotionPoint) {
            scrMotion = (float) 2.5;
        } else if (0.0 <= spikeOverTotalMotionPoint && 0.03 <= spikeOverTotalMotionPoint) {
            scrMotion = (float) 2.0;
        } else if (0.03 <= spikeOverTotalMotionPoint && 0.20 <= spikeOverTotalMotionPoint) {
            scrMotion = (float) 1.0;
        } else scrMotion = (float) 0;

        score = scrHum + scrTmp + scrSound + scrMotion;
    }

    //Calculates the average of a data set.
    private float calculateAveragefloat(ArrayList<Float> marks) {
        Float sum = (float) 0;
        if (!marks.isEmpty()) {
            float average;
            for (Float mark : marks) {
                sum += mark;
            }
            average = sum / marks.size();
            return average;
        }
        return 0;
    }

    public float getScrHum() {
        return scrHum;
    }

    public float getScrTmp() {
        return scrTmp;
    }

    public float getScrSound() {
        return scrSound;
    }

    public float getScrMotion() {
        return scrMotion;
    }

    public float getScore() {
        return score;
    }
}
