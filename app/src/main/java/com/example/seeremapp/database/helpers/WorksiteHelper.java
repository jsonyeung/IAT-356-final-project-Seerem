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

  /* table attributes */
  public static class Attr {
    public static final String
      DB_NAME = "Worksites.db";

    // Worksite attributes
    public static final String
      WID = "wid",
      BRANCH_NAME = "branch_name",
      ADDRESS = "address",
      INVITE = "invite_code";

    // Role attributes
    public static final String
      // WID = "wid"
      USER_EMAIL = "user_email",
      PERMISSION = "permission",
      STATUS = "status";
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
        Attr.BRANCH_NAME + " TEXT NOT NULL, " +
        Attr.ADDRESS + " TEXT NOT NULL, " +
        Attr.INVITE + " TEXT NOT NULL UNIQUE " +
      ")";

    String ROLES_TABLE =
      "CREATE TABLE " +
        "role (" +
        Attr.WID + " INTEGER NOT NULL UNIQUE, " +
        Attr.USER_EMAIL + " TEXT NOT NULL, " +
        Attr.PERMISSION + " TEXT NOT NULL, " +
        Attr.STATUS + " TEXT NOT NULL " +
      ")";

    try {
      db.execSQL(WORKSITE_TABLE);
      db.execSQL(ROLES_TABLE);
    } catch (SQLException err) {
      Log.e(UserHelper.Attr.DB_NAME, "Failed to create Worksite tables");
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
