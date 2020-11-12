package com.example.seeremapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.misc.FormValidator;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    emailText = findViewById(R.id.regEmail);
    passText = findViewById(R.id.regPass);
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
            emailText.getText().toString(),
            passText.getText().toString(),
            birthdayText.getText().toString(),
            firstNameText.getText().toString(),
            lastNameText.getText().toString()
          });

          finish();
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