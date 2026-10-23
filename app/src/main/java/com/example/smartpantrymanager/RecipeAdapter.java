package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapter that shows the suggested recipes as cards in a RecyclerView. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();
    private Map<Integer, Integer> ingredientCounts = new HashMap<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    /** @param counts recipe id -> number of ingredients, shown on each card */
    public void setRecipes(List<Recipe> newRecipes, Map<Integer, Integer> counts) {
        this.recipes = newRecipes;
        this.ingredientCounts = counts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Integer count = ingredientCounts.get(recipe.getId());
        int steps = Fmt.steps(recipe.getSteps()).size();

        holder.tvEmoji.setText(Fmt.recipeEmoji(recipe.getName()));
        holder.tvName.setText(recipe.getName());
        holder.tvMeta.setText((count == null ? 0 : count) + " ingredients \u00B7 " + steps + " steps");
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvEmoji, tvName, tvMeta;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvRecipeEmoji);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvMeta = itemView.findViewById(R.id.tvRecipeMeta);
        }
    }
}
