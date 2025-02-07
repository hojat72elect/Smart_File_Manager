package com.amaze.filemanager.ui.provider;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.amaze.filemanager.ui.colors.ColorPreferenceHelper;
import com.amaze.filemanager.ui.theme.AppTheme;
import com.amaze.filemanager.ui.theme.AppThemeManager;

public class UtilitiesProvider {
    private final ColorPreferenceHelper colorPreference;
    private final AppThemeManager appThemeManager;

    public UtilitiesProvider(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        colorPreference = new ColorPreferenceHelper();
        colorPreference.getCurrentUserColorPreferences(context, sharedPreferences);
        appThemeManager = new AppThemeManager(sharedPreferences, context);
    }

    public ColorPreferenceHelper getColorPreference() {
        return colorPreference;
    }

    public AppTheme getAppTheme() {
        return appThemeManager.getAppTheme();
    }

    public AppThemeManager getThemeManager() {
        return appThemeManager;
    }
}
