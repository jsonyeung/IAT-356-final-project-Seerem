package com.example.seeremapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.misc.FormValidator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* User Registration */
public class RegisterActivity extends AppCompatActivity {
  private EditText emailText, passText, firstNameText, lastNameText, birthdayText, inviteCodeText;
  private DatePickerDialog datePicker;
  private DatePickerDialog.OnDateSetListener datePickerListener;
  private Button registerButton;
  private UserDB userDB;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    userDB = UserDB.getInstance(this);

    // set references
    emailText = findViewById(R.id.userEmail);
    passText = findViewById(R.id.userPass);
    firstNameText = findViewById(R.id.regFirstName);
    lastNameText = findViewById(R.id.regLastName);
    birthdayText = findViewById(R.id.regBirthday);
    inviteCodeText = findViewById(R.id.regInvite);
    registerButton = findViewById(R.id.regSignUp);
    setupDatePicker();

    // sign up functionality
    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        List<Boolean> valid = new ArrayList<Boolean>();

        // validate all inputs
        valid.add(FormValidator.validateEmail(emailText));
        valid.add(FormValidator.validatePass(passText));
        valid.add(FormValidator.validateNotEmpty(firstNameText));
        valid.add(FormValidator.validateNotEmpty(lastNameText));
        valid.add(FormValidator.validateNotEmpty(birthdayText));

        if (!valid.contains(false)) {
          userDB.addUser(new String[] {
            emailText.getText().toString().trim(),
            passText.getText().toString().trim(),
            birthdayText.getText().toString().trim(),
            firstNameText.getText().toString().trim(),
            lastNameText.getText().toString().trim()
          });

          Intent returnIntent = new Intent();
          returnIntent.putExtra("email", emailText.getText().toString().trim());
          returnIntent.putExtra("pass", passText.getText().toString().trim());
          setResult(RESULT_OK, returnIntent);
          finish();
        } else {
          Snackbar.make(findViewById(R.id.registerLayout),
        "There are errors with your registration", Snackbar.LENGTH_LONG)
            .show();
        }
      }
    });
  }

  private void setupDatePicker() {
    final Context context = this;
    birthdayText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar c = Calendar.getInstance();

        // Datepicker listener
        datePickerListener =
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
              // when dialog box is closed, below method will be called.
              birthdayText.setText(month + "-" + day + "-" + year);
            }
          };

        // open Datepicker on editText click
        datePicker = new DatePickerDialog(context,
                android.R.style.Theme_Holo_Light_Dialog,
                datePickerListener,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        datePicker.setTitle("Select date");
        // date must be before today
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
      }
    });
  }
}