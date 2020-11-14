package com.example.restable;

import java.util.ArrayList;

public class SleepData {

    ArrayList<Float> humidityData;
    ArrayList<Float> tempData;
    ArrayList<Float> soundData;
    ArrayList<Float> motionData;

    public SleepData(StringBuilder receivedData) {
        humidityData = new ArrayList<>();
        tempData = new ArrayList<>();
        soundData = new ArrayList<>();
        motionData = new ArrayList<>();

        String[] lines = receivedData.toString().split("\n");
        for (String line : lines) {
            switch(line.split(" ")[0]) {
                case "RH:":
                    humidityData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                case "TMP:":
                    tempData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                case "DB:":
                    soundData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                case "DIST:":
                    motionData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                default:
                    break;
            }
        }
    }

    public ArrayList<Float> getHumidityData() {
        return humidityData;
    }

    public ArrayList<Float> getTempData() {
        return tempData;
    }

    public ArrayList<Float> getSoundData() {
        return soundData;
    }

    public ArrayList<Float> getMotionData() {
        return motionData;
    }
}
