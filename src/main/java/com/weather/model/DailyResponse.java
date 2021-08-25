package com.weather.model;

public class DailyResponse {
    private String iconB64;
    private String max;
    private String min;
    private String date;

    public DailyResponse(String iconB64, String max, String min, String date) {
        this.iconB64 = iconB64;
        this.max = max;
        this.min = min;
        this.date = date;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getIconB64() {
        return iconB64;
    }

    public void setIconB64(String iconB64) {
        this.iconB64 = iconB64;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}
