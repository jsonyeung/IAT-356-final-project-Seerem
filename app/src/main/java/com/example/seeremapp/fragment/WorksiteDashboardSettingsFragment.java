package com.example.seeremapp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    Button delete = view.findViewById(R.id.deleteWorksite);


    // delete/leave worksite
    final String role = worksiteDB.getUserRole(worksite.getId());
    switch (role) {
      case "WORKER":
        delete.setText("Leave Worksite");
        break;
      default: break;
    }

    delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new AlertDialog.Builder(getContext())
          .setTitle("Confirm?")
          .setMessage("Are you sure you want to " + ((role.equals("WORKER")) ? "leave" : "delete") + " this worksite?")
          .setIcon(android.R.drawable.ic_dialog_alert)
          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              if (role.equals("WORKER")) worksiteDB.leaveWorksite(worksite.getId());
              else worksiteDB.deleteWorksite(worksite.getId());
              getActivity().finish();
            }
          })
          .setNegativeButton(android.R.string.no, null)
          .show();
      }
    });



    // Inflate the layout for this fragment
    return view;
  }
}