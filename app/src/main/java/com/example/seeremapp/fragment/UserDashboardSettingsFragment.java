package com.example.seeremapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.seeremapp.CreateWorksiteActivity;
import com.example.seeremapp.JoinWorksiteActivity;
import com.example.seeremapp.R;
import com.example.seeremapp.adapter.WorksiteAdapter;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.database.helpers.UserHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class UserDashboardSettingsFragment extends Fragment {
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_user_dashboard_settings, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    try {
      UserDB userDB = UserDB.getInstance(getContext());
      WorksiteDB worksiteDB = WorksiteDB.getInstance(getContext());
      User user = userDB.getLoggedUser();

      Switch authSwitch = view.findViewById(R.id.switchAuth);

      // apply settings
      authSwitch.setChecked(user.getFingerAuth() > 0);

      // settings functionality
      authSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          String checked = (b) ? "1" : "0";
          userDB.editUser(user.getEmail(), UserHelper.Attr.FINGER_AUTH, checked);
          Toast.makeText(view.getContext(), "Change applied", Toast.LENGTH_SHORT).show();
        }
      });

    } catch (Exception err) {}
  }
}
