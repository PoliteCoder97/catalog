package project.person;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PersonDao {

  @Insert
  public void insert(Person... peoples);

  @Update
  public void update(Person... peoples);

  @Delete
  public void delete(Person people);

  @Query("DELETE  FROM person WHERE id = :id")
  public void delete(int id);

  @Query("DELETE  FROM person ")
  public void delete();


  @Query("SELECT * FROM person ")
  public List<Person> getAllPeople();

  @Query("SELECT * FROM person WHERE id = :personId")
  public List<Person> getAllPeople(int personId);

}
