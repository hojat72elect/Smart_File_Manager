package com.amaze.filemanager.ui.fragments.preferencefragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.amaze.filemanager.R
import com.amaze.filemanager.utils.getLangPreferenceDropdownEntries

class UiPrefsFragment : BasePrefsFragment() {
    override val title = R.string.ui

    private var dragAndDropPref: Preference? = null

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.ui_prefs, rootKey)

        findPreference<Preference>("sidebar_bookmarks")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(BookmarksPrefsFragment())
                true
            }

        findPreference<Preference>("sidebar_quick_access")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(QuickAccessesPrefsFragment())
                true
            }

        findPreference<Preference>(PreferencesConstants.PREFERENCE_LANGUAGE)?.apply {
            val availableLocales = requireContext().getLangPreferenceDropdownEntries()
            val currentLanguagePreference =
                AppCompatDelegate.getApplicationLocales().let {
                    if (AppCompatDelegate.getApplicationLocales() ==
                        LocaleListCompat.getEmptyLocaleList()
                    ) {
                        0
                    } else {
                        availableLocales.values.indexOf(
                            AppCompatDelegate.getApplicationLocales()[0],
                        ) + 1
                    }
                }
            this.summary =
                if (currentLanguagePreference == 0) {
                    getString(R.string.preference_language_system_default)
                } else {
                    availableLocales.entries.find {
                        it.value == AppCompatDelegate.getApplicationLocales()[0]
                    }?.key
                }
            onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    MaterialDialog.Builder(activity).apply {
                        theme(activity.getUtilsProvider().appTheme.materialDialogTheme)
                        title(R.string.preference_language_dialog_title)
                        items(
                            arrayOf(getString(R.string.preference_language_system_default))
                                .plus(availableLocales.keys.toTypedArray())
                                .toSet(),
                        )
                        itemsCallbackSingleChoice(currentLanguagePreference) { dialog, _, _, textLabel ->
                            if (textLabel == getString(R.string.preference_language_system_default)) {
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.getEmptyLocaleList(),
                                )
                            } else {
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.create(availableLocales[textLabel]),
                                )
                            }
                            dialog.dismiss()
                            true
                        }
                    }.show()
                    true
                }
        }

        dragAndDropPref = findPreference(PreferencesConstants.PREFERENCE_DRAG_AND_DROP_PREFERENCE)
        updateDragAndDropPreferenceSummary()
        dragAndDropPref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val dragDialogBuilder = MaterialDialog.Builder(activity)
                dragDialogBuilder.theme(
                    activity.getUtilsProvider().appTheme.materialDialogTheme,
                )
                dragDialogBuilder.title(R.string.drag_and_drop_preference)
                val currentDragPreference: Int =
                    activity.prefs.getInt(
                        PreferencesConstants.PREFERENCE_DRAG_AND_DROP_PREFERENCE,
                        PreferencesConstants.PREFERENCE_DRAG_DEFAULT,
                    )
                dragDialogBuilder
                    .items(R.array.dragAndDropPreference)
                    .itemsCallbackSingleChoice(currentDragPreference) { dialog, _, which, _ ->
                        val editor = activity.prefs.edit()
                        editor.putInt(
                            PreferencesConstants.PREFERENCE_DRAG_AND_DROP_PREFERENCE,
                            which,
                        )
                        editor.putString(
                            PreferencesConstants.PREFERENCE_DRAG_AND_DROP_REMEMBERED,
                            null,
                        )
                        editor.apply()
                        dialog.dismiss()
                        updateDragAndDropPreferenceSummary()
                        true
                    }
                dragDialogBuilder.build().show()
                true
            }
    }

    private fun updateDragAndDropPreferenceSummary() {
        val value =
            activity.prefs.getInt(
                PreferencesConstants.PREFERENCE_DRAG_AND_DROP_PREFERENCE,
                PreferencesConstants.PREFERENCE_DRAG_DEFAULT,
            )
        val dragToMoveArray = resources.getStringArray(R.array.dragAndDropPreference)
        dragAndDropPref?.summary = dragToMoveArray[value]
    }
}
