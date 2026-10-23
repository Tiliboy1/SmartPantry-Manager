package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/** Settings screen: toggle for expiring-soon alerts, saved in SharedPreferences. */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        EdgeInsets.whiteStatusIcons(this);
        EdgeInsets.apply(findViewById(R.id.main), findViewById(R.id.header),
                findViewById(R.id.bottomArea), findViewById(R.id.scroll));

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        CompoundButton switchExpiry = findViewById(R.id.switchExpiry);
        switchExpiry.setChecked(prefs.getBoolean("expiry_alerts", true));
        switchExpiry.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean("expiry_alerts", checked).apply());

        BottomNavHelper.setup(this, R.id.nav_settings);
    }
}
