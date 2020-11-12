package com.example.seeremapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.UserDB;

public class MainActivity extends AppCompatActivity {
  private SharedPreferences sharedPrefs;
  private SharedPreferences.Editor editor;
  private EditText emailText, passText;
  private TextView registerText, errorText;
  private CheckBox rememberCheck;
  private Button loginButton;
  private UserDB userDB;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    userDB = UserDB.getInstance(this);

    // references
    emailText = findViewById(R.id.userEmail);
    passText = findViewById(R.id.userPass);
    rememberCheck = findViewById(R.id.userRemember);
    loginButton = findViewById(R.id.userLogin);
    registerText = findViewById(R.id.userRegister);
    errorText = findViewById(R.id.errorLoginMessage);
    errorText.setVisibility(View.GONE);

    sharedPrefs = getSharedPreferences("USER", Context.MODE_PRIVATE);
    editor = sharedPrefs.edit();

    // login functionality
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailText.getText().toString();
        String pass = passText.getText().toString();
        boolean remember = rememberCheck.isChecked();
        errorText.setVisibility(View.GONE);

        try {
          User user = userDB.getUser(email);
          if (!user.getPassword().equals(pass))
            throw new Exception("Login: Password is invalid");

          // save account details for later use
          editor.putString("email", email);
          editor.putString("pass", pass);
          editor.putBoolean("remember", remember);
          editor.commit();

          // go to user dashboard once logged in successfully
          Intent i = new Intent(getApplicationContext(), UserDashboardActivity.class);
          startActivity(i);
          finish();

        } catch(Exception err) {
          Log.e("LOGIN", err.getMessage());
          errorText.setText("Account or password is invalid");
          errorText.setVisibility(View.VISIBLE);
        }
      }
    });

    // sign-up functionality
    registerText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
      Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
      startActivity(i);
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    // auto-login if user checked "remember me"
    if (sharedPrefs.getBoolean("remember", false)) {
      setLoginDetails();
      loginButton.performClick();
    }
  }

  public void setLoginDetails() {
    String email = sharedPrefs.getString("email", "");
    String pass = sharedPrefs.getString("pass", "");
    emailText.setText(email);
    passText.setText(pass);
  }
}