package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/** Pantry List screen: shows every ingredient stored in the database as a grid of tiles. */
public class MainActivity extends AppCompatActivity
        implements PantryAdapter.OnItemClickListener {

    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private TextView tvSubtitle;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        EdgeInsets.whiteStatusIcons(this);

        recyclerView = findViewById(R.id.recyclerPantry);
        EdgeInsets.apply(findViewById(R.id.main), findViewById(R.id.header),
                findViewById(R.id.bottomArea), recyclerView);

        db = AppDatabase.getInstance(this);
        RecipeSeeder.seedIfEmpty(db); // loads the recipes on first run only
        emptyState = findViewById(R.id.tvEmpty);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PantryAdapter(this);
        recyclerView.setAdapter(adapter);

        View fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditActivity.class)));

        BottomNavHelper.setup(this, R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.pantryItemDao().getAllItems();
        adapter.setItems(items);
        emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        tvSubtitle.setText(items.size() + (items.size() == 1 ? " ingredient" : " ingredients")
                + " \u00B7 tap to edit, hold to delete");
        showExpiryAlert(items);
    }

    /** Uses the Settings toggle: warns about items expiring within 3 days. */
    private void showExpiryAlert(List<PantryItem> items) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (!prefs.getBoolean("expiry_alerts", true)) return;
        int count = 0;
        for (PantryItem item : items) {
            int days = Fmt.daysUntil(item.getExpiryDate());
            if (days != Fmt.NO_EXPIRY && days <= 3) count++;
        }
        if (count > 0) {
            Toast.makeText(this, count + " item(s) expiring within 3 days",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.pantryItemDao().delete(item);
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    loadItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
