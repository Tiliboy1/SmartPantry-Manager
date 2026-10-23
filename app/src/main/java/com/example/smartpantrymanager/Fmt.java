package com.example.smartpantrymanager;

import android.os.Build;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Small helpers shared by the screens: quantity text, expiry dates, emoji badges, recipe steps. */
final class Fmt {
    /** Returned by {@link #daysUntil(String)} when an item has no (valid) expiry date. */
    static final int NO_EXPIRY = Integer.MAX_VALUE;

    private Fmt() {}

    /** 2.0 -> "2", 0.50 -> "0.5", 1.25 -> "1.25". */
    static String qty(double value) {
        return new DecimalFormat("0.##").format(value);
    }

    /** "tomato" -> "Tomato". */
    static String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase(Locale.US) + text.substring(1);
    }

    /** Whole days from today until a yyyy-MM-dd date (negative = already expired). */
    static int daysUntil(String date) {
        if (date == null || date.trim().isEmpty()) return NO_EXPIRY;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            parser.setLenient(false);
            Date expiry = parser.parse(date.trim());
            if (expiry == null) return NO_EXPIRY;
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            return (int) Math.round((expiry.getTime() - today.getTimeInMillis()) / 86400000.0);
        } catch (ParseException e) {
            return NO_EXPIRY;
        }
    }

    /** Emoji for an ingredient. Falls back to its first letter if no emoji fits. */
    static String badgeFor(String name) {
        String n = name == null ? "" : name.toLowerCase(Locale.US);
        // {keyword, emoji, minimum Android API that can draw the emoji}
        Object[][] map = {
                {"tomato", "\uD83C\uDF45", 24}, {"onion", "\uD83E\uDDC5", 29}, {"garlic", "\uD83E\uDDC4", 29}, {"egg", "\uD83E\uDD5A", 24},
                {"milk", "\uD83E\uDD5B", 24}, {"cheese", "\uD83E\uDDC0", 24}, {"rice", "\uD83C\uDF5A", 24}, {"pasta", "\uD83C\uDF5D", 24},
                {"noodle", "\uD83C\uDF5C", 24}, {"potato", "\uD83E\uDD54", 24}, {"carrot", "\uD83E\uDD55", 24}, {"chicken", "\uD83C\uDF57", 24},
                {"beef", "\uD83E\uDD69", 24}, {"bacon", "\uD83E\uDD53", 24}, {"bread", "\uD83C\uDF5E", 24}, {"butter", "\uD83E\uDDC8", 29},
                {"banana", "\uD83C\uDF4C", 24}, {"apple", "\uD83C\uDF4E", 24}, {"lemon", "\uD83C\uDF4B", 24}, {"pepper", "\uD83C\uDF36\uFE0F", 24},
                {"mushroom", "\uD83C\uDF44", 24}, {"fish", "\uD83D\uDC1F", 24}, {"tuna", "\uD83D\uDC1F", 24}, {"spinach", "\uD83E\uDD6C", 29},
                {"corn", "\uD83C\uDF3D", 24}, {"avocado", "\uD83E\uDD51", 24}, {"broccoli", "\uD83E\uDD66", 24}, {"cucumber", "\uD83E\uDD52", 24},
                {"sugar", "\uD83C\uDF6C", 24}, {"flour", "\uD83C\uDF3E", 24}, {"honey", "\uD83C\uDF6F", 24}, {"lentil", "\uD83E\uDD63", 24},
                {"salt", "\uD83E\uDDC2", 29}, {"oil", "\uD83E\uDDF4", 29}
        };
        for (Object[] row : map) {
            if (n.contains((String) row[0]) && Build.VERSION.SDK_INT >= (Integer) row[2]) {
                return (String) row[1];
            }
        }
        return n.isEmpty() ? "?" : n.substring(0, 1).toUpperCase(Locale.US);
    }

    /** Emoji for a recipe, chosen from its name. */
    static String recipeEmoji(String name) {
        String n = name == null ? "" : name.toLowerCase(Locale.US);
        if (n.contains("pancake")) return "\uD83E\uDD5E";
        if (n.contains("pasta") || n.contains("mac")) return "\uD83C\uDF5D";
        if (n.contains("omelette") || n.contains("egg")) return "\uD83C\uDF73";
        if (n.contains("soup")) return "\uD83C\uDF72";
        if (n.contains("rice")) return "\uD83C\uDF5A";
        if (n.contains("toast")) return "\uD83C\uDF5E";
        if (n.contains("potato")) return "\uD83E\uDD54";
        if (n.contains("pudding")) return "\uD83C\uDF6E";
        if (n.contains("chicken")) return "\uD83C\uDF57";
        return "\uD83C\uDF7D\uFE0F";
    }

    /** Splits "1. Beat the eggs.\n2. Melt the butter." into clean steps without the numbers. */
    static List<String> steps(String text) {
        List<String> out = new ArrayList<>();
        if (text == null) return out;
        for (String line : text.split("\\r?\\n")) {
            String step = line.trim().replaceFirst("^\\d+[.)]\\s*", "");
            if (!step.isEmpty()) out.add(step);
        }
        return out;
    }
}
