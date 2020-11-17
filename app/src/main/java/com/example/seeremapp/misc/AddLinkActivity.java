package com.example.seeremapp.misc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.seeremapp.R;

public class AddLinkActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_link);

    EditText editURL = findViewById(R.id.editURL);
    Button addURL = findViewById(R.id.addURL);

    addURL.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent();
        i.putExtra("link", editURL.getText().toString().trim());

        setResult(RESULT_OK, i);
        finish();
      }
    });
  }
}