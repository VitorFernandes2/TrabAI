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
import com.isec.trabai.model.data.SensorData;
import com.isec.trabai.model.data.SensorDataBuilder;
import com.isec.trabai.utils.SVMUtils;
import com.isec.trabai.utils.SensorUtils;
import com.isec.trabai.utils.UtilsFile;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class DashboardFragment extends Fragment implements SensorEventListener {

    private Classifier scheme;
    private String TAG = "DASHBOARD_FRAGMENT";
    private String folder_path;
    private FragmentDashboardBinding binding;

    //Data reading related
    private boolean readingData = false;
    private final static List<SensorData> sensorDataList = new ArrayList<>();
    private SensorDataBuilder sensorDataBuilder = new SensorDataBuilder();
    private float timestamp;
    private String session_ID;
    private String activityName = Constants.WALK;
    private Instances training_feature;
    private SVMUtils svm;
    private Instances insts;
    private static final String MODEL_FILENAME = "SMO.model";

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
    private ConverterUtils.DataSource ds;

    //https://www.programcreek.com/java-api-examples/?api=weka.classifiers.functions.LibSVM
    //https://github.com/anaoliveiraalves/MySmartApp/blob/master/app/src/main/java/it/isec/ami/mysmartapp/MainActivity.java
    //https://pocketstudyblog.wordpress.com/2018/10/30/svm-classification-using-weka-api-in-java/

    private void wekaClassifyFromModel(Context context){
        try {
            AssetManager assetManager = context.getAssets();
            Log.d(TAG, context.getAssets().open(MODEL_FILENAME).toString());
            scheme = (Classifier) weka.core.SerializationHelper.read(assetManager.open(MODEL_FILENAME));

            final String arffFilename = "Final_Dataset_Balanced_Final.arff";
            ds = new ConverterUtils.DataSource(assetManager.open(arffFilename));
            insts = ds.getDataSet();
            if (insts.classIndex() == -1) {
                insts.setClassIndex(insts.numAttributes() - 1);
            }
        } catch (Exception t) {
            Log.d(TAG, "ERROR: " + t.getMessage());
        }
    }

    public void wekaClassify(String lat, String lng, String alt, double speed, double bearing,
                             double avgXACC, double avgYACC, double avgZACC,
                             double avgXGYRO, double avgYGYRO, double avgZGYRO,
                             double avgXMAG, double avgYMAG, double avgZMAG) {
        StringBuilder arffString = new StringBuilder();
        arffString.append("@relation 'Final_Dataset_Balanced-weka.filters.unsupervised.attribute.Remove-R1,6,8,18'\n" +
                "\n" +
                "@attribute lat numeric\n" +
                "@attribute lng numeric\n" +
                "@attribute alt numeric\n" +
                "@attribute speed numeric\n" +
                "@attribute bearing numeric\n" +
                "@attribute x_acc numeric\n" +
                "@attribute y_acc numeric\n" +
                "@attribute z_acc numeric\n" +
                "@attribute x_gyro numeric\n" +
                "@attribute y_gyro numeric\n" +
                "@attribute z_gyro numeric\n" +
                "@attribute x_mag numeric\n" +
                "@attribute y_mag numeric\n" +
                "@attribute z_mag numeric\n" +
                "@attribute activity {OTHER,INACTIVE,DRIVING,WALKING,RUNNING}");

        final String dataString = lat + "," +
                lng + "," +
                alt + "," +
                speed + "," +
                bearing + "," +
                avgXACC + "," +
                avgYACC + "," +
                avgZACC + "," +
                avgXGYRO + "," +
                avgYGYRO + "," +
                avgZGYRO + "," +
                avgXMAG + "," +
                avgYMAG + "," +
                avgZMAG;

        arffString.append("\n\n@data\n" + dataString + ",?\n");

        try {
            final Instances unlabeledData = new Instances(
                    new BufferedReader(new StringReader(arffString.toString())));
            unlabeledData.setClassIndex(insts.numAttributes() - 1);
            final double pred = scheme.classifyInstance(unlabeledData.instance(0));
            final String prediction = insts.classAttribute().value((int) pred);
            txtActivity.setText(prediction);
        } catch (Exception e) {
            Log.e(TAG, "wekaClassify: " + e.getMessage());
        }
    }

    private void wekaTrain() {
        try {

        } catch (Exception e) {
            Log.e(TAG, "An error occurred: " + e.getMessage());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View saveDataView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtActivity = saveDataView.findViewById(R.id.ActivityDecision);

        wekaClassifyFromModel(getContext());

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

        sensorDataList.add(sensorDataBuilder.build());

        try {
            if (sensorDataList.size() >= 40) {
                UtilsFile temp = new UtilsFile().buildUtilsFile();
                svm = new SVMUtils(getContext(), txtActivity);
                //svm.predict(temp, scheme);
                SensorData auxSensor = sensorDataList.get(sensorDataList.size() - 1);
                Double avgData[] = getSensorDataAVG(sensorDataList);
                wekaClassify(auxSensor.getLat(), auxSensor.getLng(), auxSensor.getAlt(), avgData[0], avgData[1],
                        avgData[2], avgData[3], avgData[4],
                        avgData[5], avgData[6], avgData[7],
                        avgData[8], avgData[9], avgData[10]);

                sensorDataList.clear();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed to: " + accuracy);
    }

    private Double[] getSensorDataAVG(List<SensorData> sensorDataList) {
        Double counter[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        int dataSize = sensorDataList.size();

        for (SensorData data : sensorDataList) {
            counter[0] = counter[0] + Double.parseDouble(data.getSpeed());
            counter[1] = counter[1] + Double.parseDouble(data.getBearing());

            counter[2] = counter[2] + Double.parseDouble(data.getxAcc());
            counter[3] = counter[3] + Double.parseDouble(data.getyAcc());
            counter[4] = counter[4] + Double.parseDouble(data.getzAcc());

            counter[5] = counter[5] + Double.parseDouble(data.getxGyro());
            counter[6] = counter[6] + Double.parseDouble(data.getyGyro());
            counter[7] = counter[7] + Double.parseDouble(data.getzGyro());

            counter[8] = counter[8] + Double.parseDouble(data.getxMag());
            counter[9] = counter[9] + Double.parseDouble(data.getyMag());
            counter[10] = counter[10] + Double.parseDouble(data.getzMag());
        }

        counter[0] = counter[0] / dataSize;
        counter[1] = counter[1] / dataSize;

        counter[2] = counter[2] / dataSize;
        counter[3] = counter[3] / dataSize;
        counter[4] = counter[4] / dataSize;

        counter[5] = counter[5] / dataSize;
        counter[6] = counter[6] / dataSize;
        counter[7] = counter[7] / dataSize;

        counter[8] = counter[8] / dataSize;
        counter[9] = counter[9] / dataSize;
        counter[10] = counter[10] / dataSize;

        return counter;
    }
}