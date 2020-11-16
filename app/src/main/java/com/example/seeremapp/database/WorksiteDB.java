package com.example.seeremapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.seeremapp.R;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.database.helpers.WorksiteHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

  private Worksite createWorksiteObj(Cursor cursor) {
    Worksite worksite = new Worksite();
    worksite.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(attr.WID))));
    worksite.setCompany(cursor.getString(cursor.getColumnIndex(attr.COMPANY_NAME)));
    worksite.setWorksiteName(cursor.getString(cursor.getColumnIndex(attr.WORKSITE_NAME)));
    worksite.setProjectId(cursor.getString((cursor.getColumnIndex(attr.PROJECT_ID))));
    worksite.setAddress(cursor.getString((cursor.getColumnIndex(attr.ADDRESS))));
    worksite.setLat(cursor.getDouble((cursor.getColumnIndex(attr.ADDRESS_LAT))));
    worksite.setLongitude(cursor.getDouble((cursor.getColumnIndex(attr.ADDRESS_LONG))));
    worksite.setInviteCode(cursor.getString((cursor.getColumnIndex(attr.INVITE))));
    return worksite;
  }

  private String generateInviteCode() {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 10) { // length of invite code.
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }

    String saltStr = salt.toString();
    return saltStr;
  }

  public WorksiteDB(Context context) {
    helper = new WorksiteHelper(context);
    this.context = context;

    db = helper.getReadableDatabase();
  }

  public void addWorksite(String company, String name, String pid, String address, double lat, double longitude) {
    try {
      db = helper.getWritableDatabase();
      ContentValues values;

      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      // Worksite values
      values = new ContentValues();
      values.put(attr.COMPANY_NAME, company);
      values.put(attr.WORKSITE_NAME, name);
      values.put(attr.PROJECT_ID, pid);
      values.put(attr.ADDRESS, address);
      values.put(attr.ADDRESS_LAT, lat);
      values.put(attr.ADDRESS_LONG, longitude);
      values.put(attr.INVITE, generateInviteCode());
      long wid = db.insert("worksite", null, values);

      // set user as admin to new worksite
      if (wid != -1) {
        values = new ContentValues();
        values.put(attr.WID, wid);
        values.put(attr.USER_EMAIL, user.getEmail());
        values.put(attr.PERMISSION, "ADMIN");
        values.put(attr.STATUS, "INACTIVE");
        db.insert("role", null, values);
      }

      db.close();
    } catch(Exception err) {
      Log.e("Worksite", err.getMessage());
    }
  }

  public boolean joinWorksite(String inviteCode) {

    try {
      db = helper.getWritableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      String query =
        "SELECT *" +
          "FROM worksite W " +
          "WHERE W." + attr.INVITE + " = '" + inviteCode + "' LIMIT 1;";

      Cursor cursor = db.rawQuery(query, null);
      cursor.moveToFirst();
      if (cursor.getCount() < 1) return false;
      int wid = cursor.getInt(cursor.getColumnIndex(attr.WID));

      Log.i("test", "wid: " + wid + " email: " + user.getEmail());
      ContentValues values = new ContentValues();
      values.put(attr.WID, wid);
      values.put(attr.USER_EMAIL, user.getEmail());
      values.put(attr.PERMISSION, "WORKER");
      values.put(attr.STATUS, "INACTIVE");
      db.insert("role", null, values);
      db.close();
      return true;

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return false;
  }

  /* Getters */
  public List<Worksite> getUserWorksites() {
    List<Worksite> worksites = new ArrayList<Worksite>();

    try {
      db = helper.getReadableDatabase();

      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      String query =
        "SELECT *" +
        "FROM worksite W INNER JOIN role R on W.wid = R.wid " +
        "WHERE R.user_email = '" + user.getEmail() + "';";

      Cursor cursor = db.rawQuery(query, null);
      cursor.moveToFirst();
      do {
        Worksite worksite = createWorksiteObj(cursor);
        worksites.add(worksite);
      } while (cursor.moveToNext());

      db.close();
    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return worksites;
  }

  public Worksite getWorksite(int wid) {
    db = helper.getReadableDatabase();
    UserDB userDB = UserDB.getInstance(context);

    String query =
      "SELECT *" +
        "FROM worksite W " +
        "WHERE W.wid = " + wid + " " +
        "LIMIT 1;";

    Cursor cursor = db.rawQuery(query, null);
    cursor.moveToFirst();
    if (cursor.getCount() <= 0) return null;

    Worksite worksite = createWorksiteObj(cursor);
    db.close();
    return worksite;
  }

  public List<User> getUsers(int wid) {
    db = helper.getReadableDatabase();
    UserDB userDB = UserDB.getInstance(context);

    String query =
      "SELECT *" +
        "FROM worksite W, role R " +
        "WHERE W.wid = R.wid AND W.wid = " + wid + ";";

    Cursor cursor = db.rawQuery(query, null);

    List<User> users = userDB.getAllUsers();
    List<User> workUsers = new ArrayList<>();

    cursor.moveToFirst();
    do {
      String email = cursor.getString(cursor.getColumnIndex(attr.USER_EMAIL));

      Log.i("test", "email: " + email);

      for (User user : users) {
        if (user.getEmail().equals(email)) {
          workUsers.add(user);
          break;
        }
      }
    } while(cursor.moveToNext());

    return workUsers;
  }

  public String getUserRole(int wid) {
    try {
      db = helper.getReadableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      String query =
        "SELECT *" +
          "FROM worksite W, role R " +
          "WHERE W.wid = R.wid AND R." + attr.USER_EMAIL + " = '" + user.getEmail() + "' AND W.wid = " + wid + " LIMIT 1;";

      Cursor cursor = db.rawQuery(query, null);
      cursor.moveToFirst();
      if (cursor.getCount() <= 0) return "WORKER";

      String role = cursor.getString(cursor.getColumnIndex(attr.PERMISSION));
      db.close();
      return role;

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return "WORKER";
  }
}
