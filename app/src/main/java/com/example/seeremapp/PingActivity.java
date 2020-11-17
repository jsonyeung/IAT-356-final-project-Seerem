package com.example.seeremapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class PingActivity extends AppCompatActivity implements SensorEventListener {
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;
  private SensorManager sensorManager;
  private Sensor mStepCounter;
  private boolean isCounterSensorPresent;
  private int stepCount = 0;
  private double lat = 0.0D, longitude = 0.0D;

  private TextView stepCounter, locationVals;
  private Button finishSet;
  private LocationRequest locationRequest;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private LocationCallback locationCallback;

  private final static int REQUEST_LOCATION = 99;

  @SuppressLint("MissingPermission")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ping);

    sharedPreferences = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
    editor = sharedPreferences.edit();
    stepCount = 0;

    // set references
    stepCounter = findViewById(R.id.stepCounter);
    locationVals = findViewById(R.id.locationVals);
    finishSet = findViewById(R.id.finishSet);

    // set GPS details on finish
    finishSet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent();
        i.putExtra("lat", lat);
        i.putExtra("longitude", longitude);
        setResult(RESULT_OK, i);
        finish();
      }
    });

    // Google location
    locationRequest = new LocationRequest();
    locationRequest.setInterval(1000 * 5);
    locationRequest.setFastestInterval(1000 * 2);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    updateGPS();

    // location callback
    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);

        Location location = locationResult.getLastLocation();
        lat = location.getLatitude();
        longitude = location.getLongitude();
        locationVals.setText("Location (Lat, Long): (" + lat + ", " + longitude + ")");
      }
    };

    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    // Manage step sensor
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
        mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        isCounterSensorPresent = true;

    } else {
      stepCounter.setText("Steps: Step counter not available");
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch(requestCode) {
      case REQUEST_LOCATION:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          updateGPS();
        } else {
          Toast.makeText(this, "This activity requires permission to track your location", Toast.LENGTH_LONG).show();
        }
    }
  }

  private void updateGPS() {
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
        @SuppressLint("MissingPermission")
        @Override
        public void onSuccess(Location location) {
          lat = location.getLatitude();
          longitude = location.getLongitude();
          locationVals.setText("Location (Lat, Long): (" + lat + ", " + longitude + ")");
        }
      });
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
      }
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (sensorEvent.sensor == mStepCounter) {
      int steps = (int) sensorEvent.values[0];
      stepCounter.setText("Steps: " + stepCount++);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {}

  @Override
  protected void onResume() {
    super.onResume();
    if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
      sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (sensorManager.getDefaultSensor((Sensor.TYPE_STEP_COUNTER)) != null) {
      sensorManager.unregisterListener(this, mStepCounter);
    }
  }
}