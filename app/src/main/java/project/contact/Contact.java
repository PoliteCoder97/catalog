package project.contact;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "contact")
public class Contact {

  @PrimaryKey
  private int id;
  private int personId;
  private String phoneNumber;
  private String telegram;
  private String whatsApp;
  private String facebook;
  private String email;
  private String web;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPersonId() {
    return personId;
  }

  public void setPersonId(int personId) {
    this.personId = personId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getTelegram() {
    return telegram;
  }

  public void setTelegram(String telegram) {
    this.telegram = telegram;
  }

  public String getWhatsApp() {
    return whatsApp;
  }

  public void setWhatsApp(String whatsApp) {
    this.whatsApp = whatsApp;
  }

  public String getFacebook() {
    return facebook;
  }

  public void setFacebook(String facebook) {
    this.facebook = facebook;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getWeb() {
    return web;
  }

  public void setWeb(String web) {
    this.web = web;
  }

  @Override
  public String toString() {
    return "Contact{" +
      "phoneNumber='" + phoneNumber + '\'' +
      '}';
  }
}
