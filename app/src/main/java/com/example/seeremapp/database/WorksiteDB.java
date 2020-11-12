package com.example.seeremapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.helpers.WorksiteHelper;

public class WorksiteDB {
  private SQLiteDatabase db;
  private final WorksiteHelper helper;
  private final WorksiteHelper.Attr attr = new WorksiteHelper.Attr();
  private Context context;
  private static WorksiteDB instance;

  public static synchronized WorksiteDB getInstance(Context context) {
    // https://guides.codepath.com/android/local-databases-with-sqliteopenhelper#singleton-pattern
    if (instance == null) instance = new WorksiteDB(context.getApplicationContext());
    return instance;
  }

  public WorksiteDB(Context context) {
    helper = new WorksiteHelper(context);
    this.context = context;
  }

  public void addWorksite() {
    db = helper.getWritableDatabase();
    ContentValues values;

    // Worksite values
    values = new ContentValues();
    values.put(attr.BRANCH_NAME, "Vancouver");
    values.put(attr.ADDRESS, "1234 Gilbert Rd., Vancouver, BC");
    values.put(attr.INVITE, "5KS3SZO70A");
    long wid = db.insert("worksite", null, values);

    // set user as admin to new worksite
    if (wid != -1) {
      values = new ContentValues();
      values.put(attr.WID, wid);
      values.put(attr.USER_EMAIL, "jsonyeung@contact.me");
      values.put(attr.PERMISSION, "ADMIN");
      values.put(attr.STATUS, "INACTIVE");
      db.insert("role", null, values);
    }

    db.close();
  }

  /* Getters */
  public void getUserWorksites(User user) {
    db = helper.getReadableDatabase();

    String query =
      "SELECT *" +
      "FROM worksite INNER JOIN role " +
      "WHERE user_email = " + user.getEmail() + ";";

    Cursor cursor = db.rawQuery(query, null);
    db.close();
  }
}
