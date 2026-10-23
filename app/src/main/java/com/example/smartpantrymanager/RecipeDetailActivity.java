package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/** Recipe Detail screen: ingredient chips and a numbered method. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        EdgeInsets.whiteStatusIcons(this);
        EdgeInsets.apply(findViewById(R.id.main), findViewById(R.id.header),
                findViewById(R.id.scroll));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        AppDatabase db = AppDatabase.getInstance(this);
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = db.recipeDao().getRecipeById(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        List<RecipeIngredient> ingredients = db.recipeDao().getIngredientsForRecipe(recipe.getId());
        List<String> steps = Fmt.steps(recipe.getSteps());

        ((TextView) findViewById(R.id.tvRecipeEmoji)).setText(Fmt.recipeEmoji(recipe.getName()));
        ((TextView) findViewById(R.id.tvRecipeTitle)).setText(recipe.getName());
        ((TextView) findViewById(R.id.tvRecipeMeta)).setText(
                ingredients.size() + " ingredients \u00B7 " + steps.size() + " steps");

        // Ingredients as chips, e.g. "200 g Pasta"
        ChipGroup chipGroup = findViewById(R.id.chipIngredients);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (RecipeIngredient ing : ingredients) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_ingredient, chipGroup, false);
            chip.setText(Fmt.qty(ing.getQuantity()) + " " + ing.getUnit() + "  "
                    + Fmt.capitalize(ing.getName()));
            chipGroup.addView(chip);
        }

        // Method as numbered steps
        LinearLayout container = findViewById(R.id.stepsContainer);
        for (int i = 0; i < steps.size(); i++) {
            View row = inflater.inflate(R.layout.item_step, (ViewGroup) container, false);
            ((TextView) row.findViewById(R.id.tvStepNumber)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.tvStepText)).setText(steps.get(i));
            container.addView(row);
        }
    }
}
