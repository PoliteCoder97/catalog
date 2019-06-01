package project.classes;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;


import project.database.AppDatabase;

public class App extends Application {

  public static AppDatabase database;
  private static Handler handler;
  public static SharedPreferences preferences;

  @Override
  public void onCreate() {
    super.onCreate();

    database = Room.databaseBuilder(this,
      AppDatabase.class,
      "catalog.sqlite")
      .allowMainThreadQueries()
      .fallbackToDestructiveMigration()
      .build();

    handler = new Handler();
    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  }

  public static Handler getHandler(){
    return handler;
  }
}
