package com.isec.trabai.model;

public final class Constants {
    //Constant to convert nanoseconds to milliseconds
    public static final float NS2S = 1.0f / 1000000000.0f;
    //Constant of update rate in milliseconds of gps sensor
    public static final int UR05S = 500;
    //Constants of the activity definition
    public static final String INACTIVE = "INACTIVE";
    public static final String WALK = "WALKING";
    public static final String RUN = "RUNNING";
    public static final String DRIVE = "DRIVING";
    public static final String GO_DOWN = "GO_DOWN"; //This activity is not required to be recognized
    public static final String GO_UP = "GO_UP";     //This activity is not required to be recognized
    public static final String OTHER = "OTHER";     //This activity is not required to be recognized - but could be good to include
}
