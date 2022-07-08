package com.isec.trabai.model;

public final class Constants {
    //Constant to convert nanoseconds to milliseconds
    public static final float NS2S = 1.0f / 1000000000.0f;
    //Constant of update rate in milliseconds of gps sensor
    public static final int UR05S = 500;
    //Constant pressure of dry air at sea level in atmospheric units
    public static final float SAPS = 0.986923267f;
    //Constant temperature of the sea at sea level in kelvin degrees
    public static final float SST = 288.15f;
    //Constant of the rate of decrease of temperature with height in kelvin degrees for every meter
    public static final float L = 0.0098f;
    //Constant of the molar mass of dry air in g/mol units
    public static final float M = 28.9647f;
    //Constant of the earth gravity for 1 gram in m/s^2 units
    public static final float G = 9.8f;
    //Constant of the
    public static final float R = 0.082f;
    //Constants of the activity definition
    public static final String INACTIVE = "INACTIVE";
    public static final String WALK = "WALKING";
    public static final String RUN = "RUNNING";
    public static final String DRIVE = "DRIVING";
    public static final String OTHER = "OTHER";
}
