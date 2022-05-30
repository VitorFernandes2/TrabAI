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

import java.util.List;

public class LocationUtils {
    private final static String TAG = "LocationUtils";
    private static final double velocity = 0.0;
    private static Location lastLocation;

    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(Constants.UR05S);
        locationRequest.setFastestInterval(Constants.UR05S);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public static LocationCallback createLocationCallback(final TextView txtGps, List<SensorData> sensorDataList, SensorDataBuilder sensorDataBuilder) {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    final String gpsText = "Long: " + location.getLongitude() +
                            " Lat: " + location.getLatitude() +
                            " Alt: " + location.getAltitude();

                    //TODO: decide which speed calculation to use
                    /*if (lastLocation != null) {
                        //Time in seconds elapsed between current and last locations
                        long elapsedTime = (location.getTime() - lastLocation.getTime()) / 1000;
                        //Distance in meters between current and last locations
                        float distance = lastLocation.distanceTo(location);

                        velocity = distance / elapsedTime;
                    }*/

                    //TODO: decide if we register GPS accuracy (because it is different from values between -1 and 4)
                    sensorDataBuilder
                            .withSensorN("0")
                            .withLat(Double.toString(location.getLatitude()))
                            .withLng(Double.toString(location.getLongitude()))
                            .withAlt(Double.toString(location.getAltitude()))
                            .withSpeed(Double.toString(location.getSpeed()))
                            //.withSpeed(Double.toString(velocity))
                            .setBearing(Double.toString(location.getBearing()));

                    txtGps.setText(gpsText);

                    Log.d(TAG, "GPS Accuracy = " + location.getAccuracy());
                    //lastLocation = location;
                    sensorDataList.add(sensorDataBuilder.build());
                }
            }
        };
    }
}
