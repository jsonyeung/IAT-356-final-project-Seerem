package com.example.seeremapp.misc;

import android.util.Patterns;
import android.widget.EditText;

import com.example.seeremapp.database.UserDB;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class FormValidator {
  private static final Pattern PASSWORD_PATTERN =
    Pattern.compile("^" +
      "(?=.*[0-9])" +         //at least 1 digit
      "(?=.*[a-zA-Z])" +      // any letter
      // "(?=.*[@#$%^&+=])" +    // at least 1 special character
      "(?=\\S+$)" +           // no white spaces
      ".{10,}" +              // at least 10 characters
      "$");

  public static boolean validateEmail(EditText editText) {
    String input = editText.getText().toString().trim();

    if (!validateNotEmpty(editText)) {
      return false;

    } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
      editText.setError("Please enter a valid email address");
      return false;

    } else if (UserDB.getInstance(editText.getContext()).hasUser(input)) {
      editText.setError("This email is already taken");
      return false;

    } else {
      editText.setError(null);
      return true;
    }
  }

  public static boolean validatePass(EditText editText) {
    String input = editText.getText().toString().trim();

    if (!validateNotEmpty(editText)) {
      return false;

    } else if (!PASSWORD_PATTERN.matcher(input).matches()) {
      editText.setError("Password must contain at least 10 characters and 1 number");
      return false;

    } else {
      editText.setError(null);
      return true;
    }
  }

  public static boolean validateInvite(EditText editText) {
    return true;
  }

  public static boolean validateNotEmpty(EditText editText) {
    String input = editText.getText().toString().trim();
    if (input.isEmpty()) {
      editText.setError("Field can't be empty");
      return false;

    } else {
      editText.setError(null);
      return true;
    }
  }
}
