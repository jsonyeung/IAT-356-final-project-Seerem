package com.example.seeremapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.UserDB;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
  private SharedPreferences sharedPrefs;
  private SharedPreferences.Editor editor;
  private EditText emailText, passText;
  private TextView registerText, errorText;
  private CheckBox rememberCheck;
  private Button loginButton;
  private UserDB userDB;

  private static int LAUNCH_REGISTER = 1;

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

          if (user.getFingerAuth() <= 0) {
            // go to user dashboard once logged in successfully
            Intent i = new Intent(getApplicationContext(), UserDashboardActivity.class);
            startActivity(i);
            finish();

          } else {
            // secondary auth required?
            FingerprintDialog.initialize(view.getContext())
              .title("Identification")
              .message("This account requires secondary identification to login.")
              .callback(new FingerprintDialogCallback() {
                @Override
                public void onAuthenticationSucceeded() {
                  // go to user dashboard once logged in successfully
                  Intent i = new Intent(getApplicationContext(), UserDashboardActivity.class);
                  startActivity(i);
                  finish();
                }

                @Override
                public void onAuthenticationCancel() {
                  Snackbar.make(findViewById(R.id.mainLayout),
                    "Secondary identification unsuccessful", Snackbar.LENGTH_LONG)
                    .show();
                }
              }).show();
          }

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
        startActivityForResult(i, LAUNCH_REGISTER);
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    Log.i("LOGIN", "remember?: " + sharedPrefs.getBoolean("remember", false));

    // auto-login if user checked "remember me"
    if (sharedPrefs.getBoolean("remember", false)) {
      setLoginDetails();
      loginButton.performClick();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == LAUNCH_REGISTER) {
      if (resultCode == RESULT_OK){
        Snackbar.make(findViewById(R.id.mainLayout),
          "Account successfully created!", Snackbar.LENGTH_LONG)
            .show();

        editor.putString("email", data.getStringExtra("email"));
        editor.putString("pass", data.getStringExtra("pass"));
        editor.commit();
        setLoginDetails();
      }
    }
  }

  public void setLoginDetails() {
    String email = sharedPrefs.getString("email", "");
    String pass = sharedPrefs.getString("pass", "");
    emailText.setText(email);
    passText.setText(pass);
  }
}