package com.isec.trabai.utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class SVMUtils {

    private final String TAG = "SVM";
    private final Context context;
    private Classifier mClassifier;
    private final TextView txtActivityRecognition;

    public SVMUtils(Context context, TextView txtActivityRecognition) {
        this.context = context;
        this.txtActivityRecognition = txtActivityRecognition;
    }

    /*
        Log.d(TAG, "F1 Measure=" + eval.fMeasure(1));
        Log.d(TAG, "Precision=" + eval.precision(1));
        Log.d(TAG, "Recall=" + eval.recall(1));
        Log.d(TAG, "TNR=" + eval.trueNegativeRate(1));
        Log.d(TAG, "TPR=" + eval.truePositiveRate(1));
        Log.d(TAG, "FNR=" + eval.falseNegativeRate(1));
        Log.d(TAG, "FPR=" + eval.falsePositiveRate(1));
    */

    public void predict(UtilsFile utilsFile, Classifier svm) {
        if (svm == null) {
            Log.d(TAG, "Model not Loaded");
            return;
        }

        // we need those for creating new instances later
        // order of attributes/classes needs to be exactly equal to those used for training
        final Attribute attributeXAcc = new Attribute("x_acc");
        final Attribute attributeYAcc = new Attribute("y_acc");
        final Attribute attributeZAcc = new Attribute("z_acc");
        final Attribute attributeXGyro = new Attribute("x_gyro");
        final Attribute attributeYGyro = new Attribute("y_gyro");
        final Attribute attributeZGyro = new Attribute("z_gyro");
        final Attribute attributeLinearX = new Attribute("x_mag");
        final Attribute attributeLinearY = new Attribute("y_mag");
        final Attribute attributeLinearZ = new Attribute("z_mag");
        final List<String> classes = new ArrayList<String>() {
            {
                add("INACTIVE"); // cls nr 1
                add("WALKING"); // cls nr 2
                add("RUNNING"); // cls nr 3
                add("DRIVING"); // cls nr 3
                add("OTHER"); // cls nr 3
            }
        };
        //atriburos 108

        // Instances(...) requires ArrayList<> instead of List<>...
        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
            {
                add(attributeYAcc);
                add(attributeZAcc);
                add(attributeXAcc);
                add(attributeXGyro);
                add(attributeYGyro);
                add(attributeZGyro);
                add(attributeLinearX);
                add(attributeLinearY);
                add(attributeLinearZ);
                Attribute attributeClass = new Attribute("@@class@@", classes);
                add(attributeClass);
            }
        };

        // unpredicted data sets (reference to sample structure for new instances)
        Instances dataUnpredicted = new Instances("TestInstances",
                attributeList, 1);
        // last feature is target variable
        dataUnpredicted.setClassIndex(dataUnpredicted.numAttributes() - 1);

        //GET instance
        final UtilsFile act = utilsFile;
        // create new instance: this one should fall into the setosa domain

        DenseInstance newInstance = new DenseInstance(dataUnpredicted.numAttributes()) {
            {
                setValue(attributeYAcc, act.getY_acc());
                setValue(attributeZAcc, act.getZ_acc());
                setValue(attributeXAcc, act.getX_acc());
                setValue(attributeYGyro, act.getY_gyro());
                setValue(attributeZGyro, act.getZ_gyro());
                setValue(attributeXGyro, act.getX_gyro());
                setValue(attributeLinearX, act.getX_mag());
                setValue(attributeLinearY, act.getY_mag());
                setValue(attributeLinearZ, act.getZ_mag());
            }
        };
        // reference to dataset
        newInstance.setDataset(dataUnpredicted);

        // predict new sample
        try {
            double result = svm.classifyInstance(newInstance);
            String className = classes.get(new Double(result).intValue());
            String msg = ", predicted: " + className;
            //txtActivityRecognition.setText("Predicted: " + className);
            Log.d(TAG, "Predicted: " + className);
            // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}