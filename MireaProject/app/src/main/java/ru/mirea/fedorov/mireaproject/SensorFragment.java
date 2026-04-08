package ru.mirea.fedorov.mireaproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.fedorov.mireaproject.databinding.FragmentSensorBinding;

public class SensorFragment extends Fragment implements SensorEventListener {

    private FragmentSensorBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private final float[] accelerometerValues = new float[3];
    private final float[] magnetometerValues = new float[3];
    private boolean hasAccelerometerData;
    private boolean hasMagnetometerData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorBinding.inflate(inflater, container, false);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        ArrayList<String> sensorDescriptions = new ArrayList<>();
        for (Sensor sensor : sensors) {
            sensorDescriptions.add(sensor.getName() + " | range: " + sensor.getMaximumRange());
        }

        binding.textSensorCount.setText(getString(R.string.sensor_count_template, sensors.size()));
        binding.listSensors.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                sensorDescriptions
        ));

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null) {
            binding.textHeading.setText(R.string.sensor_no_accelerometer);
        } else if (magnetometer == null) {
            binding.textNorthHint.setText(R.string.sensor_no_magnetometer);
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (binding == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, accelerometerValues.length);
            hasAccelerometerData = true;
            binding.textAxisX.setText(getString(R.string.axis_template, "X", accelerometerValues[0]));
            binding.textAxisY.setText(getString(R.string.axis_template, "Y", accelerometerValues[1]));
            binding.textAxisZ.setText(getString(R.string.axis_template, "Z", accelerometerValues[2]));
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerValues, 0, magnetometerValues.length);
            hasMagnetometerData = true;
        }

        if (hasAccelerometerData && hasMagnetometerData) {
            updateOrientation();
        }
    }

    private void updateOrientation() {
        float[] rotationMatrix = new float[9];
        float[] orientationValues = new float[3];

        boolean success = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerValues,
                magnetometerValues
        );
        if (!success) {
            return;
        }

        SensorManager.getOrientation(rotationMatrix, orientationValues);
        float azimuthInDegrees = (float) Math.toDegrees(orientationValues[0]);
        if (azimuthInDegrees < 0) {
            azimuthInDegrees += 360f;
        }

        binding.textHeading.setText(getString(
                R.string.heading_template,
                azimuthInDegrees,
                resolveDirection(azimuthInDegrees)
        ));
        binding.textNorthHint.setText(getString(
                R.string.north_hint_template,
                resolveNorthAdvice(azimuthInDegrees)
        ));
    }

    private String resolveDirection(float azimuthInDegrees) {
        if (azimuthInDegrees >= 337.5f || azimuthInDegrees < 22.5f) {
            return "Север";
        }
        if (azimuthInDegrees < 67.5f) {
            return "Северо-восток";
        }
        if (azimuthInDegrees < 112.5f) {
            return "Восток";
        }
        if (azimuthInDegrees < 157.5f) {
            return "Юго-восток";
        }
        if (azimuthInDegrees < 202.5f) {
            return "Юг";
        }
        if (azimuthInDegrees < 247.5f) {
            return "Юго-запад";
        }
        if (azimuthInDegrees < 292.5f) {
            return "Запад";
        }
        return "Северо-запад";
    }

    private String resolveNorthAdvice(float azimuthInDegrees) {
        if (azimuthInDegrees >= 315f || azimuthInDegrees < 45f) {
            return "Вы уже смотрите примерно на север.";
        }
        if (azimuthInDegrees < 180f) {
            return "Поверните устройство левее, чтобы выйти на север.";
        }
        return "Поверните устройство правее, чтобы выйти на север.";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (binding == null) {
            return;
        }
        binding.textAccuracy.setText(getString(R.string.sensor_accuracy_template, accuracy));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
