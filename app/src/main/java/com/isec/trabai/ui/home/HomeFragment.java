package com.isec.trabai.ui.home;

import static com.isec.trabai.utils.LocationUtils.createLocationCallback;
import static com.isec.trabai.utils.LocationUtils.createLocationRequest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.isec.trabai.R;
import com.isec.trabai.utils.SensorUtils;

public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "HomeFragment";
    //Accelerometer auxiliary variable to store the previous state values
    private final static float[] acc = new float[3];
    //Gyroscope rotation auxiliary array
    private static final float[] deltaRotationVector = new float[4];

    //Data reading related
    private boolean readingData = false;
    private float timestamp;

    //Coordinates related
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    //Sensors related
    private SensorManager sensorManager;
    private Sensor acSensor;
    private Sensor gyrSensor;
    private LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest());

    //UI Components
    private TextView txtAccX;
    private TextView txtAccY;
    private TextView txtAccZ;

    private TextView txtGyroX;
    private TextView txtGyroY;
    private TextView txtGyroZ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View saveDataView = inflater.inflate(R.layout.fragment_home, container, false);

        //Create connection with sensor service
        sensorManager = (SensorManager) saveDataView.getContext().getSystemService(Context.SENSOR_SERVICE);
        acSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //initialize accelerometer TextViews
        txtAccX = saveDataView.findViewById(R.id.lblX);
        txtAccY = saveDataView.findViewById(R.id.lblY);
        txtAccZ = saveDataView.findViewById(R.id.lblZ);

        //initialize gyroscope TextViews
        txtGyroX = saveDataView.findViewById(R.id.lblGyroX);
        txtGyroY = saveDataView.findViewById(R.id.lblGyroY);
        txtGyroZ = saveDataView.findViewById(R.id.lblGyroZ);

        acc[0] = 10;

        //Create location services client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        final SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        final TextView txtGps = saveDataView.findViewById(R.id.lblGPS);
        locationCallback = createLocationCallback(txtGps);

        if (ActivityCompat.checkSelfPermission(saveDataView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(saveDataView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return saveDataView;
        }

        //Create a routine task to check the location
        task.addOnSuccessListener(locationSettingsResponse -> {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper());
            task.addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
        });

        final Button startButton = (Button) saveDataView.findViewById(R.id.StartScanData);
        startButton.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Started collecting data!");
            Toast.makeText(view.getContext(), "Started to collect data!", Toast.LENGTH_SHORT).show();
            readingData = true;
        });

        final Button finishButton = (Button) saveDataView.findViewById(R.id.StopScanData);
        finishButton.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Stoped collecting data!");
            Toast.makeText(view.getContext(), "Stoped collecting data!", Toast.LENGTH_SHORT).show();
            readingData = false;
        });

        final Button saveDataButton = (Button) saveDataView.findViewById(R.id.SendData);
        saveDataButton.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Send data to the destiny!");
            Toast.makeText(view.getContext(), "Sent collected data to remote server!", Toast.LENGTH_SHORT).show();
        });

        return saveDataView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (readingData) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    Log.d(TAG, "onSensorChanged: Accelerometer changed!");
                    SensorUtils.accelerometerStatusUpdate(sensorEvent, acc, txtAccX, txtAccY, txtAccZ);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    Log.d(TAG, "onSensorChanged: Gyroscope changed!");
                    timestamp = SensorUtils.gyroscopeStatusUpdate(sensorEvent, timestamp, deltaRotationVector,
                            txtGyroX, txtGyroY, txtGyroZ);
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}