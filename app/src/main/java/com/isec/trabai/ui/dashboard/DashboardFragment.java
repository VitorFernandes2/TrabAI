package com.isec.trabai.ui.dashboard;

import static com.isec.trabai.utils.LocationUtils.createLocationActivityCallback;
import static com.isec.trabai.utils.LocationUtils.createLocationRequest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
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
import android.widget.TextView;

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
import com.isec.trabai.databinding.FragmentDashboardBinding;
import com.isec.trabai.model.Constants;
import com.isec.trabai.model.data.SensorDataBuilder;
import com.isec.trabai.utils.SVMUtils;
import com.isec.trabai.utils.SensorUtils;
import com.isec.trabai.utils.UtilsFile;

import java.sql.Timestamp;
import java.util.Date;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class DashboardFragment extends Fragment implements SensorEventListener {

    private Classifier mClassifier;
    private String TAG = "DASHBOARD_FRAGMENT";
    private String folder_path;
    private FragmentDashboardBinding binding;

    //Data reading related
    private boolean readingData = false;
    private SensorDataBuilder sensorDataBuilder = new SensorDataBuilder();
    private float timestamp;
    private String session_ID;
    private String activityName = Constants.WALK;
    private Instances training_feature;
    private SVMUtils svm;
    private static final String MODEL_FILENAME = "SVM.model";

    //Accelerometer auxiliary array
    private final static float[] acc = new float[3];
    //Magnetometer auxiliary array
    private final static float[] mag = new float[3];
    //Gyroscope rotation auxiliary array
    private static final float[] deltaRotationVector = new float[4];

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
    private TextView txtActivity;

    //https://www.programcreek.com/java-api-examples/?api=weka.classifiers.functions.LibSVM
    //https://github.com/anaoliveiraalves/MySmartApp/blob/master/app/src/main/java/it/isec/ami/mysmartapp/MainActivity.java
    //https://pocketstudyblog.wordpress.com/2018/10/30/svm-classification-using-weka-api-in-java/

    private void loadModel(Context context){
        try {
            AssetManager assetManager = context.getAssets();
            Log.d(TAG, context.getAssets().toString());
            Log.d(TAG, context.getAssets().open(MODEL_FILENAME).toString());
            mClassifier = (Classifier) weka.core.SerializationHelper.read(assetManager.open(MODEL_FILENAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View saveDataView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtActivity = saveDataView.findViewById(R.id.ActivityDecision);

        loadModel(getContext());

        //Create connection with sensor service
        sensorManager = (SensorManager) saveDataView.getContext().getSystemService(Context.SENSOR_SERVICE);
        acSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        acc[0] = 10;

        //Create location services client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        final SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        final TextView txtGps = saveDataView.findViewById(R.id.lblGPS);
        locationCallback = createLocationActivityCallback(sensorDataBuilder);

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

        folder_path = getContext().getFilesDir().getPath();

        svm = new SVMUtils(getContext(), txtActivity);

        return saveDataView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        sensorDataBuilder.withSessionId(session_ID).withActivity(activityName);

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.d(TAG, "onSensorChanged: Accelerometer changed!");
                SensorUtils.accelerometerStatusUpdate(sensorEvent, acc);

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
                timestamp = SensorUtils.gyroscopeStatusUpdate(sensorEvent, timestamp, deltaRotationVector);

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
        //TODO: Detect Activity
        //boolean status_trained = training_feature.add(new Instances((Reader) sensorDataBuilder.build()));

        //Log.e("TTTTT", "Training status: " + status_trained);

        sensorDataBuilder.build().toString();

        try {
            UtilsFile temp = new UtilsFile().buildUtilsFile();
            Log.d("AQUI", "Passei 1");
            svm.predict(temp, mClassifier);
            Log.d("AQUI", "Passei 2");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed to: " + accuracy);
    }
}