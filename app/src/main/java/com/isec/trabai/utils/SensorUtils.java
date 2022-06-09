package com.isec.trabai.utils;

import static android.util.Half.EPSILON;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.isec.trabai.model.Constants;

public class SensorUtils {

    public static float gyroscopeStatusUpdate(final SensorEvent sensorEvent,
                                              float timestamp,
                                              final float[] deltaRotationVector,
                                              final TextView txtXGyr,
                                              final TextView txtYGyr,
                                              final TextView txtZGyr) {
        // This timestamp's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (sensorEvent.timestamp - timestamp) * Constants.NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = sensorEvent.values[0];
            float axisY = sensorEvent.values[1];
            float axisZ = sensorEvent.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float) sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestamp
            // in order to get a delta rotation from this sample over the timestamp
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) sin(thetaOverTwo);
            float cosThetaOverTwo = (float) cos(thetaOverTwo);

            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = sensorEvent.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

        final String textGyroX = "Gyro X: " + String.format("%.4f", deltaRotationVector[0]);
        final String textGyroY = " Gyro Y: " + String.format("%.4f", deltaRotationVector[1]);
        final String textGyroZ = " Gyro Z: " + String.format("%.4f", deltaRotationVector[2]);

        txtXGyr.setText(textGyroX);
        txtYGyr.setText(textGyroY);
        txtZGyr.setText(textGyroZ);

        return timestamp;
    }

    public static void accelerometerStatusUpdate(final SensorEvent sensorEvent,
                                                 final float[] acc,
                                                 final TextView txtXAcc,
                                                 final TextView txtYAcc,
                                                 final TextView txtZAcc) {
        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
        linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
        linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

        if (abs(linear_acceleration[0] - acc[0]) > 2) {
            acc[0] = linear_acceleration[0];
            acc[1] = linear_acceleration[1];
            acc[2] = linear_acceleration[2];

            final String textAccX = "Acc X: " + String.format("%.4f", acc[0]);
            final String textAccY = " Acc Y: " + String.format("%.4f", acc[1]);
            final String textAccZ = " Acc Z: " + String.format("%.4f", acc[2]);

            txtXAcc.setText(textAccX);
            txtYAcc.setText(textAccY);
            txtZAcc.setText(textAccZ);
        }
    }

    public static void magnetometerStatusUpdate(final SensorEvent sensorEvent,
                                                 final float[] mag) {
        mag[0] = sensorEvent.values[0];
        mag[1] = sensorEvent.values[1];
        mag[2] = sensorEvent.values[2];
    }

    @Deprecated
    public static double pressureStatusUpdate(final SensorEvent sensorEvent,
                                            final TextView txtOrientation) {
        // Ambient pressure in millibar units
        float pressure = sensorEvent.values[0];

        double auxPressure = pressure * 1000 * 0.986923267;

        return (Constants.SST / Constants.L) * Math.pow(1 - (auxPressure / Constants.SAPS), (Constants.R * Constants.L) / (Constants.M * Constants.G));
    }

}
