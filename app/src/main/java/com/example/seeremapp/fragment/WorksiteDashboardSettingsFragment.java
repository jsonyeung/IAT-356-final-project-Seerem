package com.example.seeremapp.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.seeremapp.MainActivity;
import com.example.seeremapp.R;
import com.example.seeremapp.WorksiteDashboardActivity;
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

    LinearLayout adminDetails = view.findViewById(R.id.adminDetails);
    LinearLayout supervisorDetails = view.findViewById(R.id.supervisorDetails);
    Button delete = view.findViewById(R.id.deleteWorksite);

    adminDetails.setVisibility(View.GONE);
    supervisorDetails.setVisibility(View.GONE);

    // show options based on role
    final String role = worksiteDB.getLoggedUserRole(worksite.getId());
    switch (role) {
      case "ADMIN":
        adminDetails.setVisibility(View.VISIBLE);
        supervisorDetails.setVisibility(View.VISIBLE);
        break;
      case "SUPERVISOR":
        supervisorDetails.setVisibility(View.VISIBLE);
        delete.setText("Leave Worksite");
        break;
      case "WORKER":
        delete.setText("Leave Worksite");
        break;
    }

    // ADMIN: set join verification
    Switch joinVerify = view.findViewById(R.id.joinVerify);
    joinVerify.setChecked(worksite.getRequiresVerify() > 0);

    joinVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        worksiteDB.setVerification(worksite.getId(), b);
      }
    });

    // ADMIN/SUPERVISOR: Send emergency notification
    EditText emergencyInput = supervisorDetails.findViewById(R.id.emergencyInput);
    Button emergencySend = supervisorDetails.findViewById(R.id.emergencySend);

    emergencySend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String message = emergencyInput.getText().toString().trim();
        if (!message.isEmpty()) {
          sendNotification(
            worksite.getCompany() + " (" + worksite.getWorksiteName() + ") Emergency Notification",
            message,
            new Intent(getContext(), MainActivity.class)
          );

          emergencyInput.setText("");
          Toast.makeText(getActivity(), "Emergency notification sent.", Toast.LENGTH_SHORT).show();
        }
      }
    });

    // delete/leave worksite
    delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new AlertDialog.Builder(getContext())
          .setTitle("Confirm?")
          .setMessage("Are you sure you want to " + ((!role.equals("ADMIN")) ? "leave" : "delete") + " this worksite?")
          .setIcon(android.R.drawable.ic_dialog_alert)
          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              if (!role.equals("ADMIN")) worksiteDB.leaveWorksite(worksite.getId());
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

  public void sendNotification(String title, String message , Intent intent){
    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 , intent,
            PendingIntent.FLAG_ONE_SHOT);

    String channelId = "seerem_channel_id";
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(getContext(), channelId)
              .setSmallIcon(R.mipmap.ic_launcher_round)
              .setContentTitle(title) // getString(R.string.app_name))
              .setContentText(message)
              .setAutoCancel(true)
              .setSound(defaultSoundUri)
              .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
              .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(channelId,
              "Channel human readable title",
              NotificationManager.IMPORTANCE_DEFAULT);
      assert notificationManager != null;
      notificationManager.createNotificationChannel(channel);
    }

    assert notificationManager != null;
    notificationManager.notify(0, notificationBuilder.build());
  }
}