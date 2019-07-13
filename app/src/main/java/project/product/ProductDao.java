package project.product;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ProductDao {

  @Insert
  public void insert(Product... product);

  @Update
  public void update(Product... product);

  @Delete
  public void delete(Product product);

  @Query("DELETE  FROM product WHERE categoryId = :categoryId")
  public void delete(int categoryId);
  @Query("DELETE  FROM product")
  public void delete();


  @Query("SELECT * FROM product")
  public List<Product> getAllProducts();

  @Query("SELECT * FROM product WHERE categoryId=:categoryId")
  public List<Product> getAllProducts(int categoryId);

  @Query("SELECT * FROM product WHERE seen >= 100 ")
  public List<Product> getMostVisite();

  @Query("SELECT * FROM product WHERE posted_date BETWEEN :now AND :past")
  public List<Product> getNewestProducts(Date now, Date past);

  @Query("SELECT * FROM product WHERE title LIKE :title  ")
  public List<Product> getSearchedProducts(String title);


}
