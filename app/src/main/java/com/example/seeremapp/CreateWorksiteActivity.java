package com.example.seeremapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import droidninja.filepicker.FilePickerBuilder;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.User;
import com.google.maps.model.LatLng;
import com.schibstedspain.leku.LocationPickerActivity;
import com.schibstedspain.leku.locale.SearchZoneRect;

public class CreateWorksiteActivity extends AppCompatActivity {
  private WorksiteDB worksiteDB;
  private EditText worksiteCompany, worksiteName, worksiteProjectId, worksiteAddress;
  private Button createWorksiteButton;
  private double lat = 0.0D;
  private double longitude = 0.0D;

  private static int LAUNCH_ADDRESS = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_worksite);
    worksiteDB = WorksiteDB.getInstance(this);

    // set references
    worksiteCompany = findViewById(R.id.worksiteCompany);
    worksiteName = findViewById(R.id.worksiteName);
    worksiteProjectId = findViewById(R.id.worksiteProjectId);
    worksiteAddress = findViewById(R.id.worksiteAddress);
    createWorksiteButton = findViewById(R.id.createWorksiteButton);

    // location picker
    Intent locationPickerIntent = (new LocationPickerActivity.Builder())
      .withGeolocApiKey(getString(R.string.map_key))
      .shouldReturnOkOnBackPressed()
      .withSatelliteViewHidden()
      .withGoogleTimeZoneEnabled()
      .withVoiceSearchHidden()
      .build(CreateWorksiteActivity.this);

    worksiteAddress.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivityForResult(locationPickerIntent, LAUNCH_ADDRESS);
      }
    });

    createWorksiteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        worksiteDB.addWorksite(
          worksiteCompany.getText().toString(),
          worksiteName.getText().toString(),
          worksiteProjectId.getText().toString(),
          worksiteAddress.getText().toString(),
          lat,
          longitude
        );

        finish();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == LAUNCH_ADDRESS) {
      if (resultCode == RESULT_OK) {
        String address = data.getStringExtra("location_address");
        lat = data.getDoubleExtra("latitude", 0.0D);
        longitude = data.getDoubleExtra("longitude", 0.0D);

        worksiteAddress.setText(address);
      }
    }
  }
}