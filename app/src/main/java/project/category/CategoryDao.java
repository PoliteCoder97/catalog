package project.category;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
  @Insert
  public void insert(Category... categories);

  @Update
  public void update(Category... categories);

  @Delete
  public void delete(Category category);

  @Query("DELETE  FROM category")
  public void delete();


  @Query("SELECT * FROM category")
  public List<Category> getCategoryList();

  @Query("SELECT * FROM category WHERE parentId = :parentId")
  public List<Category> getCategoryList(int parentId);

  @Query("SELECT * FROM category WHERE title LIKE :title")
  public List<Category> getSearchedCategoryList(String title);
}
