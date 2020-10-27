package com.example.restable;

import java.time.LocalDateTime;

public class AlarmTime {
    private LocalDateTime wakeTime;
    private String duration;
    private String rating;

    AlarmTime(LocalDateTime wakeTime, String duration, String rating) {
        this.wakeTime = wakeTime;
        this.duration = duration;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "AlarmTime{" +
                "wakeTime=" + wakeTime +
                ", duration='" + duration + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }

    public LocalDateTime getWakeTime() {
        return wakeTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getRating() {
        return rating;
    }

}

