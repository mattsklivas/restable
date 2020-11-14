package com.example.restable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class SleepData implements Serializable {

    private ArrayList<Float> humidityData;
    private ArrayList<Float> tempData;
    private ArrayList<Float> soundData;
    private ArrayList<Float> motionData;
    private Map time;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    public SleepData() {

    }

    public SleepData(ArrayList<Float> humidityData, ArrayList<Float> tempData, ArrayList<Float> soundData, ArrayList<Float> motionData, Map time, LocalDateTime startTime, LocalDateTime endTime) {
        this.humidityData = humidityData;
        this.tempData = tempData;
        this.soundData = soundData;
        this.motionData = motionData;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SleepData(StringBuilder receivedData, Map time, LocalDateTime startTime, LocalDateTime endTime) {
        humidityData = new ArrayList<>();
        tempData = new ArrayList<>();
        soundData = new ArrayList<>();
        motionData = new ArrayList<>();
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
