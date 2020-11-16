package com.example.seeremapp.database.containers;

public class Worksite {
  private int id;
  private String company, worksiteName, projectId, address, inviteCode;
  private double lat;

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

  private double longitude;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getWorksiteName() {
    return worksiteName;
  }

  public void setWorksiteName(String worksiteName) {
    this.worksiteName = worksiteName;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }
}
