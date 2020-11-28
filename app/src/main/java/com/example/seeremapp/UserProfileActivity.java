package com.example.seeremapp;

import android.os.Bundle;

import com.example.seeremapp.fragment.UserDashboardProfileFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserProfileActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_view);

    Toolbar toolbar = findViewById(R.id.toolbar2);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("User Details");

    String email = getIntent().getStringExtra("email");
    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
        new UserDashboardProfileFragment(email, false)).commit();
  }
}
