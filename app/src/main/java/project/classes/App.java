package project.classes;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.Build;
import android.os.Environment;

import project.database.AppDatabase;

public class App extends Application {

  public static AppDatabase database;

  @Override
  public void onCreate() {
    super.onCreate();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
      database = Room.databaseBuilder(this, AppDatabase.class,
        "catalog.sqlite")
//      Environment.getExternalStorageDirectory().getAbsolutePath()+"/Catalog"+"/catalog.sqlite")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build();
    } else {
      database = Room.databaseBuilder(this, AppDatabase.class,
//        "catalog.sqlite")
        Environment.getExternalStorageDirectory().getAbsolutePath() + "/Catalog" + "/catalog.sqlite")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build();
    }

  }
}
