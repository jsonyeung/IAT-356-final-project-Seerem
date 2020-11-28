package com.example.seeremapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import droidninja.filepicker.FilePickerBuilder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.PingActivity;
import com.example.seeremapp.R;
import com.example.seeremapp.WorksiteDashboardActivity;
import com.example.seeremapp.adapter.DocumentAdapter;
import com.example.seeremapp.adapter.LocationAdapter;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Location;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.misc.AddLinkActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorksiteDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorksiteDashboardFragment extends Fragment {
  private WorksiteDB worksiteDB;
  private Worksite worksite;

  public WorksiteDashboardFragment() {
    // Required empty public constructor
  }

  // TODO: Rename and change types and number of parameters
  public static WorksiteDashboardFragment newInstance(int wid) {
    WorksiteDashboardFragment fragment = new WorksiteDashboardFragment();
    Bundle args = new Bundle();
    args.putInt("WID", wid);
    fragment.setArguments(args);
    return fragment;
  }

  // FRAGMENT ACTIVITY
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    worksiteDB = WorksiteDB.getInstance(getContext());

    if (getArguments() != null) {
      int wid = getArguments().getInt("WID");
      worksite = worksiteDB.getWorksite(wid);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = (View) inflater.inflate(R.layout.fragment_worksite_dashboard, container, false);

    TextView worksiteName = (TextView) view.findViewById(R.id.currentWorksite);
    TextView projectId = (TextView) view.findViewById(R.id.currentProjectId);

    String curRole = worksiteDB.getLoggedUserRole(worksite.getId());
    RecyclerView documentView = view.findViewById(R.id.documentView);
    RecyclerView.Adapter documentAdapter = new DocumentAdapter(getContext(),
      worksiteDB.getWorksiteDocuments(worksite.getId()),
      curRole
    );
    RecyclerView.LayoutManager documentLayoutManager = new LinearLayoutManager(getContext());
    documentView.setLayoutManager(documentLayoutManager);
    documentView.setAdapter(documentAdapter);

    List<Location> locations = worksiteDB.getWorksiteLocations(worksite.getId(), curRole.equals("WORKER"));

    RecyclerView locationView = view.findViewById(R.id.locationView);
    RecyclerView.Adapter locationAdapter = new LocationAdapter(getContext(), locations);
    RecyclerView.LayoutManager locationLayoutManager = new LinearLayoutManager(getContext());
    locationView.setLayoutManager(locationLayoutManager);
    locationView.setAdapter(locationAdapter);

    Button addDocs = view.findViewById(R.id.addDocument);
    Button addLink = view.findViewById(R.id.addLink);
    Button pinger = view.findViewById(R.id.pingLocation);
    MapView mapView = view.findViewById(R.id.worksiteMap);

    // set values
    worksiteName.setText("Worksite: " + worksite.getWorksiteName());
    projectId.setText("Project ID: " + worksite.getProjectId());

    // map view of worksite
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap map) {
        if (map != null) {
          map.addMarker(new MarkerOptions()
                  .position(new LatLng(worksite.getLat(), worksite.getLongitude()))
                  .title("Worksite")
          );
        }
      }
    });

    // add docs
    addDocs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FilePickerBuilder.getInstance()
          .setMaxCount(1) //optional
          .setActivityTheme(R.style.LibAppTheme) //optional
          .pickFile(getActivity());
      }
    });

    // add Link
    addLink.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getActivity(), AddLinkActivity.class);
        getActivity().startActivityForResult(i, WorksiteDashboardActivity.REQUEST_LINK);
      }
    });

    // ping location
    pinger.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getActivity(), "Location pinged", Toast.LENGTH_LONG);
        Intent i = new Intent(getActivity(), PingActivity.class);
        getActivity().startActivityForResult(i, WorksiteDashboardActivity.REQUEST_PING);
      }
    });

    // show/hide certain elements depending on user's role
    String role = worksiteDB.getLoggedUserRole(worksite.getId());
    switch (role) {
      case "ADMIN":
        (view.findViewById(R.id.addDocument)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.addLink)).setVisibility(View.VISIBLE);
        break;
    }

    // Inflate the layout for this fragment
    return view;
  }
}