package project.comment;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CommentDao {
  @Insert
  public void insert(Comment... comments);

  @Update
  public void update(Comment... comments);

  @Delete
  public void delete(Comment comment);

  @Query("DELETE  FROM comment WHERE id = :id")
  public void delete(int id);

  @Query("SELECT * FROM comment")
  public List<Comment> getCommentList();

  @Query("SELECT * FROM comment WHERE productId = :productId")
  public List<Comment> getCommentList(int productId);
}
