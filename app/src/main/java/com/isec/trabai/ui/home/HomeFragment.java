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
import android.widget.RadioButton;
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
import com.isec.trabai.model.Constants;
import com.isec.trabai.model.data.SensorData;
import com.isec.trabai.model.data.SensorDataBuilder;
import com.isec.trabai.utils.CsvUtils;
import com.isec.trabai.utils.SensorUtils;
import com.isec.trabai.utils.ServerUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "HomeFragment";
    //Accelerometer auxiliary array
    private final static float[] acc = new float[3];
    //Magnetometer auxiliary array
    private final static float[] mag = new float[3];
    //Gyroscope rotation auxiliary array
    private static final float[] deltaRotationVector = new float[4];

    //Data reading related
    private boolean readingData = false;
    private float timestamp;
    private String session_ID;
    private String activityName = Constants.WALK;
    private final static List<SensorData> sensorDataList = new ArrayList<>();
    private SensorDataBuilder sensorDataBuilder = new SensorDataBuilder();

    //Coordinates related
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    //Sensors related
    private SensorManager sensorManager;
    private Sensor acSensor;
    private Sensor gyrSensor;
    private Sensor magSensor;
    private LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest());

    //UI Components
    private TextView txtAccX;
    private TextView txtAccY;
    private TextView txtAccZ;

    private TextView txtGyroX;
    private TextView txtGyroY;
    private TextView txtGyroZ;

    private RadioButton rdbWalk;
    private RadioButton rdbRun;
    private RadioButton rdbDrive;
    private RadioButton rdbInactive;
    private RadioButton rdbOther;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View saveDataView = inflater.inflate(R.layout.fragment_home, container, false);

        //Create connection with sensor service
        sensorManager = (SensorManager) saveDataView.getContext().getSystemService(Context.SENSOR_SERVICE);
        acSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //initialize accelerometer TextViews
        txtAccX = saveDataView.findViewById(R.id.lblX);
        txtAccY = saveDataView.findViewById(R.id.lblY);
        txtAccZ = saveDataView.findViewById(R.id.lblZ);

        //initialize gyroscope TextViews
        txtGyroX = saveDataView.findViewById(R.id.lblGyroX);
        txtGyroY = saveDataView.findViewById(R.id.lblGyroY);
        txtGyroZ = saveDataView.findViewById(R.id.lblGyroZ);

        //initialize radio button GroupView
        rdbWalk = saveDataView.findViewById(R.id.radioButton);
        rdbRun = saveDataView.findViewById(R.id.radioButton2);
        rdbDrive = saveDataView.findViewById(R.id.radioButton3);
        rdbInactive = saveDataView.findViewById(R.id.radioButton4);
        rdbOther = saveDataView.findViewById(R.id.radioButton5);

        acc[0] = 10;

        //Create location services client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        final SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        final TextView txtGps = saveDataView.findViewById(R.id.lblGPS);
        locationCallback = createLocationCallback(txtGps, sensorDataList, sensorDataBuilder);

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

        final Button startButton = saveDataView.findViewById(R.id.StartScanData);
        final Button finishButton = saveDataView.findViewById(R.id.StopScanData);
        final Button saveDataButton = saveDataView.findViewById(R.id.SendData);

        startButton.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Started collecting data!");
            Toast.makeText(view.getContext(), "Started to collect data!", Toast.LENGTH_SHORT).show();
            sensorDataList.clear();
            session_ID = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH).format(new Date());
            readingData = true;
        });

        finishButton.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Stopped collecting data!");
            Toast.makeText(view.getContext(), "Stopped collecting data!", Toast.LENGTH_SHORT).show();
            session_ID = null;
            readingData = false;
        });

        saveDataButton.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Send data to the destiny!");
            final String fileName = CsvUtils.saveSensorDataToCSVFile(sensorDataList, getContext().getFilesDir().getPath());
            ServerUtils.sendFile(getContext().getFilesDir().getPath(), fileName);
            sensorDataList.clear();
            sensorDataBuilder = new SensorDataBuilder();
            Toast.makeText(view.getContext(), "Sent collected data to remote server!", Toast.LENGTH_SHORT).show();
        });

        rdbWalk.setOnClickListener(view -> {
            activityName = Constants.WALK;
        });

        rdbRun.setOnClickListener(view -> {
            activityName = Constants.RUN;
        });

        rdbDrive.setOnClickListener(view -> {
            activityName = Constants.DRIVE;
        });

        rdbInactive.setOnClickListener(view -> {
            activityName = Constants.INACTIVE;
        });

        rdbOther.setOnClickListener(view -> {
            activityName = Constants.OTHER;
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
        sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (readingData) {
            sensorDataBuilder.withSessionId(session_ID).withActivity(activityName);

            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    Log.d(TAG, "onSensorChanged: Accelerometer changed!");
                    SensorUtils.accelerometerStatusUpdate(sensorEvent, acc, txtAccX, txtAccY, txtAccZ);

                    sensorDataBuilder
                            .withSensorN("" + Sensor.TYPE_ACCELEROMETER)
                            .withTimestamp("" + new Timestamp(new Date().getTime()).getTime())
                            .withXAcc("" + acc[0])
                            .withYAcc("" + acc[1])
                            .withZAcc("" + acc[2])
                            .setAccuracy("" + sensorEvent.accuracy);

                    Log.d(TAG, "Accelerometer Accuracy = " + sensorEvent.accuracy);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    Log.d(TAG, "onSensorChanged: Gyroscope changed!");
                    timestamp = SensorUtils.gyroscopeStatusUpdate(sensorEvent, timestamp, deltaRotationVector,
                            txtGyroX, txtGyroY, txtGyroZ);

                    sensorDataBuilder
                            .withSensorN("" + Sensor.TYPE_GYROSCOPE)
                            .withTimestamp("" + new Timestamp(new Date().getTime()).getTime())
                            .withXGyro("" + deltaRotationVector[0])
                            .withYGyro("" + deltaRotationVector[1])
                            .withZGyro("" + deltaRotationVector[2])
                            .setAccuracy("" + sensorEvent.accuracy);

                    Log.d(TAG, "Gyroscope Accuracy = " + sensorEvent.accuracy);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    Log.d(TAG, "onSensorChanged: Magnetometer changed!");
                    SensorUtils.magnetometerStatusUpdate(sensorEvent, mag);

                    sensorDataBuilder
                            .withSensorN("" + Sensor.TYPE_MAGNETIC_FIELD)
                            .withTimestamp("" + new Timestamp(new Date().getTime()).getTime())
                            .withXMag("" + mag[0])
                            .withYMag("" + mag[1])
                            .withZMag("" + mag[2])
                            .setAccuracy("" + sensorEvent.accuracy);

                    Log.d(TAG, "Magnetometer Accuracy = " + sensorEvent.accuracy);
                    break;
            }

            //session_id lat lng alt speed accuracy bearing timestamp x_acc y_acc z_acc x_gyro y_gyro z_gyro x_mag y_mag z_mag sensorN activity
            sensorDataList.add(sensorDataBuilder.build());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed to: " + accuracy);
    }

}