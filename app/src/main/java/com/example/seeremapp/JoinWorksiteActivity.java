package com.example.seeremapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Worksite;
import com.google.android.material.snackbar.Snackbar;

public class JoinWorksiteActivity extends AppCompatActivity {
  private EditText joinInvite;
  private WorksiteDB worksiteDB;

  private static final int REQUEST_CODE_QR_SCAN = 101;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_join_worksite);
    worksiteDB = WorksiteDB.getInstance(this);

    // set refereces
    joinInvite = findViewById(R.id.joinInvite);
    TextView errorInvite = findViewById(R.id.errorInviteMessage);
    Button joinButton = findViewById(R.id.joinButton);
    ImageView QRScanButton = findViewById(R.id.QRScan);

    errorInvite.setVisibility(View.GONE);

    QRScanButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(JoinWorksiteActivity.this, QrCodeActivity.class);
        startActivityForResult(i, REQUEST_CODE_QR_SCAN);
      }
    });

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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode != RESULT_OK) {
      Snackbar.make(findViewById(R.id.JoinWorksiteLayout),
        "Could not retrieve a scan successfully. Please try again", Snackbar.LENGTH_LONG)
        .show();
    }

    if (requestCode == REQUEST_CODE_QR_SCAN) {
      if (data == null) return;

      String res = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
      joinInvite.setText(res.toUpperCase());
    }
  }
}