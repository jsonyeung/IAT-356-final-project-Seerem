package com.example.seeremapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.seeremapp.MainActivity;
import com.example.seeremapp.UserDashboardActivity;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.helpers.UserHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDB {
  private SQLiteDatabase db;
  private final UserHelper helper;
  private final UserHelper.Attr attr = new UserHelper.Attr();
  private Context context;
  private static UserDB instance;

  public static synchronized UserDB getInstance(Context context) {
    // https://guides.codepath.com/android/local-databases-with-sqliteopenhelper#singleton-pattern
    if (instance == null) instance = new UserDB(context.getApplicationContext());
    return instance;
  }

  private User createUserObj(Cursor cursor) {
    User user = new User();
    user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(attr.UID))));
    user.setEmail(cursor.getString(cursor.getColumnIndex(attr.EMAIL)));
    user.setPassword(cursor.getString(cursor.getColumnIndex(attr.PASSWORD)));
    user.setFirstName(cursor.getString((cursor.getColumnIndex(attr.FIRST_NAME))));
    user.setLastName(cursor.getString((cursor.getColumnIndex(attr.LAST_NAME))));
    user.setBirthday(cursor.getString(cursor.getColumnIndex(attr.BIRTHDAY)));
    return user;
  }

  public UserDB(Context context) {
    helper = new UserHelper(context);
    this.context = context;
  }

  public void addUser(String[] params) {
    db = helper.getWritableDatabase();
    ContentValues values = new ContentValues();

    // required attributes to create new user
    values.put(attr.EMAIL, params[0]);
    values.put(attr.PASSWORD, params[1]);
    values.put(attr.BIRTHDAY, params[2]);
    values.put(attr.FIRST_NAME, params[3]);
    values.put(attr.LAST_NAME, params[4]);

    // inserting row
    db.insert(attr.TABLE_NAME, null, values);
    db.close();
  }

  public User getLoggedUser() throws Exception {
    String email = UserDashboardActivity.sharedPrefs.getString("email", "");
    return getUser(email);
  }

  public User getUser(String email) throws Exception {
    db = helper.getReadableDatabase();

    String selection = attr.EMAIL + " = ?";
    String[] selectionArgs = {email};
    Cursor cursor = db.query(attr.TABLE_NAME, null, selection, selectionArgs, null, null, null, "1");

    if (cursor.getCount() < 1)
      throw new Exception("No such user found");

    cursor.moveToFirst();
    return createUserObj(cursor);
  }

  public boolean hasUser(String email) {
    try { getUser(email); return true; }
    catch (Exception err) { return false; }
  }

  public List<User> getAllUsers() {
    db = helper.getReadableDatabase();
    Cursor cursor = db.query(attr.TABLE_NAME, null, null, null, null, null, null);

    List<User> users = new ArrayList<User>();
    cursor.moveToFirst();
    do {
      User user = createUserObj(cursor);
      users.add(user);
    } while (cursor.moveToNext());

    db.close();
    return users;
  }
}
