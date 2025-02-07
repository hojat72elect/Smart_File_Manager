package com.amaze.filemanager.ui.theme;

import android.content.Context;
import android.content.SharedPreferences;

import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants;

/**
 * Saves and restores the AppTheme
 */
public class AppThemeManager {
    private final Context context;
    private AppThemePreference appThemePreference;

    public AppThemeManager(SharedPreferences preferences, Context context) {
        this.context = context;
        String themeId = preferences.getString(PreferencesConstants.FRAGMENT_THEME, "4");
        appThemePreference = AppThemePreference.getTheme(Integer.parseInt(themeId));
    }

    /**
     * @return The current Application theme
     */
    public AppTheme getAppTheme() {
        return appThemePreference.getSimpleTheme(context);
    }

    /**
     * Change the current theme of the application. The change is saved.
     *
     * @param appThemePreference The new theme
     * @return The theme manager.
     */
    public AppThemeManager setAppThemePreference(AppThemePreference appThemePreference) {
        this.appThemePreference = appThemePreference;
        return this;
    }
}
