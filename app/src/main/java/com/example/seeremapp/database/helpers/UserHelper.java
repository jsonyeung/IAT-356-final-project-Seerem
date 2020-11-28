package com.example.seeremapp.database.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class UserHelper extends SQLiteOpenHelper {
  private Context context;

  /* table attributes */
  public static class Attr {
    public static final String
      DB_NAME = "Users.db",
      TABLE_NAME = "users";

    public static final String
      UID = "uid",
      EMAIL = "email",
      PASSWORD = "password",
      FIRST_NAME = "first_name",
      LAST_NAME = "last_name",
      BIRTHDAY = "birthday",
      PHONE = "phone",
      EMERGENCY_PHONE = "emergency_phone",
      DRIVERS_LICENSE = "driver_license",
      AVATAR = "avatar",

      // settings
      FINGER_AUTH = "finger_auth";
  }

  public UserHelper(Context context){
    super(context, Attr.DB_NAME, null, 1);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_TABLE =
      "CREATE TABLE " +
        Attr.TABLE_NAME + " (" +
        Attr.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        Attr.EMAIL + " TEXT NOT NULL UNIQUE, " +
        Attr.PASSWORD + " TEXT NOT NULL, " +
        Attr.FIRST_NAME + " TEXT," +
        Attr.LAST_NAME + " TEXT," +
        Attr.BIRTHDAY + " TEXT, " +
        Attr.PHONE + " TEXT, " +
        Attr.EMERGENCY_PHONE + " TEXT, " +
        Attr.DRIVERS_LICENSE + " TEXT, " +
        Attr.AVATAR + " TEXT, " +
        Attr.FINGER_AUTH + " INTEGER " +
      ")";

    try {
      db.execSQL(CREATE_TABLE);
      Log.i(Attr.DB_NAME, "Table \"" + Attr.TABLE_NAME + "\" created");
    } catch (SQLException err) {
      Log.e(Attr.DB_NAME, "Failed to create table \"" + Attr.TABLE_NAME + "\"");
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    String DROP_TABLE = "DROP TABLE IF EXISTS " + Attr.TABLE_NAME;

    try {
      db.execSQL(DROP_TABLE);
      onCreate(db);
      Log.i(Attr.DB_NAME, "Table \"" + Attr.TABLE_NAME + "\" upgraded");
    } catch (SQLException e) {
      Log.e(Attr.DB_NAME, "Failed to upgrade table \"" + Attr.TABLE_NAME + "\"");
    }
  }
}
