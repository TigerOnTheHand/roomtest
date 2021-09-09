package com.websarva.wings.android.roomtest;

import android.content.Context;
import androidx.room.Room;

public class AppDatabaseSingleton {
    private static AppDatabase instance = null;

    public static AppDatabase getInstance(Context context) {
        if (instance != null) {
            return instance;
        }

        instance = Room.databaseBuilder(context,
                AppDatabase.class, "cocktailmemos").build();
        return instance;
    }
}