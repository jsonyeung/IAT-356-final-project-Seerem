package com.example.seeremapp.database.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WorksiteHelper extends SQLiteOpenHelper {
  private Context context;

  // 3 tables:
  // - Worksites (wid)
  // - wid -> Roles
  // - wid -> Documents
  // - wid -> Location

  /* table attributes */
  public static class Attr {
    public static final String
      DB_NAME = "Worksites.db";

    // Worksite attributes
    public static final String
      WID = "wid",
      COMPANY_NAME = "company_name",
      WORKSITE_NAME = "worksite_name",
      PROJECT_ID = "project_id",
      ADDRESS = "address",
      ADDRESS_LAT = "address_lat",
      ADDRESS_LONG = "address_long",
      INVITE = "invite_code";

    // Role attributes
    public static final String
      // WID = "wid"
      USER_EMAIL = "user_email",
      PERMISSION = "permission",
      STATUS = "status";

    // Document attributes
    public static final String
      // WID = "wid"
      DOCUMENT = "document",
      DOCUMENT_NAME = "document_name",
      TYPE = "document_type";

    // Location attributes
    public static final String
      // WID = "wid"
      // USER_EMAIL = "user_email"
      LAST_LOGGED = "last_logged",
      LAT = "lat",
      LONG = "long";
  }

  public WorksiteHelper(Context context){
    super(context, Attr.DB_NAME, null, 1);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String WORKSITE_TABLE =
      "CREATE TABLE " +
        "worksite (" +
        Attr.WID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        Attr.WORKSITE_NAME + " TEXT NOT NULL, " +
        Attr.COMPANY_NAME + " TEXT NOT NULL, " +
        Attr.PROJECT_ID + " TEXT NOT NULL, " +
        Attr.ADDRESS + " TEXT NOT NULL, " +
        Attr.ADDRESS_LAT + " REAL NOT NULL, " +
        Attr.ADDRESS_LONG + " REAL NOT NULL, " +
        Attr.INVITE + " TEXT NOT NULL UNIQUE " +
      ")";

    String ROLES_TABLE =
      "CREATE TABLE " +
        "role (" +
        Attr.WID + " INTEGER NOT NULL, " +
        Attr.USER_EMAIL + " TEXT NOT NULL, " +
        Attr.PERMISSION + " TEXT NOT NULL, " +
        Attr.STATUS + " TEXT NOT NULL " +
      ")";

    String DOCUMENT_TABLE =
      "CREATE TABLE " +
        "document (" +
        Attr.WID + " INTEGER NOT NULL, " +
        Attr.DOCUMENT + " TEXT NOT NULL, " +
        Attr.TYPE + " TEXT NOT NULL, " +
        Attr.DOCUMENT_NAME + " TEXT " +
      ")";

    String LOCATION_TABLE =
      "CREATE TABLE " +
        "location (" +
          Attr.WID + " INTEGER NOT NULL, " +
          Attr.USER_EMAIL + " TEXT NOT NULL, " +
          Attr.LAST_LOGGED + " DATE, " +
          Attr.LAT + " REAL NOT NULL, " +
          Attr.LONG + " REAL NOT NULL " +
        ")";

    try {
      db.execSQL(WORKSITE_TABLE);
      db.execSQL(ROLES_TABLE);
      db.execSQL(DOCUMENT_TABLE);
      db.execSQL(LOCATION_TABLE);

    } catch (SQLException err) {
      Log.e(UserHelper.Attr.DB_NAME, err.getMessage());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    String DROP_TABLE = "DROP TABLE IF EXISTS " + UserHelper.Attr.TABLE_NAME;

    try {
      db.execSQL(DROP_TABLE);
      onCreate(db);
      Log.i(UserHelper.Attr.DB_NAME, "Table \"" + UserHelper.Attr.TABLE_NAME + "\" upgraded");
    } catch (SQLException e) {
      Log.e(UserHelper.Attr.DB_NAME, "Failed to upgrade table \"" + UserHelper.Attr.TABLE_NAME + "\"");
    }
  }
}
