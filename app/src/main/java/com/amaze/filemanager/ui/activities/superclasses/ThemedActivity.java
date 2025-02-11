package com.amaze.filemanager.ui.activities.superclasses;

import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_COLORED_NAVIGATION;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.amaze.filemanager.R;
import com.amaze.filemanager.ui.colors.ColorPreferenceHelper;
import com.amaze.filemanager.ui.colors.UserColorPreferences;
import com.amaze.filemanager.ui.dialogs.ColorPickerDialog;
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants;
import com.amaze.filemanager.ui.theme.AppTheme;
import com.amaze.filemanager.ui.theme.AppThemePreference;
import com.amaze.filemanager.utils.PreferenceUtils;
import com.amaze.filemanager.utils.Utils;

public class ThemedActivity extends PreferenceActivity {
    /**
     * BroadcastReceiver responsible for updating the theme if battery saver mode is turned on or off
     */
    private final BroadcastReceiver powerModeReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent i) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    boolean followBatterySaver =
                            preferences.getBoolean(PreferencesConstants.FRAGMENT_FOLLOW_BATTERY_SAVER, false);

                    AppThemePreference theme =
                            AppThemePreference.getTheme(
                                    Integer.parseInt(
                                            preferences.getString(PreferencesConstants.FRAGMENT_THEME, "4")));

                    if (followBatterySaver && theme.getCanBeLight()) {
                        recreate();
                    }
                }
            };
    private int uiModeNight = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerPowerModeReceiver();

        // setting window background color instead of each item, in order to reduce pixel overdraw
        if (getAppTheme().equals(AppTheme.LIGHT)) {
            getWindow().setBackgroundDrawableResource(android.R.color.white);
        } else if (getAppTheme().equals(AppTheme.BLACK)) {
            getWindow().setBackgroundDrawableResource(android.R.color.black);
        } else {
            getWindow().setBackgroundDrawableResource(R.color.holo_dark_background);
        }

        // checking if theme should be set light/dark or automatic
        int colorPickerPref =
                getPrefs().getInt(PreferencesConstants.PREFERENCE_COLOR_CONFIG, ColorPickerDialog.NO_DATA);
        if (colorPickerPref == ColorPickerDialog.RANDOM_INDEX) {
            getColorPreference().saveColorPreferences(getPrefs(), ColorPreferenceHelper.randomize(this));
        }


        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getString(R.string.appbar_name), ((BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.ic_launcher)).getBitmap(), getPrimary());
        setTaskDescription(taskDescription);
        setTheme();
    }

    /**
     * Set status bar and navigation bar colors based on sdk
     *
     * @param parentView parent view required to set margin on kitkat top
     */
    public void initStatusBarResources(View parentView) {

        if (getToolbar() != null) {
            getToolbar().setBackgroundColor(getPrimary());
        }

        Window window = getWindow();

        if (findViewById(R.id.tab_frame) != null || findViewById(R.id.drawer_layout) == null) {
            window.setStatusBarColor(PreferenceUtils.getStatusColor(getPrimary()));
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (getBoolean(PREFERENCE_COLORED_NAVIGATION)) {
            window.setNavigationBarColor(PreferenceUtils.getStatusColor(getPrimary()));
        } else {
            if (getAppTheme().equals(AppTheme.LIGHT)) {
                window.setNavigationBarColor(Utils.getColor(this, android.R.color.white));
            } else if (getAppTheme().equals(AppTheme.BLACK)) {
                window.setNavigationBarColor(Utils.getColor(this, android.R.color.black));
            } else {
                window.setNavigationBarColor(Utils.getColor(this, R.color.holo_dark_background));
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        final int newUiModeNight = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // System theme change
        if (uiModeNight != newUiModeNight) {
            uiModeNight = newUiModeNight;

            if (getPrefs().getString(PreferencesConstants.FRAGMENT_THEME, "4").equals("4")) {
                getUtilsProvider().getThemeManager().setAppThemePreference(AppThemePreference.getTheme(4));
                // Recreate activity, handling saved state
                //
                // Not smooth, but will only be called if the user changes the system theme, not
                // the app theme.
                recreate();
            }
        }
    }

    public UserColorPreferences getCurrentColorPreference() {
        return getColorPreference().getCurrentUserColorPreferences(this, getPrefs());
    }

    public @ColorInt int getAccent() {
        return getColorPreference().getCurrentUserColorPreferences(this, getPrefs()).getAccent();
    }

    public @ColorInt int getPrimary() {
        return ColorPreferenceHelper.getPrimary(getCurrentColorPreference(), getCurrentTab());
    }

    @Nullable
    private Toolbar getToolbar() {
        return findViewById(R.id.toolbar);
    }

    void setTheme() {
        AppTheme theme = getAppTheme();
        String stringRepresentation = String.format("#%06X", (0xFFFFFF & getAccent()));

        switch (stringRepresentation.toUpperCase()) {
            case "#F44336":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_red);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK)) setTheme(com.amaze.filemanager.R.style.pref_accent_black_red);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_red);
                break;

            case "#E91E63":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_pink);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK)) setTheme(com.amaze.filemanager.R.style.pref_accent_black_pink);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_pink);
                break;

            case "#9C27B0":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_purple);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_purple);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_purple);
                break;

            case "#673AB7":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_light_deep_purple);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_deep_purple);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_deep_purple);
                break;

            case "#3F51B5":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_indigo);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_indigo);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_indigo);
                break;

            case "#2196F3":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_blue);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK)) setTheme(com.amaze.filemanager.R.style.pref_accent_black_blue);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_blue);
                break;

            case "#03A9F4":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_light_light_blue);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_light_blue);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_light_blue);
                break;

            case "#00BCD4":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_cyan);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK)) setTheme(com.amaze.filemanager.R.style.pref_accent_black_cyan);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_cyan);
                break;

            case "#009688":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_teal);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK)) setTheme(com.amaze.filemanager.R.style.pref_accent_black_teal);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_teal);
                break;

            case "#4CAF50":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_green);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_green);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_green);
                break;

            case "#8BC34A":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_light_light_green);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_light_green);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_light_green);
                break;

            case "#FFC107":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_amber);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_amber);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_amber);
                break;

            case "#FF9800":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_orange);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_orange);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_orange);
                break;

            case "#FF5722":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_light_deep_orange);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_deep_orange);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_deep_orange);
                break;

            case "#795548":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_brown);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_brown);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_brown);
                break;

            case "#212121":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_black);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_black);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_black);
                break;

            case "#607D8B":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_blue_grey);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_blue_grey);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_blue_grey);
                break;

            case "#004D40":
                if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.LIGHT)) setTheme(com.amaze.filemanager.R.style.pref_accent_light_super_su);
                else if (theme.equals(com.amaze.filemanager.ui.theme.AppTheme.BLACK))
                    setTheme(com.amaze.filemanager.R.style.pref_accent_black_super_su);
                else setTheme(com.amaze.filemanager.R.style.pref_accent_dark_super_su);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        uiModeNight = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        setTheme();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterPowerModeReceiver();
    }

    /**
     * Registers the BroadcastReceiver \`powerModeReceiver\` to listen to broadcasts that the battery
     * save mode has been changed
     */
    private void registerPowerModeReceiver() {
        registerReceiver(powerModeReceiver, new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED));
    }

    /**
     * Unregisters the BroadcastReceiver \`powerModeReceiver\`
     */
    private void unregisterPowerModeReceiver() {
        unregisterReceiver(powerModeReceiver);
    }
}
