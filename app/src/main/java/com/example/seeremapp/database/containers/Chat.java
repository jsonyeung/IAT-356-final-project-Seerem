package com.example.seeremapp.database.containers;

public class Chat {
  private int id;
  private String name, email, role, message, messageDate;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessageDate() {
    return messageDate;
  }

  public void setMessageDate(String messageDate) {
    this.messageDate = messageDate;
  }
}
