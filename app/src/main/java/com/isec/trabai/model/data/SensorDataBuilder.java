package com.isec.trabai.model.data;

public class SensorDataBuilder {

    private String sessionId;
    private String lat;
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

    public SensorDataBuilder() {
    }

    public SensorDataBuilder withSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public SensorDataBuilder withLat(String lat) {
        this.lat = lat;
        return this;
    }

    public SensorDataBuilder whitAlt(String alt) {
        this.alt = alt;
        return this;
    }

    public SensorDataBuilder withSpeed(String speed) {
        this.speed = speed;
        return this;
    }

    public SensorDataBuilder setAccuracy(String accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public SensorDataBuilder setBearing(String bearing) {
        this.bearing = bearing;
        return this;
    }


    public SensorDataBuilder withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public SensorDataBuilder withXAcc(String xAcc) {
        this.xAcc = xAcc;
        return this;
    }

    public SensorDataBuilder withYAcc(String yAcc) {
        this.yAcc = yAcc;
        return this;
    }

    public SensorDataBuilder withZAcc(String zAcc) {
        this.zAcc = zAcc;
        return this;
    }

    public SensorDataBuilder withXGyro(String xGyro) {
        this.xGyro = xGyro;
        return this;
    }

    public SensorDataBuilder withYGyro(String yGyro) {
        this.yGyro = yGyro;
        return this;
    }

    public SensorDataBuilder withZGyro(String zGyro) {
        this.zGyro = zGyro;
        return this;
    }

    public SensorData build() {
        return new SensorData(sessionId, lat, alt, speed, accuracy,
                bearing, timestamp, xAcc, yAcc, zAcc, xGyro, yGyro, zGyro);
    }
}
