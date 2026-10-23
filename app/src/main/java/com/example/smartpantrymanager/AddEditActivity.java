package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Add/Edit Ingredient screen. If an item id is passed in the Intent,
 * the form is pre-filled and saving UPDATES that item; otherwise it CREATES one.
 */
public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";
    private static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "cup", "tbsp", "tsp"};

    private EditText etName, etQuantity, etExpiry;
    private TextInputLayout tilName, tilQty, tilExpiry;
    private ChipGroup chipUnits;
    private AppDatabase db;
    private PantryItem existingItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit);
        EdgeInsets.whiteStatusIcons(this);
        EdgeInsets.apply(findViewById(R.id.main), findViewById(R.id.header),
                findViewById(R.id.scroll));

        db = AppDatabase.getInstance(this);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        tilName = findViewById(R.id.tilName);
        tilQty = findViewById(R.id.tilQty);
        tilExpiry = findViewById(R.id.tilExpiry);
        chipUnits = findViewById(R.id.chipUnits);
        Button btnSave = findViewById(R.id.btnSave);
        TextView tvFormTitle = findViewById(R.id.tvFormTitle);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        buildUnitChips();

        // Edit mode: an item id was sent through the Intent
        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            existingItem = db.pantryItemDao().getItemById(itemId);
            if (existingItem != null) {
                tvFormTitle.setText("Edit Ingredient");
                btnSave.setText("Save changes");
                etName.setText(existingItem.getName());
                etQuantity.setText(Fmt.qty(existingItem.getQuantity()));
                etExpiry.setText(existingItem.getExpiryDate());
                selectUnit(existingItem.getUnit());
            }
        }

        // Expiry: tap to pick a date, tap the X (or long-press) to clear it
        etExpiry.setOnClickListener(v -> showDatePicker());
        etExpiry.setOnLongClickListener(v -> { etExpiry.setText(""); return true; });
        tilExpiry.setEndIconOnClickListener(v -> etExpiry.setText(""));
        etExpiry.addTextChangedListener(new SimpleWatcher(() ->
                tilExpiry.setEndIconVisible(!etExpiry.getText().toString().isEmpty())));
        tilExpiry.setEndIconVisible(!etExpiry.getText().toString().isEmpty());

        // Clear validation errors as soon as the user starts typing again
        etName.addTextChangedListener(new SimpleWatcher(() -> tilName.setError(null)));
        etQuantity.addTextChangedListener(new SimpleWatcher(() -> tilQty.setError(null)));

        btnSave.setOnClickListener(v -> saveItem());
    }

    /** One selectable chip per unit; "pcs" is selected by default. */
    private void buildUnitChips() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < UNITS.length; i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_unit, chipUnits, false);
            chip.setId(android.view.View.generateViewId());
            chip.setText(UNITS[i]);
            chip.setTag(UNITS[i]);
            chipUnits.addView(chip);
            if (i == 0) chipUnits.check(chip.getId());
        }
    }

    private void selectUnit(String unit) {
        for (int i = 0; i < chipUnits.getChildCount(); i++) {
            Chip chip = (Chip) chipUnits.getChildAt(i);
            if (chip.getTag().equals(unit)) chipUnits.check(chip.getId());
        }
    }

    private String selectedUnit() {
        Chip chip = chipUnits.findViewById(chipUnits.getCheckedChipId());
        return chip == null ? UNITS[0] : chip.getTag().toString();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        String unit = selectedUnit();
        String expiry = etExpiry.getText().toString().trim();

        // Input validation
        if (name.isEmpty()) {
            tilName.setError("Please enter an ingredient name");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            tilQty.setError("Enter a valid number");
            return;
        }
        if (quantity <= 0) {
            tilQty.setError("Quantity must be greater than 0");
            return;
        }

        if (existingItem == null) {
            db.pantryItemDao().insert(new PantryItem(name, quantity, unit, expiry)); // Create
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            existingItem.setName(name);
            existingItem.setQuantity(quantity);
            existingItem.setUnit(unit);
            existingItem.setExpiryDate(expiry);
            db.pantryItemDao().update(existingItem); // Update
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // back to the Pantry List
    }

    /** TextWatcher that only cares that the text changed. */
    private static class SimpleWatcher implements TextWatcher {
        private final Runnable onChange;
        SimpleWatcher(Runnable onChange) { this.onChange = onChange; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { onChange.run(); }
        @Override public void afterTextChanged(Editable s) {}
    }
}
