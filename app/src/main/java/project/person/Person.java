package project.person;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "person")
public class Person {

  @PrimaryKey
  private int id;
  private int isMentor;
  private String name;
  private String img;
  private String desc;


  public Person() {
  }

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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public int getIsMentor() {
    return isMentor;
  }

  public void setIsMentor(int isMentor) {
    this.isMentor = isMentor;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  @Override
  public String toString() {
    return "Person{" +
      "name='" + name + '\'' +
      '}';
  }
}
