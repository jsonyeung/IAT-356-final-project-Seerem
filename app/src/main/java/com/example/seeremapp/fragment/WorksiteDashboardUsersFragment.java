package com.example.seeremapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seeremapp.R;
import com.example.seeremapp.adapter.UsersAdapter;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorksiteDashboardUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorksiteDashboardUsersFragment extends Fragment {
  private WorksiteDB worksiteDB;
  private Worksite worksite;
  private RecyclerView usersView;
  private RecyclerView.Adapter usersAdapter;
  private RecyclerView.LayoutManager usersLayoutManager;

  public WorksiteDashboardUsersFragment() {
    // Required empty public constructor
  }

  // TODO: Rename and change types and number of parameters
  public static WorksiteDashboardUsersFragment newInstance(int wid) {
    WorksiteDashboardUsersFragment fragment = new WorksiteDashboardUsersFragment();
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
    View view = (View) inflater.inflate(R.layout.fragment_worksite_dashboard_users, container, false);

    List<User> users = worksiteDB.getUsers(worksite.getId());

    // set references
    usersView = (RecyclerView) view.findViewById(R.id.usersView);
    usersAdapter = new UsersAdapter(getContext(), users, worksite.getId());
    usersLayoutManager = new LinearLayoutManager(getContext());

    usersView.setLayoutManager(usersLayoutManager);
    usersView.setAdapter(usersAdapter);

    // Inflate the layout for this fragment
    return view;
  }
}