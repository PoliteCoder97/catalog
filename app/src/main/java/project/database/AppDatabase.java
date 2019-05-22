package project.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import project.category.Category;
import project.category.CategoryDao;
import project.comment.Comment;
import project.comment.CommentDao;
import project.contact.Contact;
import project.contact.ContactDao;
import project.person.Person;
import project.person.PersonContact;
import project.person.PersonContactDao;
import project.person.PersonDao;
import project.product.Product;
import project.product.ProductDao;

@Database(entities = {Product.class, Person.class, Comment.class, Category.class, Contact.class, PersonContact.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

  public abstract ProductDao getProductDao();
  public abstract PersonDao getPersonDao();
  public abstract ContactDao getContactDao();
  public abstract CommentDao getCommentdao();
  public abstract CategoryDao getCategorydao();
  public abstract PersonContactDao getPersonContactdao();
}
