package com.isec.trabai.utils;

import java.util.Locale;

enum Actividades{ INACTIVE, WALKING, RUNNING, DRIVING, OTHER };
public class UtilsFile {
    public static int session_id = 0, accuracy = 0, sensorN = 0;
    public static final String CABECALHO = "session_id,lat,lng,alt,speed,accuracy,bearing,timestamp,x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro,x_mag,y_mag,z_mag,sensorN,activity";
    public static String linha = "%d,%.3f,%.3f,%.3f,%.3f,%d,%.3f,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%d,%s";
    public static String linhaSemAct = "%d,%.3f,%.3f,%.3f,%.3f,%d,%.3f,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%d";
    public static final String CABECALHOSEMACT = "session_id,lat,lng,alt,speed,accuracy,bearing,timestamp,x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro,x_mag,y_mag,z_mag,sensorN";
    private double lat, lng, alt, speed, bearing;
    private double x_acc, y_acc,z_acc;
    private double x_mag, y_mag, z_mag;
    private double x_gyro, y_gyro,z_gyro;
    private long timestamp;
    private Actividades actividades = null;

    public Actividades getActividades() {
        return actividades;
    }

    public int getSession_id(){return session_id;}

    public void setActividades(Actividades actividades) {
        this.actividades = actividades;
    }

    public double getLat() {
        return lat;
    }

    public double getX_mag() {
        return x_mag;
    }

    public void setX_mag(double x_mag) {
        this.x_mag = x_mag;
    }

    public double getY_mag() {
        return y_mag;
    }

    public void setY_mag(double y_mag) {
        this.y_mag = y_mag;
    }

    public double getZ_mag() {
        return z_mag;
    }

    public void setZ_mag(double z_mag) {
        this.z_mag = z_mag;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getX_acc() {
        return x_acc;
    }

    public void setX_acc(double x_acc) {
        this.x_acc = x_acc;
    }

    public double getY_acc() {
        return y_acc;
    }

    public void setY_acc(double y_acc) {
        this.y_acc = y_acc;
    }

    public double getZ_acc() {
        return z_acc;
    }

    public void setZ_acc(double z_acc) {
        this.z_acc = z_acc;
    }

    public double getX_gyro() {
        return x_gyro;
    }

    public void setX_gyro(double x_gyro) {
        this.x_gyro = x_gyro;
    }

    public double getY_gyro() {
        return y_gyro;
    }

    public void setY_gyro(double y_gyro) {
        this.y_gyro = y_gyro;
    }

    public double getZ_gyro() {
        return z_gyro;
    }

    public void setZ_gyro(double z_gyro) {
        this.z_gyro = z_gyro;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public void GPS(double x, double y, double z){
        setLat(x);
        setAlt(y);
        setLng(z);
    }

    public void gyro(double x, double y, double z){
        setX_gyro(x);
        setY_gyro(y);
        setZ_gyro(z);
    }

    public void acc(double x, double y, double z){
        setX_acc(x);
        setY_acc(y);
        setZ_acc(z);
    }

    public void linearAcc(double x, double y, double z){
        setX_mag(x);
        setY_mag(y);
        setZ_mag(z);
    }

    public UtilsFile(int i){
        session_id = i;
        setTimestamp();
        x_acc= y_acc = z_acc =0;
        lat=lng=alt = 0;
        x_gyro= y_gyro= z_gyro = 0;
    }

    public UtilsFile(){
        session_id++;
        x_acc= y_acc = z_acc =0;
        lat=lng=alt = 0;
        x_gyro= y_gyro= z_gyro = 0;
        setTimestamp();
    }

    public String converteLinha(){
        return getActividades() == null ? linhaSemAct() : linha();

    }
    public String linhaSemAct(){
        return String.format(Locale.ROOT,linhaSemAct, session_id,getLat(), getLng(), getAlt() ,getTimestamp(), getX_acc(),
                getY_acc(),getZ_acc(), getX_gyro(),getY_gyro(),getZ_gyro(), getX_mag(), getY_mag(), getZ_mag());
    }
    public String linha(){
        return  String.format(Locale.ROOT,linha, session_id,getLat(), getLng(), getAlt() ,getTimestamp(), getX_acc(),
                getY_acc(),getZ_acc(), getX_gyro(),getY_gyro(),getZ_gyro(), getX_mag(), getY_mag(), getZ_mag(), getActividades().toString());
    }

    public UtilsFile buildUtilsFile(){
        UtilsFile temp = new UtilsFile();
        temp.GPS(lat, lng, alt);
        temp.acc(x_acc, y_acc, z_acc);
        temp.linearAcc(x_mag, y_mag, z_mag);
        temp.gyro(x_gyro, y_gyro, z_gyro);
        return  temp;
    }
}