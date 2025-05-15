package com.example.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ADD_ITEM_REQUEST = 1;
    private static final int EDIT_ITEM_REQUEST = 2;
    private static final int VIEW_ITEM_REQUEST = 3;

    private ItemViewModel itemViewModel;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Initialize RecyclerView
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);

            // Initialize adapter
            adapter = new ItemAdapter();
            recyclerView.setAdapter(adapter);

            // Initialize ViewModel
            itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
            
            // Observe all items initially
            itemViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
                @Override
                public void onChanged(List<Item> items) {
                    adapter.submitList(items);
                }
            });

            // Setup tabs
            tabLayout = findViewById(R.id.tab_layout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0: // All
                            itemViewModel.getAllItems().observe(MainActivity.this, new Observer<List<Item>>() {
                                @Override
                                public void onChanged(List<Item> items) {
                                    adapter.submitList(items);
                                }
                            });
                            break;
                        case 1: // Lost
                            itemViewModel.getLostItems().observe(MainActivity.this, new Observer<List<Item>>() {
                                @Override
                                public void onChanged(List<Item> items) {
                                    adapter.submitList(items);
                                }
                            });
                            break;
                        case 2: // Found
                            itemViewModel.getFoundItems().observe(MainActivity.this, new Observer<List<Item>>() {
                                @Override
                                public void onChanged(List<Item> items) {
                                    adapter.submitList(items);
                                }
                            });
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            // Handle FAB click
            FloatingActionButton fabAdd = findViewById(R.id.fab_add);
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                    startActivityForResult(intent, ADD_ITEM_REQUEST);
                }
            });

            // Handle item click
            adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Item item) {
                    try {
                        if (item != null) {
                            Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                            intent.putExtra(ItemDetailActivity.EXTRA_ITEM, item);
                            startActivityForResult(intent, VIEW_ITEM_REQUEST);
                        } else {
                            Toast.makeText(MainActivity.this, "Cannot open null item", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening item details: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Error opening item details", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Application initialization error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (data == null) {
                return;
            }

            if (requestCode == ADD_ITEM_REQUEST && resultCode == RESULT_OK) {
                String name = data.getStringExtra(AddEditItemActivity.EXTRA_NAME);
                String description = data.getStringExtra(AddEditItemActivity.EXTRA_DESCRIPTION);
                String date = data.getStringExtra(AddEditItemActivity.EXTRA_DATE);
                String location = data.getStringExtra(AddEditItemActivity.EXTRA_LOCATION);
                String type = data.getStringExtra(AddEditItemActivity.EXTRA_TYPE);
                String phone = data.getStringExtra(AddEditItemActivity.EXTRA_PHONE);
                String email = data.getStringExtra(AddEditItemActivity.EXTRA_EMAIL);

                if (name == null || name.isEmpty()) {
                    Toast.makeText(this, "Cannot save item without a name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Item item = new Item(name, description, date, location, type, phone, email);
                itemViewModel.insert(item);

                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            } else if ((requestCode == EDIT_ITEM_REQUEST || requestCode == VIEW_ITEM_REQUEST) && resultCode == RESULT_OK) {
                int id = data.getIntExtra(AddEditItemActivity.EXTRA_ID, -1);

                if (id == -1) {
                    Toast.makeText(this, "Item can't be updated", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = data.getStringExtra(AddEditItemActivity.EXTRA_NAME);
                String description = data.getStringExtra(AddEditItemActivity.EXTRA_DESCRIPTION);
                String date = data.getStringExtra(AddEditItemActivity.EXTRA_DATE);
                String location = data.getStringExtra(AddEditItemActivity.EXTRA_LOCATION);
                String type = data.getStringExtra(AddEditItemActivity.EXTRA_TYPE);
                String phone = data.getStringExtra(AddEditItemActivity.EXTRA_PHONE);
                String email = data.getStringExtra(AddEditItemActivity.EXTRA_EMAIL);

                // Check if type has changed
                String originalType = data.getStringExtra(AddEditItemActivity.EXTRA_ORIGINAL_TYPE);
                boolean typeChanged = originalType != null && !originalType.equals(type);

                if (name == null || name.isEmpty()) {
                    Toast.makeText(this, "Cannot update item without a name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Item item = new Item(name, description, date, location, type, phone, email);
                item.setId(id);
                itemViewModel.update(item);
                
                // Only show message if from the edit activity
                if (requestCode == EDIT_ITEM_REQUEST) {
                    if (typeChanged) {
                        String message = type.equals("found") 
                            ? "Item status changed to FOUND!" 
                            : "Item status changed to LOST!";
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == VIEW_ITEM_REQUEST && resultCode == ItemDetailActivity.RESULT_DELETE) {
                int id = data.getIntExtra(AddEditItemActivity.EXTRA_ID, -1);
                
                if (id == -1) {
                    Toast.makeText(this, "Item can't be deleted", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = data.getStringExtra(AddEditItemActivity.EXTRA_NAME);
                String description = data.getStringExtra(AddEditItemActivity.EXTRA_DESCRIPTION);
                String date = data.getStringExtra(AddEditItemActivity.EXTRA_DATE);
                String location = data.getStringExtra(AddEditItemActivity.EXTRA_LOCATION);
                String type = data.getStringExtra(AddEditItemActivity.EXTRA_TYPE);
                String phone = data.getStringExtra(AddEditItemActivity.EXTRA_PHONE);
                String email = data.getStringExtra(AddEditItemActivity.EXTRA_EMAIL);

                Item item = new Item(name, description, date, location, type, phone, email);
                item.setId(id);
                itemViewModel.delete(item);

                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onActivityResult: " + e.getMessage());
            Toast.makeText(this, "Error processing result", Toast.LENGTH_SHORT).show();
        }
    }
}