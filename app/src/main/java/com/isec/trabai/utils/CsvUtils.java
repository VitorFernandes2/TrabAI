package com.isec.trabai.utils;

import android.util.Log;

import com.isec.trabai.model.data.SensorData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CsvUtils {

    private static final String TAG = "CsvUtils";

    public static String saveSensorDataToCSVFile(final List<SensorData> data,
                                               final String filepath) {
        final String filename = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH)
                .format(new Date()) + "_TrabAI2022.csv";

        File file = new File(filepath, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            bw.append(transformData(data));
            bw.close();
            Log.d(TAG, "saveSensorDataToCSVFile: file " + filename + " successfully created!");
        } catch (IOException e) {
            Log.e(TAG, "saveSensorDataToCSVFile: " + e.getMessage(), e);
        }

        return filename;
    }

    private static String transformData(final List<SensorData> data) {
        StringBuilder str = new StringBuilder();

        str.append("session_id")
                .append(",").append("lat")
                .append(",").append("lng")
                .append(",").append("alt")
                .append(",").append("speed")
                .append(",").append("accuracy")
                .append(",").append("bearing")
                .append(",").append("timestamp")
                .append(",").append("x_acc")
                .append(",").append("y_acc")
                .append(",").append("z_acc")
                .append(",").append("x_gyro")
                .append(",").append("y_gyro")
                .append(",").append("z_gyro")
                .append(",").append("sensorN")
                .append(",").append("activity");

        for (SensorData sd : data) {
            str.append("\n");
            str.append(sd.toString());
        }

        return str.toString();
    }
}
