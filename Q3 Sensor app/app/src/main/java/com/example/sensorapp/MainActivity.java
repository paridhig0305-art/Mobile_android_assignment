package com.example.sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // SensorManager gives access to all sensors on the phone
    SensorManager sensorManager;

    // Three sensors we need
    Sensor accelerometer;
    Sensor lightSensor;
    Sensor proximitySensor;

    // TextViews to display sensor data
    TextView tvAccelerometer;
    TextView tvLight;
    TextView tvProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect TextViews to XML
        tvAccelerometer = findViewById(R.id.tvAccelerometer);
        tvLight         = findViewById(R.id.tvLight);
        tvProximity     = findViewById(R.id.tvProximity);

        // Get the SensorManager service
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get each sensor
        accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Check if sensors exist on this phone
        if (accelerometer == null) tvAccelerometer.setText("Accelerometer: Not available");
        if (lightSensor == null)   tvLight.setText("Light: Not available");
        if (proximitySensor == null) tvProximity.setText("Proximity: Not available");
    }

    // Register sensors when app is visible
    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (proximitySensor != null)
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Unregister sensors when app is not visible (saves battery)
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // This is called automatically whenever sensor data changes
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Accelerometer gives X, Y, Z values
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            tvAccelerometer.setText("Accelerometer\nX: " + x + "\nY: " + y + "\nZ: " + z);

        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            // Light sensor gives one value in lux
            float lux = event.values[0];
            tvLight.setText("Light Sensor\n" + lux + " lux");

        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // Proximity sensor gives distance in cm
            float distance = event.values[0];
            String near = (distance == 0) ? "NEAR" : "FAR";
            tvProximity.setText("Proximity Sensor\n" + distance + " cm (" + near + ")");
        }
    }

    // Required method — we don't need it
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}