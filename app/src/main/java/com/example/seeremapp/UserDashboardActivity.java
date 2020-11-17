package com.example.seeremapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import droidninja.filepicker.FilePickerConst;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.database.helpers.UserHelper;
import com.example.seeremapp.fragment.UserDashboardHomeFragment;
import com.example.seeremapp.fragment.UserProfileFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  public static SharedPreferences sharedPrefs;
  private SharedPreferences.Editor editor;
  private LinearLayout signOut;
  private NavigationView navView;
  private Toolbar toolbar;
  private DrawerLayout drawer;
  private UserDB userDB;
  private WorksiteDB worksiteDB;
  private String email;

  private boolean change = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_dashboard);
    userDB = UserDB.getInstance(this);
    worksiteDB = WorksiteDB.getInstance(this);

    // references
    navView = findViewById(R.id.navView);
    drawer = findViewById(R.id.drawerLayout);
    toolbar = findViewById(R.id.toolbar);
    signOut = findViewById(R.id.userSignOut);

    setSupportActionBar(toolbar);
    navView.setNavigationItemSelectedListener(this);

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
      R.string.open_nav, R.string.close_nav);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    // get account details & login
    sharedPrefs = getSharedPreferences("USER", Context.MODE_PRIVATE);
    email = sharedPrefs.getString("email", "");

    try {
      User user = userDB.getUser(email);

      // set user details on navigation header
      View headerView = navView.getHeaderView(0);
      ((TextView) headerView.findViewById(R.id.navUsername)).setText(user.getFirstName() + " " + user.getLastName());
      ((TextView) headerView.findViewById(R.id.navEmail)).setText(user.getEmail());
    } catch(Exception err) { /* finish(); */ }

    // Sign out functionality
    signOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        drawer.closeDrawer(GravityCompat.START);
        logOut();
      }
    });
  }

  public void logOut() {
    editor = sharedPrefs.edit();
    editor.putBoolean("remember", false);
    editor.commit();

    Intent i = new Intent(getApplicationContext(), MainActivity.class);
    startActivity(i);
    finish();
  }

  @Override
  protected void onStart() {
    super.onStart();
    // make 'home' selected on startup and show corresponding fragment
    if (!change) onNavigationItemSelected(navView.getMenu().findItem(R.id.navHome));
    else onNavigationItemSelected(navView.getMenu().findItem(R.id.navUser));
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.navHome:
        getSupportActionBar().setTitle("Your Dashboard");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
          new UserDashboardHomeFragment()).commit();
        break;
      case R.id.navUser:
        getSupportActionBar().setTitle("User Profile");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
          new UserProfileFragment(email, true)).commit();
        break;
      default: break;
    }

    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START))
      drawer.closeDrawer(GravityCompat.START);
    else super.onBackPressed();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case 98:
        change = true;
        if (resultCode == RESULT_OK && data != null) {
          List<Uri> photoPaths = new ArrayList<>();
          photoPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
          userDB.editUser(email, UserHelper.Attr.AVATAR, photoPaths.get(0).toString());
        }
        break;
      case 99: //FilePickerConst.REQUEST_CODE_PHOTO:
        change = true;
        if (resultCode == RESULT_OK && data != null) {
          List<Uri> photoPaths = new ArrayList<>();
          photoPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
          userDB.editUser(email, UserHelper.Attr.DRIVERS_LICENSE, photoPaths.get(0).toString());
        }
        break;
    }
  }
}