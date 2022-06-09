package com.isec.trabai.utils;

import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.isec.trabai.model.Constants;
import com.isec.trabai.model.data.SensorData;
import com.isec.trabai.model.data.SensorDataBuilder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class LocationUtils {
    private final static String TAG = "LocationUtils";

    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(Constants.UR05S);
        locationRequest.setFastestInterval(Constants.UR05S);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public static LocationCallback createLocationCallback(final TextView txtGps,
                                                          final List<SensorData> sensorDataList,
                                                          final SensorDataBuilder sensorDataBuilder) {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    final String gpsText = "Long: " + location.getLongitude() + "\n" +
                            " Lat: " + location.getLatitude() + "\n" +
                            " Alt: " + location.getAltitude();

                    final int locationAccuracy = location.getAccuracy() < 34 ? 1 : location.getAccuracy() < 67 ? 2 : 3;

                    txtGps.setText(gpsText);

                    Log.d(TAG, "GPS Accuracy = " + locationAccuracy);

                    sensorDataBuilder
                            .withSensorN("0")
                            .withTimestamp("" + new Timestamp(new Date().getTime()).getTime())
                            .setAccuracy("" + locationAccuracy)
                            .withLat("" + location.getLatitude())
                            .withLng("" + location.getLongitude())
                            .withAlt("" + location.getAltitude())
                            .withSpeed("" + location.getSpeed())
                            .setBearing("" + location.getBearing());

                    sensorDataList.add(sensorDataBuilder.build());
                }
            }
        };
    }
}
