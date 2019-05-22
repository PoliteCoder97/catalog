package project.product;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "product")
public class Product {

  @PrimaryKey
  private int id;
  private String title;
  private String desc;
  private String img;
  private String price;
  private int categoryId;
  private int seen;
  private Date posted_date;

  public int getSeen() {
    return seen;
  }
  public void setSeen(int seen) {
    this.seen = seen;
  }
  public Date getPosted_date() {
    return posted_date;
  }

  public void setPosted_date(Date posted_date) {
    this.posted_date = posted_date;
  }


  public Product() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  @Override
  public String toString() {
    return getTitle();
  }
}
