package project.person;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "personCotact")
public class PersonContact {
  @PrimaryKey(autoGenerate = true)
  private int id;
  private int personId;
  private int isMentor;
  private String name;
  private String desc;
  private String phoneNumber;
  private String telegram;
  private String whatsApp;
  private String facebool;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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

  public String getFacebool() {
    return facebool;
  }

  public void setFacebool(String facebool) {
    this.facebool = facebool;
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
  public int getIsMentor() {
    return isMentor;
  }

  public void setIsMentor(int isMentor) {
    this.isMentor = isMentor;
  }
}
