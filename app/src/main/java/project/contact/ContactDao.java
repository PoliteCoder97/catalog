package project.contact;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

  @Insert
  public void insert(Contact... contacts);

  @Update
  public void update(Contact... contacts);
  @Delete
  public void delete(Contact contact);

  @Query("DELETE  FROM contact WHERE id = :id")
  public void delete(int id);
  @Query("DELETE  FROM contact")
  public void delete();


  @Query("SELECT * FROM contact WHERE personId = :personId")
  public Contact getContact(int personId);

//  @Query("SELECT person.*,contact.* FROM person LEFT JOIN contact ON person.id = contact.personId")
//  public List<Person> getAllPeople();


}
