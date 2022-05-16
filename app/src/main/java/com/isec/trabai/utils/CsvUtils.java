package com.isec.trabai.utils;

import android.util.Log;

import com.isec.trabai.model.data.SensorData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CsvUtils {

    private static final String TAG = "CsvUtils";

    public static void saveSensorDataToCSVFile(final List<SensorData> data,
                                               final String filepath) {
        final String filename = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH)
                .format(new Date()) + "_TrabAI2022.csv";
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(filepath + filename));
            bw.append("Field here!");
            bw.close();
            Log.d(TAG, "saveSensorDataToCSVFile: file " + filename + " successfully created!");
        } catch (IOException e) {
            Log.e(TAG, "saveSensorDataToCSVFile: " + e.getMessage(), e);
        }
    }
}
