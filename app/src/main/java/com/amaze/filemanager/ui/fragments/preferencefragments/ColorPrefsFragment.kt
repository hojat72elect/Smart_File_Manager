package com.amaze.filemanager.ui.fragments.preferencefragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.amaze.filemanager.R
import com.amaze.filemanager.adapters.ColorAdapter
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.databinding.DialogGridBinding
import com.amaze.filemanager.ui.colors.ColorPreference
import com.amaze.filemanager.ui.colors.UserColorPreferences
import com.amaze.filemanager.ui.dialogs.ColorPickerDialog

class ColorPrefsFragment : BasePrefsFragment() {
    override val title = R.string.color_title

    private var dialog: MaterialDialog? = null

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.color_prefs, rootKey)

        val showColorChangeDialogListener =
            Preference.OnPreferenceClickListener {
                showColorChangeDialog(it.key)

                true
            }

        val colorPickerPref =
            activity.prefs.getInt(
                PreferencesConstants.PREFERENCE_COLOR_CONFIG,
                ColorPickerDialog.NO_DATA,
            )

        val skin = findPreference<Preference>(PreferencesConstants.PREFERENCE_SKIN)
        val skinTwo = findPreference<Preference>(PreferencesConstants.PREFERENCE_SKIN_TWO)
        val accent = findPreference<Preference>(PreferencesConstants.PREFERENCE_ACCENT)
        val icon = findPreference<Preference>(PreferencesConstants.PREFERENCE_ICON_SKIN)

        if (colorPickerPref != ColorPickerDialog.CUSTOM_INDEX) {
            skin?.isEnabled = false
            skinTwo?.isEnabled = false
            accent?.isEnabled = false
            icon?.isEnabled = false
        } else {
            skin?.onPreferenceClickListener = showColorChangeDialogListener
            skinTwo?.onPreferenceClickListener = showColorChangeDialogListener
            accent?.onPreferenceClickListener = showColorChangeDialogListener
            icon?.onPreferenceClickListener = showColorChangeDialogListener
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference.key == PreferencesConstants.PRESELECTED_CONFIGS) {
            showPreselectedColorsConfigDialog()
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    private fun showPreselectedColorsConfigDialog() {
        val newDialog =
            ColorPickerDialog.newInstance(
                PreferencesConstants.PRESELECTED_CONFIGS,
                activity.currentColorPreference,
                activity.getAppTheme(),
            )
        newDialog.setListener {
            val colorPickerPref =
                activity.prefs.getInt(
                    PreferencesConstants.PREFERENCE_COLOR_CONFIG,
                    ColorPickerDialog.NO_DATA,
                )
            if (colorPickerPref == ColorPickerDialog.RANDOM_INDEX) {
                AmazeFileManagerApplication.toast(getActivity(), R.string.set_random)
            }

            activity.recreate()
        }
        newDialog.setTargetFragment(this, 0)
        newDialog.show(parentFragmentManager, PreferencesConstants.PREFERENCE_SELECT_COLOR_CONFIG)
    }

    private fun showColorChangeDialog(colorPrefKey: String) {
        val currentColorPreference = activity.currentColorPreference ?: return

        @ColorInt val currentColor =
            when (colorPrefKey) {
                PreferencesConstants.PREFERENCE_SKIN -> currentColorPreference.primaryFirstTab
                PreferencesConstants.PREFERENCE_SKIN_TWO -> currentColorPreference.primarySecondTab
                PreferencesConstants.PREFERENCE_ACCENT -> currentColorPreference.accent
                PreferencesConstants.PREFERENCE_ICON_SKIN -> currentColorPreference.iconSkin
                else -> 0
            }

        val adapter =
            ColorAdapter(
                activity,
                ColorPreference.availableColors,
                currentColor,
            ) { selectedColor: Int ->
                @ColorInt var primaryFirst = currentColorPreference.primaryFirstTab

                @ColorInt var primarySecond = currentColorPreference.primarySecondTab

                @ColorInt var accent = currentColorPreference.accent

                @ColorInt var iconSkin = currentColorPreference.iconSkin
                when (colorPrefKey) {
                    PreferencesConstants.PREFERENCE_SKIN -> primaryFirst = selectedColor
                    PreferencesConstants.PREFERENCE_SKIN_TWO -> primarySecond = selectedColor
                    PreferencesConstants.PREFERENCE_ACCENT -> accent = selectedColor
                    PreferencesConstants.PREFERENCE_ICON_SKIN -> iconSkin = selectedColor
                }
                activity
                    .getColorPreference()
                    .saveColorPreferences(
                        activity.prefs,
                        UserColorPreferences(primaryFirst, primarySecond, accent, iconSkin),
                    )
                dialog?.dismiss()
                activity.recreate()
            }

        val v =
            DialogGridBinding.inflate(LayoutInflater.from(requireContext())).root.also {
                it.adapter = adapter
                it.onItemClickListener = adapter
            }

        val fabSkin = activity.accent

        dialog =
            MaterialDialog.Builder(activity)
                .positiveText(R.string.cancel)
                .title(R.string.choose_color)
                .theme(activity.getAppTheme().materialDialogTheme)
                .autoDismiss(true)
                .positiveColor(fabSkin)
                .neutralColor(fabSkin)
                .neutralText(R.string.default_string)
                .onNeutral { _, _ ->
                    activity
                        .getColorPreference()
                        .saveColorPreferences(activity.prefs, currentColorPreference)
                    activity.recreate()
                }.customView(v, false)
                .show()
    }
}
