package com.amaze.filemanager.ui.fragments.preferencefragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.amaze.filemanager.R
import com.amaze.filemanager.utils.TinyDB

class QuickAccessesPrefsFragment : BasePrefsFragment() {
    override val title = R.string.show_quick_access_pref

    companion object {
        const val KEY = "quick access array"
        val KEYS =
            arrayOf(
                "fastaccess",
                "recent",
                "image",
                "video",
                "audio",
                "documents",
                "apks",
            )
        val DEFAULT = arrayOf(true, true, true, true, true, true, true)

        val prefPos: Map<String, Int> =
            KEYS.withIndex().associate {
                Pair(it.value, it.index)
            }
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.quickaccess_prefs, rootKey)

        val currentValue = TinyDB.getBooleanArray(activity.prefs, KEY, DEFAULT)!!

        val onChange =
            Preference.OnPreferenceClickListener { preference ->
                prefPos[preference.key]?.let {
                    currentValue[it] = (preference as SwitchPreference).isChecked
                    TinyDB.putBooleanArray(activity.prefs, KEY, currentValue)
                }

                true
            }

        for (key in KEYS) {
            findPreference<Preference>(key)?.onPreferenceClickListener = onChange
        }
    }
}
