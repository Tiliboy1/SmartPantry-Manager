package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Suggested Recipes screen: only recipes the user can make right now. */
public class SuggestedActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private View noMatch;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested);
        EdgeInsets.whiteStatusIcons(this);

        recyclerView = findViewById(R.id.recyclerSuggested);
        EdgeInsets.apply(findViewById(R.id.main), findViewById(R.id.header),
                findViewById(R.id.bottomArea), recyclerView);

        db = AppDatabase.getInstance(this);
        noMatch = findViewById(R.id.tvNoMatch);
        tvCount = findViewById(R.id.tvCount);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        BottomNavHelper.setup(this, R.id.nav_suggested);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions(); // re-run the matching every time the screen is shown
    }

    private void loadSuggestions() {
        List<PantryItem> pantry = db.pantryItemDao().getAllItems();
        List<Recipe> suggested = new ArrayList<>();
        Map<Integer, Integer> counts = new HashMap<>();
        for (Recipe recipe : db.recipeDao().getAllRecipes()) {
            List<RecipeIngredient> needed = db.recipeDao().getIngredientsForRecipe(recipe.getId());
            // Strict-matching rule: every single ingredient must be in the pantry
            if (RecipeMatcher.canMake(needed, pantry)) {
                suggested.add(recipe);
                counts.put(recipe.getId(), needed.size());
            }
        }
        adapter.setRecipes(suggested, counts);
        noMatch.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(suggested.isEmpty() ? View.GONE : View.VISIBLE);
        tvCount.setText(suggested.size() + (suggested.size() == 1 ? " recipe" : " recipes")
                + " ready with what you have");
    }
}
