package com.example.seeremapp.database.containers;

public class Location {
  private int id;
  private String email, lastLogged;
  private double lat, longitude;
  private int steps;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLastLogged() {
    return lastLogged;
  }

  public void setLastLogged(String lastLogged) {
    this.lastLogged = lastLogged;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    this.steps = steps;
  }
}
