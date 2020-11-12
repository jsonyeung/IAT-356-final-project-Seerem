package com.example.seeremapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.seeremapp.JoinWorksiteActivity;
import com.example.seeremapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserDashboardHomeFragment extends Fragment {
  private Button joinWorksiteButton, createWorksiteButton;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_user_dashboard_home, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // set references
    joinWorksiteButton = view.findViewById(R.id.worksiteJoin);
    createWorksiteButton = view.findViewById(R.id.worksiteCreate);

    // join a worksite
    joinWorksiteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getContext(), JoinWorksiteActivity.class);
        startActivity(i);
      }
    });


  }
}
