package com.websarva.wings.android.roomtest;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface CocktailDao {
    @Query("SELECT * FROM cocktailmemos WHERE id = :id")
    Cocktail findById(int id);

    @Query("INSERT INTO cocktailmemos (id, name, note) VALUES (:id, :name, :note)")
    void insert(int id, String name, String note);

    @Query("DELETE FROM cocktailmemos WHERE id = :id")
    void delete(int id);
}
