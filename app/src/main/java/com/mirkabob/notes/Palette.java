package com.mirkabob.notes;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.google.android.material.color.MaterialColors;

final class Palette {
    static final int COUNT = 7;
    private static final int[] RES = {0, R.color.note_1, R.color.note_2, R.color.note_3,
            R.color.note_4, R.color.note_5, R.color.note_6};

    private Palette() {}

    /** Цвет заметки: 0 = нейтральный цвет темы, остальные пастельные. */
    static int bg(Context c, int idx) {
        if (idx <= 0 || idx >= COUNT) {
            return attr(c, com.google.android.material.R.attr.colorSurfaceContainerHigh);
        }
        return ContextCompat.getColor(c, RES[idx]);
    }

    static int attr(Context c, int attr) {
        return MaterialColors.getColor(c, attr, Color.GRAY);
    }
}
