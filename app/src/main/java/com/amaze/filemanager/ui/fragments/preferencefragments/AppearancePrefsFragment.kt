package com.amaze.filemanager.ui.fragments.preferencefragments

import android.os.Bundle
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_GRID_COLUMNS
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_GRID_COLUMNS_DEFAULT
import com.amaze.filemanager.ui.theme.AppThemePreference
import java.util.Objects

class AppearancePrefsFragment : BasePrefsFragment() {
    override val title = R.string.appearance

    /**
     * The actual value saved for the preference, to see the localized strings see [R.array.columns]
     */
    private val savedPreferenceValues =
        listOf(
            PREFERENCE_GRID_COLUMNS_DEFAULT,
            "2",
            "3",
            "4",
            "5",
            "6",
        )
    private var currentTheme = 0
    private var gridColumnPref: Preference? = null

    private val onClickTheme =
        Preference.OnPreferenceClickListener {
            val builder = MaterialDialog.Builder(activity)
            builder.items(R.array.theme)
                .itemsCallbackSingleChoice(currentTheme) { dialog, _, which, _ ->
                    val editor = activity.prefs.edit()
                    editor.putString(PreferencesConstants.FRAGMENT_THEME, which.toString())
                    editor.apply()

                    activity.getUtilsProvider().themeManager.setAppThemePreference(
                        AppThemePreference.getTheme(which),
                    )
                    activity.recreate()

                    dialog.dismiss()
                    true
                }
                .title(R.string.theme)
                .build()
                .show()

            true
        }

    private val onClickGridColumn =
        Preference.OnPreferenceClickListener {
            val dialog =
                MaterialDialog.Builder(activity).also { builder ->
                    builder.theme(activity.getUtilsProvider().appTheme.materialDialogTheme)
                    builder.title(R.string.gridcolumnno)
                    val columnsPreference =
                        activity
                            .prefs
                            .getString(PREFERENCE_GRID_COLUMNS, PREFERENCE_GRID_COLUMNS_DEFAULT)

                    Objects.requireNonNull(columnsPreference)
                    val current =
                        when (columnsPreference) {
                            null -> {
                                PREFERENCE_GRID_COLUMNS_DEFAULT.toInt()
                            }

                            else -> {
                                columnsPreference.toInt() - 1
                            }
                        }

                    builder
                        .items(R.array.columns)
                        .itemsCallbackSingleChoice(current) { dialog, _, which, _ ->
                            val editor = activity.prefs.edit()
                            editor.putString(
                                PREFERENCE_GRID_COLUMNS,
                                savedPreferenceValues[which],
                            )
                            editor.apply()
                            dialog.dismiss()
                            updateGridColumnSummary()
                            true
                        }
                }.build()
            dialog.show()

            true
        }

    private val onClickFollowBatterySaver =
        Preference.OnPreferenceClickListener {
            // recreate the activity since the theme could have changed with this preference change
            activity.recreate()
            true
        }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.appearance_prefs, rootKey)

        val themePref = findPreference<Preference>(PreferencesConstants.FRAGMENT_THEME)
        val themes = resources.getStringArray(R.array.theme)
        currentTheme =
            activity
                .prefs
                .getString(PreferencesConstants.FRAGMENT_THEME, "4")!!
                .toInt()

        themePref?.summary = themes[currentTheme]
        themePref?.onPreferenceClickListener = onClickTheme

        val batterySaverPref =
            findPreference<Preference>(
                PreferencesConstants.FRAGMENT_FOLLOW_BATTERY_SAVER,
            )

        val currentThemeEnum = AppThemePreference.getTheme(currentTheme)
        batterySaverPref?.isVisible = currentThemeEnum.canBeLight
        batterySaverPref?.onPreferenceClickListener = onClickFollowBatterySaver

        findPreference<Preference>(PreferencesConstants.PREFERENCE_COLORED_NAVIGATION)
            ?.let {
                it.isEnabled = true
                it.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        activity.invalidateNavBar()

                        true
                    }
            }

        findPreference<Preference>(
            PreferencesConstants.PREFERENCE_SELECT_COLOR_CONFIG,
        )?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(ColorPrefsFragment())

                true
            }

        gridColumnPref = findPreference(PREFERENCE_GRID_COLUMNS)
        updateGridColumnSummary()
        gridColumnPref?.onPreferenceClickListener = onClickGridColumn
    }

    private fun updateGridColumnSummary() {
        val preferenceColumns =
            activity.prefs.getString(
                PREFERENCE_GRID_COLUMNS,
                PREFERENCE_GRID_COLUMNS_DEFAULT,
            )
        gridColumnPref?.summary = preferenceColumns
    }
}
