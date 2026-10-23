package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Sets up the floating bottom navigation bar and moves between screens using Intents. */
public class BottomNavHelper {

    public static void setup(Activity activity, int selectedId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;
            Class<?> target;
            if (id == R.id.nav_pantry) target = MainActivity.class;
            else if (id == R.id.nav_suggested) target = SuggestedActivity.class;
            else target = SettingsActivity.class;
            activity.startActivity(new Intent(activity, target));
            activity.overridePendingTransition(0, 0);
            activity.finish();
            return true;
        });
    }
}
