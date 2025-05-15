package com.example.lostfoundapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ItemDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM = "com.example.lostfoundapp.EXTRA_ITEM";
    public static final int RESULT_DELETE = 2;
    public static final int EDIT_ITEM_REQUEST = 1;
    public static final int RESULT_SAVE = 3;
    private static final String TAG = "ItemDetailActivity";

    private Item currentItem;
    private Item originalItem;
    private boolean isEdited = false;
    
    private TextView textViewDetailTitle;
    private TextView textViewDetailType;
    private TextView textViewDetailName;
    private TextView textViewDetailDescription;
    private TextView textViewDetailLocation;
    private TextView textViewDetailDate;
    private TextView textViewDetailPhone;
    private TextView textViewDetailEmail;
    private TextView textViewPhoneLabel;
    private TextView textViewEmailLabel;
    private Button buttonEdit;
    private Button buttonSave;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialize views
        textViewDetailTitle = findViewById(R.id.text_view_detail_title);
        textViewDetailType = findViewById(R.id.text_view_detail_type);
        textViewDetailName = findViewById(R.id.text_view_detail_name);
        textViewDetailDescription = findViewById(R.id.text_view_detail_description);
        textViewDetailLocation = findViewById(R.id.text_view_detail_location);
        textViewDetailDate = findViewById(R.id.text_view_detail_date);
        textViewDetailPhone = findViewById(R.id.text_view_detail_phone);
        textViewDetailEmail = findViewById(R.id.text_view_detail_email);
        textViewPhoneLabel = findViewById(R.id.text_view_phone_label);
        textViewEmailLabel = findViewById(R.id.text_view_email_label);
        buttonEdit = findViewById(R.id.button_edit);
        buttonSave = findViewById(R.id.button_save);
        buttonDelete = findViewById(R.id.button_delete);

        try {
            // Get the item from intent
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(EXTRA_ITEM)) {
                // Cast as Serializable first, then to Item
                currentItem = (Item) intent.getSerializableExtra(EXTRA_ITEM);
                if (currentItem != null) {
                    // Keep a copy of the original item for comparison
                    originalItem = new Item(
                            currentItem.getName(),
                            currentItem.getDescription(),
                            currentItem.getDate(),
                            currentItem.getLocation(),
                            currentItem.getType(),
                            currentItem.getPhone(),
                            currentItem.getEmail()
                    );
                    originalItem.setId(currentItem.getId());
                    
                    displayItemDetails();
                    updateSaveButtonState();
                } else {
                    throw new Exception("Item is null");
                }
            } else {
                throw new Exception("No item data found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading item: " + e.getMessage());
            Toast.makeText(this, "Error loading item: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Edit button click
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem();
            }
        });
        
        // Save button click
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        // Delete button click
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_detail_menu, menu);
        
        // Set enabled state for save button
        MenuItem saveItem = menu.findItem(R.id.save_item_detail);
        if (saveItem != null) {
            saveItem.setEnabled(isEdited);
            saveItem.getIcon().setAlpha(isEdited ? 255 : 130);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.save_item_detail) {
            saveItem();
            return true;
        } else if (id == R.id.edit_item_detail) {
            editItem();
            return true;
        } else if (id == R.id.delete_item_detail) {
            showDeleteConfirmationDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void updateSaveButtonState() {
        // Initially, the save button is disabled until changes are made
        buttonSave.setEnabled(isEdited);
        buttonSave.setAlpha(isEdited ? 1.0f : 0.5f);
        
        // Update menu item if available
        invalidateOptionsMenu();
    }

    private void displayItemDetails() {
        try {
            // Set the title based on item type
            String type = currentItem.getType();
            if (type != null && !type.isEmpty()) {
                textViewDetailType.setText(type.substring(0, 1).toUpperCase() + type.substring(1));
            } else {
                textViewDetailType.setText("Unknown");
            }
            
            // Set other details
            textViewDetailName.setText(Objects.requireNonNull(currentItem.getName(), ""));
            textViewDetailDescription.setText(Objects.toString(currentItem.getDescription(), ""));
            textViewDetailLocation.setText(Objects.toString(currentItem.getLocation(), ""));
            textViewDetailDate.setText(Objects.toString(currentItem.getDate(), ""));
            
            String phone = currentItem.getPhone();
            if (phone != null && !phone.isEmpty()) {
                textViewDetailPhone.setText(phone);
                textViewDetailPhone.setVisibility(View.VISIBLE);
                textViewPhoneLabel.setVisibility(View.VISIBLE);
            } else {
                textViewDetailPhone.setVisibility(View.GONE);
                textViewPhoneLabel.setVisibility(View.GONE);
            }
            
            String email = currentItem.getEmail();
            if (email != null && !email.isEmpty()) {
                textViewDetailEmail.setText(email);
                textViewDetailEmail.setVisibility(View.VISIBLE);
                textViewEmailLabel.setVisibility(View.VISIBLE);
            } else {
                textViewDetailEmail.setVisibility(View.GONE);
                textViewEmailLabel.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying item details: " + e.getMessage());
            Toast.makeText(this, "Error displaying item details", Toast.LENGTH_SHORT).show();
        }
    }

    private void editItem() {
        try {
            Intent intent = new Intent(this, AddEditItemActivity.class);
            intent.putExtra(AddEditItemActivity.EXTRA_ID, currentItem.getId());
            intent.putExtra(AddEditItemActivity.EXTRA_NAME, currentItem.getName());
            intent.putExtra(AddEditItemActivity.EXTRA_DESCRIPTION, currentItem.getDescription());
            intent.putExtra(AddEditItemActivity.EXTRA_DATE, currentItem.getDate());
            intent.putExtra(AddEditItemActivity.EXTRA_LOCATION, currentItem.getLocation());
            intent.putExtra(AddEditItemActivity.EXTRA_TYPE, currentItem.getType());
            intent.putExtra(AddEditItemActivity.EXTRA_PHONE, currentItem.getPhone());
            intent.putExtra(AddEditItemActivity.EXTRA_EMAIL, currentItem.getEmail());
            intent.putExtra(AddEditItemActivity.EXTRA_ORIGINAL_TYPE, currentItem.getType());
            
            startActivityForResult(intent, EDIT_ITEM_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "Error opening edit screen: " + e.getMessage());
            Toast.makeText(this, "Error opening edit screen", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveItem() {
        try {
            if (!isEdited) {
                Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get data to send back to MainActivity
            Intent data = new Intent();
            data.putExtra(AddEditItemActivity.EXTRA_ID, currentItem.getId());
            data.putExtra(AddEditItemActivity.EXTRA_NAME, currentItem.getName());
            data.putExtra(AddEditItemActivity.EXTRA_DESCRIPTION, currentItem.getDescription());
            data.putExtra(AddEditItemActivity.EXTRA_DATE, currentItem.getDate());
            data.putExtra(AddEditItemActivity.EXTRA_LOCATION, currentItem.getLocation());
            data.putExtra(AddEditItemActivity.EXTRA_TYPE, currentItem.getType());
            data.putExtra(AddEditItemActivity.EXTRA_PHONE, currentItem.getPhone());
            data.putExtra(AddEditItemActivity.EXTRA_EMAIL, currentItem.getEmail());
            data.putExtra(AddEditItemActivity.EXTRA_ORIGINAL_TYPE, originalItem.getType());
            
            // Check if type has changed
            boolean typeChanged = !originalItem.getType().equals(currentItem.getType());
            
            // Send result back
            setResult(RESULT_OK, data);
            
            // Show success message
            if (typeChanged) {
                String message = currentItem.getType().equals("found") 
                    ? "Item status changed to FOUND and saved to database!" 
                    : "Item status changed to LOST and saved to database!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Changes saved to database", Toast.LENGTH_SHORT).show();
            }
            
            // Reset edit state
            isEdited = false;
            updateSaveButtonState();
            
            // Update original item after saving
            originalItem = new Item(
                    currentItem.getName(),
                    currentItem.getDescription(),
                    currentItem.getDate(),
                    currentItem.getLocation(),
                    currentItem.getType(),
                    currentItem.getPhone(),
                    currentItem.getEmail()
            );
            originalItem.setId(currentItem.getId());
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving item: " + e.getMessage());
            Toast.makeText(this, "Error saving item", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteItem() {
        try {
            Intent data = new Intent();
            data.putExtra(AddEditItemActivity.EXTRA_ID, currentItem.getId());
            data.putExtra(AddEditItemActivity.EXTRA_NAME, currentItem.getName());
            data.putExtra(AddEditItemActivity.EXTRA_DESCRIPTION, currentItem.getDescription());
            data.putExtra(AddEditItemActivity.EXTRA_DATE, currentItem.getDate());
            data.putExtra(AddEditItemActivity.EXTRA_LOCATION, currentItem.getLocation());
            data.putExtra(AddEditItemActivity.EXTRA_TYPE, currentItem.getType());
            data.putExtra(AddEditItemActivity.EXTRA_PHONE, currentItem.getPhone());
            data.putExtra(AddEditItemActivity.EXTRA_EMAIL, currentItem.getEmail());
            
            setResult(RESULT_DELETE, data);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting item: " + e.getMessage());
            Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == EDIT_ITEM_REQUEST && resultCode == RESULT_OK && data != null) {
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
                
                String originalType = currentItem.getType();
                boolean typeChanged = !originalType.equals(type);

                // Update the current item
                currentItem = new Item(name, description, date, location, type, phone, email);
                currentItem.setId(id);
                displayItemDetails();
                
                // Mark as edited
                isEdited = true;
                updateSaveButtonState();

                // Automatically pass the result back to MainActivity and save to database
                saveItem();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating item: " + e.getMessage());
            Toast.makeText(this, "Error updating item: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}