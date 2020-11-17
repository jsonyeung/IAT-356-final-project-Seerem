package com.example.seeremapp.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.R;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.database.helpers.UserHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import droidninja.filepicker.FilePickerBuilder;

public class UserProfileFragment extends Fragment {
  private String email;
  private boolean edit = false;
  private View view;
  private User user, userLogged;
  private DatePickerDialog datePicker;
  private DatePickerDialog.OnDateSetListener datePickerListener;

  public UserProfileFragment(String user_email, boolean edit) {
    email = user_email;
    this.edit = edit;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    view = (View) inflater.inflate(R.layout.fragment_user_profile, container, false);
    displayUserInfo(view);
    return view;
  }

  public void displayUserInfo(View view) {
    try {
      UserDB userDB = UserDB.getInstance(getContext());
      WorksiteDB worksiteDB = WorksiteDB.getInstance(getContext());
      userLogged = userDB.getLoggedUser();
      user = userDB.getUser(email);

      ImageView profileAvatar = view.findViewById(R.id.profileAvatar);
      TextView profileFirstName = view.findViewById(R.id.profileFirstName);
      TextView profileLastName = view.findViewById(R.id.profileLastName);
      TextView profileBirthday = view.findViewById(R.id.profileBirthday);
      TextView profilePhone = view.findViewById(R.id.profilePhone);
      TextView profileEmergencyPhone = view.findViewById(R.id.emergencyPhone);
      ImageView profileDrivers = view.findViewById(R.id.driverLicense);

      // set profile
      setImage(profileAvatar, user.getAvatar());
      profileFirstName.setText(user.getFirstName());
      profileLastName.setText(user.getLastName());
      profileBirthday.setText(user.getBirthday());
      profilePhone.setText("Phone: " + ((user.getPhone() == null) ? "-" : user.getPhone()));
      profileEmergencyPhone.setText("Emergency Phone: " + ((user.getEmergencyPhone() == null) ? "-" : user.getEmergencyPhone()));
      setImage(profileDrivers, user.getDriversLicense());


      if (userLogged.getEmail().equals(email) && edit) {
        Toast.makeText(getContext(), "Click to edit", Toast.LENGTH_SHORT).show();

        profileAvatar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editUserImage(UserHelper.Attr.AVATAR, 98);
          }
        });

        profileDrivers.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editUserImage(UserHelper.Attr.DRIVERS_LICENSE, 99);
          }
        });

        profileFirstName.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editText(UserHelper.Attr.FIRST_NAME, user.getFirstName(), InputType.TYPE_CLASS_TEXT);
          }
        });

        profileLastName.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editText(UserHelper.Attr.LAST_NAME, user.getLastName(), InputType.TYPE_CLASS_TEXT);
          }
        });

        profileBirthday.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editDate(UserHelper.Attr.BIRTHDAY);
          }
        });

        profilePhone.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editText(UserHelper.Attr.PHONE, user.getPhone(), InputType.TYPE_CLASS_PHONE);
          }
        });

        profileEmergencyPhone.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            editText(UserHelper.Attr.EMERGENCY_PHONE, user.getEmergencyPhone(), InputType.TYPE_CLASS_PHONE);
          }
        });

      }

    } catch(Exception err) {}
  }

  private void setImage(ImageView view, String strUri) throws Exception {
    if (strUri == null) return;

    Uri uri = Uri.parse(strUri);
    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
    view.setImageBitmap(bitmap);
  }

  public void editText(String attr, String defaultVal, int type) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("Edit Value");

    // Set up the input
    final EditText input = new EditText(getContext());
    input.setText(defaultVal);

    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.setInputType(InputType.TYPE_CLASS_TEXT | type);
    builder.setView(input);

    // Set up the buttons
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String change = input.getText().toString().trim();

        (UserDB.getInstance(getContext()))
                .editUser(user.getEmail(), attr, change);
        displayUserInfo(view);
        Toast.makeText(getContext(), "Your profile was edited", Toast.LENGTH_SHORT).show();
      }
    });

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });

    builder.show();
  }

  public void editDate(String attr) {
    Calendar c = Calendar.getInstance();

    // Datepicker listener
    datePickerListener =
      new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker dpView, int year, int month, int day) {
          // when dialog box is closed, below method will be called.
          (UserDB.getInstance(getContext()))
                  .editUser(user.getEmail(), attr, month + "-" + day + "-" + year);
          displayUserInfo(view);
          Toast.makeText(getContext(), "Your profile was edited", Toast.LENGTH_SHORT).show();
        }
      };

    // open Datepicker on editText click
    datePicker = new DatePickerDialog(getContext(),
            android.R.style.Theme_Holo_Light_Dialog,
            datePickerListener,
            c.get(Calendar.YEAR), c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH));

    datePicker.setTitle("Select date");
    // date must be before today
    datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
    datePicker.show();
  }

  public void editUserImage(String attr, int code) {
    FilePickerBuilder.getInstance()
      .setMaxCount(1) //optional
      .setActivityTheme(R.style.LibAppTheme) //optional
      .pickPhoto(getActivity(), code);
  }
}
