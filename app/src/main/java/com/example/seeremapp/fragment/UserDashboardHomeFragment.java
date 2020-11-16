package com.example.seeremapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.seeremapp.CreateWorksiteActivity;
import com.example.seeremapp.JoinWorksiteActivity;
import com.example.seeremapp.R;
import com.example.seeremapp.adapter.WorksiteAdapter;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Worksite;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserDashboardHomeFragment extends Fragment {
  private Button joinWorksiteButton, createWorksiteButton;
  private RecyclerView worksiteView;
  private RecyclerView.Adapter worksiteAdapter;
  private RecyclerView.LayoutManager worksiteLayoutManager;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_user_dashboard_home, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    WorksiteDB worksiteDB = WorksiteDB.getInstance(view.getContext());
    List<Worksite> worksites = worksiteDB.getUserWorksites();

    // set references
    joinWorksiteButton = view.findViewById(R.id.worksiteJoin);
    createWorksiteButton = view.findViewById(R.id.worksiteCreate);

    worksiteView = view.findViewById(R.id.worksiteView);
    worksiteView.setHasFixedSize(true);
    worksiteLayoutManager = new LinearLayoutManager(getContext());
    worksiteAdapter = new WorksiteAdapter(getContext(), worksites);

    worksiteView.setLayoutManager(worksiteLayoutManager);
    worksiteView.setAdapter(worksiteAdapter);

    // join a worksite
    joinWorksiteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getContext(), JoinWorksiteActivity.class);
        startActivity(i);
      }
    });

    // create a worksite
    createWorksiteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getContext(), CreateWorksiteActivity.class);
        startActivity(i);
      }
    });
  }
}
