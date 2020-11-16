package com.example.seeremapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seeremapp.R;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Worksite;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorksiteDashboardSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorksiteDashboardSettingsFragment extends Fragment {
  private WorksiteDB worksiteDB;
  private Worksite worksite;

  public WorksiteDashboardSettingsFragment() {
    // Required empty public constructor
  }

  // TODO: Rename and change types and number of parameters
  public static WorksiteDashboardSettingsFragment newInstance(int wid) {
    WorksiteDashboardSettingsFragment fragment = new WorksiteDashboardSettingsFragment();
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
    View view = (View) inflater.inflate(R.layout.fragment_worksite_dashboard_settings, container, false);



    // Inflate the layout for this fragment
    return view;
  }
}