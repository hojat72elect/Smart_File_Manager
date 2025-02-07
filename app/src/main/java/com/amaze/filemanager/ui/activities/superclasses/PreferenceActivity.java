package com.amaze.filemanager.ui.activities.superclasses;

import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_BOOKMARKS_ADDED;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_CHANGEPATHS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_COLORED_NAVIGATION;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_COLORIZE_ICONS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_DISABLE_PLAYER_INTENT_FILTERS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_ENABLE_MARQUEE_FILENAME;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_NEED_TO_SET_HOME;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_ROOTMODE;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_ROOT_LEGACY_LISTING;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_DIVIDERS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_FILE_SIZE;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_GOBACK_BUTTON;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_HEADERS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_HIDDENFILES;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_LAST_MODIFIED;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_PERMISSIONS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_SIDEBAR_FOLDERS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_THUMB;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_TEXTEDITOR_NEWSTACK;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_USE_CIRCULAR_IMAGES;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_VIEW;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants;
import com.amaze.filemanager.utils.PreferenceUtils;

public class PreferenceActivity extends BasicActivity {

    private SharedPreferences sharedPrefs;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // Fragments are created before the super call returns, so we must initialize sharedPrefs before the super call. Otherwise, it cannot be used by fragments.
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    public SharedPreferences getPrefs() {
        return sharedPrefs;
    }

    public boolean isRootExplorer() {
        return getBoolean(PREFERENCE_ROOTMODE);
    }

    public int getCurrentTab() {
        return getPrefs()
                .getInt(PreferencesConstants.PREFERENCE_CURRENT_TAB, PreferenceUtils.DEFAULT_CURRENT_TAB);
    }

    public boolean getBoolean(@NonNull String key) {
        boolean defaultValue;

        switch (key) {
            case PREFERENCE_SHOW_PERMISSIONS:
            case PREFERENCE_SHOW_GOBACK_BUTTON:
            case PREFERENCE_SHOW_HIDDENFILES:
            case PREFERENCE_BOOKMARKS_ADDED:
            case PREFERENCE_ROOTMODE:
            case PREFERENCE_COLORED_NAVIGATION:
            case PREFERENCE_TEXTEDITOR_NEWSTACK:
            case PREFERENCE_CHANGEPATHS:
            case PREFERENCE_ROOT_LEGACY_LISTING:
            case PREFERENCE_DISABLE_PLAYER_INTENT_FILTERS:
                defaultValue = false;
                break;
            case PREFERENCE_SHOW_FILE_SIZE:
            case PREFERENCE_SHOW_DIVIDERS:
            case PREFERENCE_SHOW_HEADERS:
            case PREFERENCE_USE_CIRCULAR_IMAGES:
            case PREFERENCE_COLORIZE_ICONS:
            case PREFERENCE_SHOW_THUMB:
            case PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES:
            case PREFERENCE_NEED_TO_SET_HOME:
            case PREFERENCE_SHOW_SIDEBAR_FOLDERS:
            case PREFERENCE_VIEW:
            case PREFERENCE_SHOW_LAST_MODIFIED:
            case PREFERENCE_ENABLE_MARQUEE_FILENAME:
                defaultValue = true;
                break;
            default:
                throw new IllegalArgumentException("Please map '" + key + "'");
        }

        return sharedPrefs.getBoolean(key, defaultValue);
    }
}
