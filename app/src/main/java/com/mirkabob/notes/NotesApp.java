package com.mirkabob.notes;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class NotesApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
