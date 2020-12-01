package com.example.restable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;

public class SleepData implements Serializable {

    private ArrayList<Float> humidityData;
    private ArrayList<Float> tempData;
    private ArrayList<Float> soundData;
    private ArrayList<Float> motionTemp;
    private ArrayList<Float> motionData;
    private long startTime;
    private long endTime;
    private String notes;

    public SleepData() {
        this.humidityData = null;
        this.tempData = null;
        this.soundData = null;
        this.motionData = null;
    }

    public SleepData(ArrayList<Float> humidityData, ArrayList<Float> tempData, ArrayList<Float> soundData, ArrayList<Float> motionData, LocalDateTime startTime, LocalDateTime endTime) {
        this.humidityData = humidityData;
        this.tempData = tempData;
        this.soundData = soundData;
        this.motionData = motionData;

        ZoneId zoneId = ZoneId.systemDefault();
        this.startTime = startTime.atZone(zoneId).toEpochSecond();
        this.endTime = endTime.atZone(zoneId).toEpochSecond();
    }

    public SleepData(StringBuilder receivedData, LocalDateTime startTime, LocalDateTime endTime) {
        humidityData = new ArrayList<>();
        tempData = new ArrayList<>();
        soundData = new ArrayList<>();
        motionData = new ArrayList<>();

        ZoneId zoneId = ZoneId.systemDefault();
        this.startTime = startTime.atZone(zoneId).toEpochSecond();
        this.endTime = endTime.atZone(zoneId).toEpochSecond();

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

        // Prevent out of range data from being included in motionData
        motionTemp = new ArrayList<>();
        for (float val : motionData) {
            if (val != 0) {
                motionTemp.add(val);
            }
        }

        motionData = motionTemp;
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

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
