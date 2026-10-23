package com.example.smartpantrymanager;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Edge-to-edge helper: lets the orange header draw under the status bar and keeps the
 * bottom content clear of the navigation bar and the keyboard.
 */
final class EdgeInsets {
    private EdgeInsets() {}

    /**
     * @param root         the screen's root view (R.id.main)
     * @param top          the header: gets extra top padding for the status bar
     * @param bottom       the view at the bottom: gets extra bottom padding for the nav bar / keyboard
     * @param extraBottom  other scrolling views that also need room at the bottom
     */
    static void apply(View root, View top, View bottom, View... extraBottom) {
        final int topPad = top.getPaddingTop();
        final int bottomPad = bottom.getPaddingBottom();
        final int[] extraPad = new int[extraBottom.length];
        for (int i = 0; i < extraBottom.length; i++) extraPad[i] = extraBottom[i].getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(bars.bottom, ime.bottom);

            v.setPadding(bars.left, 0, bars.right, 0);
            top.setPadding(top.getPaddingLeft(), topPad + bars.top, top.getPaddingRight(), top.getPaddingBottom());
            bottom.setPadding(bottom.getPaddingLeft(), bottom.getPaddingTop(), bottom.getPaddingRight(),
                    bottomPad + bottomInset);
            for (int i = 0; i < extraBottom.length; i++) {
                View view = extraBottom[i];
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
                        extraPad[i] + bottomInset);
            }
            return insets;
        });
    }

    /** White status-bar icons, because they sit on the orange header. */
    static void whiteStatusIcons(Activity activity) {
        WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
    }
}
