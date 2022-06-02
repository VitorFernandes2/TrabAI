package com.isec.trabai.model.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SensorData {

    private String sessionId;
    private String lat;
    private String lng;
    private String alt;
    private String speed;
    private String accuracy;
    private String bearing;
    private String timestamp;
    private String xAcc;
    private String yAcc;
    private String zAcc;
    private String xGyro;
    private String yGyro;
    private String zGyro;
    private String sensorN;
    private String activity;

    SensorData(final String sessionId,
               final String lat,
               final String lng,
               final String alt,
               final String speed,
               final String accuracy,
               final String bearing,
               final String timestamp,
               final String xAcc,
               final String yAcc,
               final String zAcc,
               final String xGyro,
               final String yGyro,
               final String zGyro,
               final String sensorN,
               final String activity) {
        this.sessionId = sessionId;
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
        this.speed = speed;
        this.accuracy = accuracy;
        this.bearing = bearing;
        this.timestamp = timestamp;
        this.xAcc = xAcc;
        this.yAcc = yAcc;
        this.zAcc = zAcc;
        this.xGyro = xGyro;
        this.yGyro = yGyro;
        this.zGyro = zGyro;
        this.sensorN = sensorN;
        this.activity = activity;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getxAcc() {
        return xAcc;
    }

    public void setxAcc(String xAcc) {
        this.xAcc = xAcc;
    }

    public String getyAcc() {
        return yAcc;
    }

    public void setyAcc(String yAcc) {
        this.yAcc = yAcc;
    }

    public String getzAcc() {
        return zAcc;
    }

    public void setzAcc(String zAcc) {
        this.zAcc = zAcc;
    }

    public String getxGyro() {
        return xGyro;
    }

    public void setxGyro(String xGyro) {
        this.xGyro = xGyro;
    }

    public String getyGyro() {
        return yGyro;
    }

    public void setyGyro(String yGyro) {
        this.yGyro = yGyro;
    }

    public String getzGyro() {
        return zGyro;
    }

    public void setzGyro(String zGyro) {
        this.zGyro = zGyro;
    }

    public String getSensorN() {
        return sensorN;
    }

    public void setSensorN(String sensorN) {
        this.sensorN = sensorN;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    //TODO: see if it has a logic problem
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SensorData) {
            SensorData sd = (SensorData) obj;
            return (this.essentialData() == sd.essentialData() && this.timestamp != sd.timestamp) ? false : true;
        }

        return false;
    }

    private String essentialData() {
        StringBuilder str = new StringBuilder();

        str.append(lat)
                .append(",").append(lng)
                .append(",").append(alt)
                .append(",").append(speed)
                .append(",").append(accuracy)
                .append(",").append(bearing)
                .append(",").append(xAcc)
                .append(",").append(yAcc)
                .append(",").append(zAcc)
                .append(",").append(xGyro)
                .append(",").append(yGyro)
                .append(",").append(zGyro)
                .append(",").append(sensorN);

        return str.toString();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(sessionId)
                .append(",").append(lat)
                .append(",").append(lng)
                .append(",").append(alt)
                .append(",").append(speed)
                .append(",").append(accuracy)
                .append(",").append(bearing)
                .append(",").append(timestamp)
                .append(",").append(xAcc)
                .append(",").append(yAcc)
                .append(",").append(zAcc)
                .append(",").append(xGyro)
                .append(",").append(yGyro)
                .append(",").append(zGyro)
                .append(",").append(sensorN)
                .append(",").append(activity);

        return str.toString();
    }
}
