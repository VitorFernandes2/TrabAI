package com.isec.trabai.model;

public final class Constants {
    //Constant to convert nanoseconds to milliseconds
    public static final float NS2S = 1.0f / 1000000000.0f;
    //Constant of update rate in milliseconds of gps sensor
    public static final int UR05S = 500;
    //Constants of the activity definition
    public static final String INACTIVE = "INACTIVE";
    public static final String ACTIVE = "ACTIVE";
    //TODO: read the project guidelines again
    public static final String WALK = "ANDAR";
    public static final String RUN = "CORRER";
    public static final String GO_DOWN = "DESCER";
    public static final String GO_UP = "SUBIR";
    public static final String OTHER = "OUTRO";
}
