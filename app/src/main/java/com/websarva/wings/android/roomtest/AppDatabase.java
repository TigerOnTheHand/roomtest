package com.websarva.wings.android.roomtest;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Cocktail.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CocktailDao cocktailDao();
}
