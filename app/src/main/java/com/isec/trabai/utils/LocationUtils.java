package com.isec.trabai.utils;

import android.location.Location;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class LocationUtils {
    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public static LocationCallback createLocationCallback(final TextView txtGps) {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    final String gpsText = "Long: " + String.valueOf(location.getLongitude()) +
                            " Lat: " + String.valueOf(location.getLatitude());
                    txtGps.setText(gpsText);
                }
            }
        };
    }
}
