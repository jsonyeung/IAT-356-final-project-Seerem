package com.example.seeremapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.seeremapp.database.containers.Chat;
import com.example.seeremapp.database.containers.Document;
import com.example.seeremapp.database.containers.Location;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.database.helpers.WorksiteHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    worksite.setRequiresVerify(cursor.getInt((cursor.getColumnIndex(attr.REQUIRES_VERIFY))));
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
      values.put(attr.REQUIRES_VERIFY, "0");
      long wid = db.insert("worksite", null, values);

      // set user as admin to new worksite
      if (wid != -1) {
        values = new ContentValues();
        values.put(attr.WID, wid);
        values.put(attr.USER_EMAIL, user.getEmail());
        values.put(attr.ROLE, "ADMIN");
        values.put(attr.STATUS, "INACTIVE");
        values.put(attr.VERIFIED, "1");
        db.insert("role", null, values);
      }

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
      int needsVerification = cursor.getInt(cursor.getColumnIndex(attr.REQUIRES_VERIFY));

      Log.i("test", "wid: " + wid + " email: " + user.getEmail());
      ContentValues values = new ContentValues();
      values.put(attr.WID, wid);
      values.put(attr.USER_EMAIL, user.getEmail());
      values.put(attr.ROLE, "WORKER");
      values.put(attr.STATUS, "INACTIVE");
      values.put(attr.VERIFIED, (needsVerification > 0) ? "0" : "1");
      db.insert("role", null, values);
      return true;

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return false;
  }

  public boolean deleteWorksite(int wid) {
    db = helper.getWritableDatabase();
    boolean res = db.delete("worksite", attr.WID + " = " + wid, null) > 0;
    return res;
  }

  public boolean leaveWorksite(int wid) {
    try {
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();
      kickUser(user.getEmail(), wid);
    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return false;
  }

  public boolean checkVerified(String email, int wid) {
    if (getWorksite(wid).getRequiresVerify() <= 0) return true;

    try {
      db = helper.getWritableDatabase();

      String query =
              "SELECT *" +
                "FROM worksite W INNER JOIN role R ON W.wid = R.wid " +
                "WHERE W.wid = " + wid + " AND R.user_email = '" + email + "' AND R.verified = 1 " +
                "LIMIT 1;";

      Cursor cursor = db.rawQuery(query, null);
      cursor.moveToFirst();
      if (cursor.getCount() <= 0) return false;
      else return true;
    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return false;
  }

  public boolean checkLoggedVerified(int wid) {
    if (getWorksite(wid).getRequiresVerify() <= 0) return true;

    try {
      db = helper.getWritableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      String query =
        "SELECT *" +
          "FROM worksite W INNER JOIN role R ON W.wid = R.wid " +
          "WHERE W.wid = " + wid + " AND R.user_email = '" + user.getEmail() + "' AND R.verified = 1 " +
          "LIMIT 1;";

      Cursor cursor = db.rawQuery(query, null);
      cursor.moveToFirst();
      if (cursor.getCount() <= 0) return false;
      else return true;
    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return false;
  }

  public void setUserVerify(String email, int wid, boolean verified) {
    db = helper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(attr.VERIFIED, (verified) ? "1" : "0");
    db.update("role", values, attr.WID + "=" + wid, null);
  }

  public void setVerification(int wid, boolean required) {
    db = helper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(attr.REQUIRES_VERIFY, (required) ? "1" : "0");
    db.update("worksite", values, attr.WID + "=" + wid, null);
  }

  public boolean kickUser(String email, int wid) {
    db = helper.getWritableDatabase();
    boolean res = db.delete("role", attr.WID + " = " + wid + " AND " + attr.USER_EMAIL + " = '" + email + "'", null) > 0;
    return res;
  }

  /* Roles */
  public void updateRole(int wid, String email, String role) {
    db = helper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(attr.ROLE, role);
    db.update("role", values, attr.WID + "=" + wid + " AND " + attr.USER_EMAIL + "='" + email + "'", null);
  }

  /* Documents */
  public void addDocument(int wid, String path, String type, String name) {
    db = helper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(attr.WID, wid);
    values.put(attr.DOCUMENT_NAME, name);
    values.put(attr.DOCUMENT, path);
    values.put(attr.TYPE, type);
    db.insert("document", null, values);
  }

  public boolean deleteDocument(int wid, String path) {
    db = helper.getWritableDatabase();
    boolean res = db.delete("document", attr.DOCUMENT + " = '" + path + "';", null) > 0;
    return res;
  }

  /* Locations */
  public void addLocation(int wid, String lastLogged, double lat, double longitude, int steps) {
    try {
      db = helper.getReadableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      ContentValues values = new ContentValues();
      values.put(attr.WID, wid);
      values.put(attr.USER_EMAIL, user.getEmail());
      values.put(attr.LAST_LOGGED, lastLogged);
      values.put(attr.LAT, lat);
      values.put(attr.LONG, longitude);
      values.put(attr.STEPS, steps);
      db.insert("location", null, values);

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }
  }

  /* Chats */
  public void addLoggedUserChatMessage(int wid, String message) {
    try {
      db = helper.getReadableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      String today = formatter.format(new Date());

      ContentValues values = new ContentValues();
      values.put(attr.WID, wid);
      values.put(attr.NAME, user.getFirstName() + " " + user.getLastName());
      values.put(attr.USER_EMAIL, user.getEmail());
      values.put(attr.ROLE, getLoggedUserRole(wid));
      values.put(attr.MESSAGE, message);
      values.put(attr.MESSAGE_DATE, today);
      db.insert("chat", null, values);

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }
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

  public String getLoggedUserRole(int wid) {
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

      String role = cursor.getString(cursor.getColumnIndex(attr.ROLE));
      return role;

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return "WORKER";
  }

  public String getUserRole(String email, int wid) {
    try {
      db = helper.getReadableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getUser(email);

      String query =
        "SELECT *" +
          "FROM worksite W, role R " +
          "WHERE W.wid = R.wid AND R." + attr.USER_EMAIL + " = '" + user.getEmail() + "' AND W.wid = " + wid + " LIMIT 1;";

      Cursor cursor = db.rawQuery(query, null);
      cursor.moveToFirst();
      if (cursor.getCount() <= 0) return "WORKER";

      String role = cursor.getString(cursor.getColumnIndex(attr.ROLE));
      return role;

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return "WORKER";
  }

  public List<Document> getWorksiteDocuments(int wid) {
    List<Document> docs = new ArrayList<>();

    try {
      db = helper.getReadableDatabase();
      String query =
        "SELECT * " +
          "FROM document D " +
          "WHERE D.wid = " + wid + ";";

      Cursor cursor = db.rawQuery(query, null);

      cursor.moveToFirst();
      Log.i("test", "count " + cursor.getCount());
      if (cursor.getCount() <= 0) return docs;
      do {
        Document doc = new Document();
        doc.setId(cursor.getInt(cursor.getColumnIndex(attr.WID)));
        doc.setPath(cursor.getString(cursor.getColumnIndex(attr.DOCUMENT)));
        doc.setName(cursor.getString(cursor.getColumnIndex(attr.DOCUMENT_NAME)));
        doc.setType(cursor.getString(cursor.getColumnIndex(attr.TYPE)));
        docs.add(doc);
      } while (cursor.moveToNext());

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return docs;
  }

  public List<Location> getWorksiteLocations(int wid, boolean userOnly) {
    List<Location> locs = new ArrayList<>();

    try {
      db = helper.getReadableDatabase();
      UserDB userDB = UserDB.getInstance(context);
      User user = userDB.getLoggedUser();

      String query =
              "SELECT * " +
                "FROM location L " +
                "WHERE L.wid = " + wid + ((userOnly) ? (" AND L." + attr.USER_EMAIL + " = '" + user.getEmail() + "'") : "") + ";";

      Cursor cursor = db.rawQuery(query, null);

      cursor.moveToFirst();
      if (cursor.getCount() <= 0) return locs;
      do {
        Location loc = new Location();
        loc.setId(cursor.getInt(cursor.getColumnIndex(attr.WID)));
        loc.setLastLogged(cursor.getString(cursor.getColumnIndex(attr.LAST_LOGGED)));
        loc.setEmail(cursor.getString(cursor.getColumnIndex(attr.USER_EMAIL)));
        loc.setLat(cursor.getDouble(cursor.getColumnIndex(attr.LAT)));
        loc.setLongitude(cursor.getDouble(cursor.getColumnIndex(attr.LONG)));
        loc.setSteps(cursor.getInt(cursor.getColumnIndex(attr.STEPS)));
        locs.add(loc);
      } while (cursor.moveToNext());

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return locs;
  }

  public List<Chat> getWorksiteChats(int wid) {
    List<Chat> chats = new ArrayList<>();

    try {
      db = helper.getReadableDatabase();

      String query =
        "SELECT * " +
        "FROM chat C " +
        "WHERE C.wid = " + wid + ";";

      Cursor cursor = db.rawQuery(query, null);

      cursor.moveToFirst();
      if (cursor.getCount() <= 0) return chats;
      do {
        Chat chat = new Chat();
        chat.setId(cursor.getInt(cursor.getColumnIndex(attr.WID)));
        chat.setName(cursor.getString(cursor.getColumnIndex(attr.NAME)));
        chat.setEmail(cursor.getString(cursor.getColumnIndex(attr.USER_EMAIL)));
        chat.setMessage(cursor.getString(cursor.getColumnIndex(attr.MESSAGE)));
        chat.setRole(cursor.getString(cursor.getColumnIndex(attr.ROLE)));
        chat.setMessageDate(cursor.getString(cursor.getColumnIndex(attr.MESSAGE_DATE)));
        chats.add(chat);
      } while (cursor.moveToNext());

    } catch (Exception err) {
      Log.e("Worksite", err.getMessage());
    }

    return chats;
  }
}
