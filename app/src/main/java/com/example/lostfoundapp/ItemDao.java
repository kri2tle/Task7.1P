package com.example.lostfoundapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    
    @Insert
    void insert(Item item);
    
    @Update
    void update(Item item);
    
    @Delete
    void delete(Item item);
    
    @Query("DELETE FROM items")
    void deleteAllItems();
    
    @Query("SELECT * FROM items ORDER BY id DESC")
    LiveData<List<Item>> getAllItems();
    
    @Query("SELECT * FROM items WHERE type = :type ORDER BY id DESC")
    LiveData<List<Item>> getItemsByType(String type);
} 