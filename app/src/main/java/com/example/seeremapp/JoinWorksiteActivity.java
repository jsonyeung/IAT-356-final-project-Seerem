package com.example.seeremapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Worksite;

public class JoinWorksiteActivity extends AppCompatActivity {
  private WorksiteDB worksiteDB;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_join_worksite);
    worksiteDB = WorksiteDB.getInstance(this);

    TextView errorInvite = findViewById(R.id.errorInviteMessage);
    EditText joinInvite = findViewById(R.id.joinInvite);
    Button joinButton = findViewById(R.id.joinButton);

    errorInvite.setVisibility(View.GONE);

    joinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String code = joinInvite.getText().toString().toUpperCase();
        errorInvite.setVisibility(View.GONE);

        if (worksiteDB.joinWorksite(code)) {
          Toast.makeText(getApplicationContext(), "You have successfully joined the worksite", Toast.LENGTH_LONG).show();
          finish();
        } else {
          errorInvite.setVisibility(View.VISIBLE);
          errorInvite.setText("There was an error with the code. Please try again.");
        }
      }
    });
  }
}