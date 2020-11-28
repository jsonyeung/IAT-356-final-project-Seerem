package com.example.seeremapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Document;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.misc.InviteDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import droidninja.filepicker.FilePickerConst;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.seeremapp.adapter.WorksitePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorksiteDashboardActivity extends AppCompatActivity {
  private FloatingActionButton fab;
  private WorksiteDB worksiteDB;
  private Worksite worksite;

  public static final int REQUEST_LINK = 0;
  public static final int REQUEST_PING = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_worksite_dashboard);
    worksiteDB = worksiteDB.getInstance(this);

    // get worksite information for dashboard
    Intent i = getIntent();
    int wid = i.getIntExtra("wid", -1);
    worksite = worksiteDB.getWorksite(wid);

    WorksitePagerAdapter sectionsPagerAdapter = new WorksitePagerAdapter(this, getSupportFragmentManager(), wid);
    ViewPager viewPager = findViewById(R.id.view_pager);
    viewPager.setAdapter(sectionsPagerAdapter);

    TabLayout tabs = findViewById(R.id.tabs);
    tabs.setupWithViewPager(viewPager);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(worksite.getCompany());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.worksite_dashboard_toolbar_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.invite:
        openInviteDialog();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void openInviteDialog() {
    InviteDialog dialog = new InviteDialog(worksite.getInviteCode());
    dialog.show(getSupportFragmentManager(), "Invite dialog");
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
        switch (requestCode) {
          case FilePickerConst.REQUEST_CODE_DOC:
            Toast.makeText(this, "new file added", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK && data != null) {
              List<Uri> docPaths = new ArrayList<>();
              docPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));

              AlertDialog.Builder builder = new AlertDialog.Builder(this);
              builder.setTitle("Enter Filename");

              // Set up the input
              final EditText input = new EditText(this);

              // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
              input.setInputType(InputType.TYPE_CLASS_TEXT);
              builder.setView(input);

              // Set up the buttons
              builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  String docName = input.getText().toString().trim();
                  worksiteDB.addDocument(worksite.getId(), docPaths.get(0).toString(), "DOC", docName);
                  recreate();
                }
              });

              builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              });

              builder.show();
            }
            break;
          case FilePickerConst.REQUEST_CODE_PHOTO:
            Toast.makeText(this, "new photo added", Toast.LENGTH_SHORT).show();
            recreate();
            break;
          case REQUEST_LINK:
            String url = data.getStringExtra("link");
            worksiteDB.addDocument(worksite.getId(), url, "URL", null);
            Toast.makeText(this, "new url added", Toast.LENGTH_SHORT).show();
            recreate();
            break;
          case REQUEST_PING:
            double lat = data.getDoubleExtra("lat", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);
            int steps = data.getIntExtra("steps", 0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String today = formatter.format(new Date());

            worksiteDB.addLocation(worksite.getId(), today, lat, longitude, steps);
            Log.i("test", "lat " + lat + ", long: " + longitude);
            Toast.makeText(this, "new ping added", Toast.LENGTH_SHORT).show();
            recreate();
            break;
      }
    }
  }
}