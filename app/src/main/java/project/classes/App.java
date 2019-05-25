package project.classes;

import android.app.Application;
import android.arch.persistence.room.Room;

import project.database.AppDatabase;

public class App extends Application {

  public static AppDatabase database;

  @Override
  public void onCreate() {
    super.onCreate();

    database = Room.databaseBuilder(this,
      AppDatabase.class,
      "catalog.sqlite")
      .allowMainThreadQueries()
      .fallbackToDestructiveMigration()
      .build();

  }
}
