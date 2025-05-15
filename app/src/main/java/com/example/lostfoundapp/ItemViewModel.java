package com.example.lostfoundapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private ItemRepository repository;
    private LiveData<List<Item>> allItems;
    private LiveData<List<Item>> lostItems;
    private LiveData<List<Item>> foundItems;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new ItemRepository(application);
        allItems = repository.getAllItems();
        lostItems = repository.getLostItems();
        foundItems = repository.getFoundItems();
    }

    public void insert(Item item) {
        repository.insert(item);
    }

    public void update(Item item) {
        repository.update(item);
    }

    public void delete(Item item) {
        repository.delete(item);
    }

    public void deleteAllItems() {
        repository.deleteAllItems();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public LiveData<List<Item>> getLostItems() {
        return lostItems;
    }

    public LiveData<List<Item>> getFoundItems() {
        return foundItems;
    }
} 