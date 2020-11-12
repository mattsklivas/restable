package com.example.restable;

import java.util.ArrayList;
import java.util.Map;

public class SleepData {

    public ArrayList<Float> humidityData;
    public ArrayList<Float> tempData;
    public ArrayList<Float> soundData;
    public ArrayList<Float> motionData;
    public Map time;

    public SleepData() {

    }

    public SleepData(ArrayList<Float> humidityData, ArrayList<Float> tempData, ArrayList<Float> soundData, ArrayList<Float> motionData, Map time) {
        this.humidityData = humidityData;
        this.tempData = tempData;
        this.soundData = soundData;
        this.motionData = motionData;
        this.time = time;
    }

    public SleepData(StringBuilder receivedData, Map time) {
        humidityData = new ArrayList<>();
        tempData = new ArrayList<>();
        soundData = new ArrayList<>();
        motionData = new ArrayList<>();
        this.time = time;

        String[] lines = receivedData.toString().split("\n");
            for (String line : lines) {
            switch(line.split(" ")[0]) {
                case "RH:":
                    humidityData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                case "TMP:":
                    tempData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                case "SOUND:":
                    soundData.add(Float.parseFloat(line.split(" ")[1]));
                    break;
                case "PIR:":
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

    public Map getTime() {
        return time;
    }
}
