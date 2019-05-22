package project.person;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PersonContactDao {

  @Query("SELECT person.* , contact.* FROM person LEFT OUTER JOIN contact ON person.id = contact.personId AND person.id = :personId")
  public List<PersonContact> getPersonContacts(int personId);

}
