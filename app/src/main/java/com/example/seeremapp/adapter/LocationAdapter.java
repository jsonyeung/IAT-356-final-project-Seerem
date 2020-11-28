package com.example.seeremapp.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.CreateWorksiteActivity;
import com.example.seeremapp.R;
import com.example.seeremapp.UserProfileActivity;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Document;
import com.example.seeremapp.database.containers.User;
import com.schibstedspain.leku.LocationPickerActivity;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
  private List<com.example.seeremapp.database.containers.Location> mData;
  private LayoutInflater mInflater;

  // data is passed into the constructor
  public LocationAdapter(Context context, List<com.example.seeremapp.database.containers.Location> data) {
    this.mInflater = LayoutInflater.from(context);
    this.mData = data;
  }

  // inflates the row layout from xml when needed
  @Override
  public LocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.card_li, parent, false);
    return new LocationAdapter.ViewHolder(view);
  }

  // binds the data to the TextView in each row
  @Override
  public void onBindViewHolder(LocationAdapter.ViewHolder holder, int position) {
    com.example.seeremapp.database.containers.Location loc = mData.get(position);

    holder.heading.setText(loc.getEmail());
    holder.caption.setText("Last logged: " + loc.getLastLogged() + "\nlat: " + loc.getLat() + ", long: " + loc.getLongitude() + "\nsteps: " + loc.getSteps());

    // View location data
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent locationPickerIntent = (new LocationPickerActivity.Builder())
                .withGeolocApiKey(view.getContext().getResources().getString(R.string.map_key))
                .shouldReturnOkOnBackPressed()
                .withSatelliteViewHidden()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .build(view.getContext());

        view.getContext().startActivity(locationPickerIntent);
      }
    });
  }

  // total number of rows
  @Override
  public int getItemCount() {
    return mData.size();
  }

  // stores and recycles views as they are scrolled off screen
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView heading, caption;
    ImageView avatar, delete;

    ViewHolder(View itemView) {
      super(itemView);
      heading = itemView.findViewById(R.id.heading);
      caption = itemView.findViewById(R.id.caption);
      avatar = itemView.findViewById(R.id.avatar);
      delete = itemView.findViewById(R.id.closeButton);

      avatar.setVisibility(View.GONE);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    }
  }
}
