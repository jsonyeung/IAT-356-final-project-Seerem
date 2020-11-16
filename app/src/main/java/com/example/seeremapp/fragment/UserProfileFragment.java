package com.example.seeremapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.seeremapp.R;
import com.example.seeremapp.RegisterActivity;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.containers.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment {
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = (View) inflater.inflate(R.layout.fragment_user_profile, container, false);

    try {
      UserDB userDB = UserDB.getInstance(getContext());
      User user = userDB.getLoggedUser();

      Button editUserProfile = (Button) view.findViewById(R.id.editUserProfileButton);
      editUserProfile.setVisibility(View.VISIBLE);

      editUserProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i = new Intent(getContext(), RegisterActivity.class);
          startActivity(i);
        }
      });

    } catch(Exception err) {}
    return view;
  }
}
