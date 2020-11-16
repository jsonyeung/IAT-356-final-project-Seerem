package com.example.seeremapp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.misc.InviteDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.seeremapp.adapter.WorksitePagerAdapter;

public class WorksiteDashboardActivity extends AppCompatActivity {
  private FloatingActionButton fab;
  private WorksiteDB worksiteDB;
  private Worksite worksite;

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
    dialog.show(getSupportFragmentManager(), "example dialog");
  }
}