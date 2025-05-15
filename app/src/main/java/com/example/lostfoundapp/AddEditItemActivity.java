package com.example.lostfoundapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditItemActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.lostfoundapp.EXTRA_ID";
    public static final String EXTRA_NAME = "com.example.lostfoundapp.EXTRA_NAME";
    public static final String EXTRA_DESCRIPTION = "com.example.lostfoundapp.EXTRA_DESCRIPTION";
    public static final String EXTRA_DATE = "com.example.lostfoundapp.EXTRA_DATE";
    public static final String EXTRA_LOCATION = "com.example.lostfoundapp.EXTRA_LOCATION";
    public static final String EXTRA_TYPE = "com.example.lostfoundapp.EXTRA_TYPE";
    public static final String EXTRA_PHONE = "com.example.lostfoundapp.EXTRA_PHONE";
    public static final String EXTRA_EMAIL = "com.example.lostfoundapp.EXTRA_EMAIL";
    public static final String EXTRA_ORIGINAL_TYPE = "com.example.lostfoundapp.EXTRA_ORIGINAL_TYPE";

    private TextView textViewTitle;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonLost;
    private RadioButton radioButtonFound;
    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private TextInputEditText editTextLocation;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextEmail;
    private Button buttonSave;
    private Calendar calendar;
    private String originalType;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        // Initialize views
        textViewTitle = findViewById(R.id.text_view_title);
        radioGroupType = findViewById(R.id.radio_group_type);
        radioButtonLost = findViewById(R.id.radio_button_lost);
        radioButtonFound = findViewById(R.id.radio_button_found);
        editTextName = findViewById(R.id.edit_text_name);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextLocation = findViewById(R.id.edit_text_location);
        editTextDate = findViewById(R.id.edit_text_date);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextEmail = findViewById(R.id.edit_text_email);
        buttonSave = findViewById(R.id.button_save);

        // Initialize calendar for date picker
        calendar = Calendar.getInstance();
        setupDatePicker();

        // Check if we're editing an existing item
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            // Edit mode
            isEditMode = true;
            textViewTitle.setText("Edit Item");
            editTextName.setText(intent.getStringExtra(EXTRA_NAME));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            editTextLocation.setText(intent.getStringExtra(EXTRA_LOCATION));
            editTextDate.setText(intent.getStringExtra(EXTRA_DATE));
            editTextPhone.setText(intent.getStringExtra(EXTRA_PHONE));
            editTextEmail.setText(intent.getStringExtra(EXTRA_EMAIL));

            // Set type
            String type = intent.getStringExtra(EXTRA_TYPE);
            originalType = type;
            if ("lost".equals(type)) {
                radioButtonLost.setChecked(true);
            } else {
                radioButtonFound.setChecked(true);
            }
            
            // Update window title
            setTitle("Edit Item");
        } else {
            // Add mode
            isEditMode = false;
            textViewTitle.setText("Add New Item");
            
            // Default to current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());
            editTextDate.setText(currentDate);
            
            // Set default type to lost
            radioButtonLost.setChecked(true);
            originalType = "lost";
            
            // Update window title
            setTitle("Add Item");
        }

        // Save button click
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddEditItemActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveItem() {
        // Get values from form
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        
        // Validate required fields
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        
        if (location.isEmpty()) {
            editTextLocation.setError("Location is required");
            editTextLocation.requestFocus();
            return;
        }
        
        if (date.isEmpty()) {
            editTextDate.setError("Date is required");
            editTextDate.requestFocus();
            return;
        }

        // Get type
        String type = radioButtonLost.isChecked() ? "lost" : "found";
        
        // Check if type has changed
        boolean typeChanged = isEditMode && !type.equals(originalType);

        // Create result intent
        Intent data = new Intent();
        data.putExtra(EXTRA_NAME, name);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_LOCATION, location);
        data.putExtra(EXTRA_DATE, date);
        data.putExtra(EXTRA_TYPE, type);
        data.putExtra(EXTRA_PHONE, phone);
        data.putExtra(EXTRA_EMAIL, email);
        data.putExtra(EXTRA_ORIGINAL_TYPE, originalType);

        // If we're editing, set the ID
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        // Set success result
        setResult(RESULT_OK, data);
        
        // Show success message
        if (isEditMode) {
            if (typeChanged) {
                String message = type.equals("found") 
                    ? "Item changed to FOUND and saved to database" 
                    : "Item changed to LOST and saved to database";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item updated in database", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "New item saved to database", Toast.LENGTH_SHORT).show();
        }
        
        // Close activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_item) {
            saveItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 