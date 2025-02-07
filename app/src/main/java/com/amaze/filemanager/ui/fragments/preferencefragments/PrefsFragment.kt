package com.amaze.filemanager.ui.fragments.preferencefragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.activities.AboutActivity
import com.amaze.filemanager.utils.Utils

class PrefsFragment : BasePrefsFragment() {
    override val title = R.string.setting

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("appearance")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(AppearancePrefsFragment())
                true
            }

        findPreference<Preference>("ui")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(UiPrefsFragment())
                true
            }

        findPreference<Preference>("behavior")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(BehaviorPrefsFragment())
                true
            }

        findPreference<Preference>("security")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(SecurityPrefsFragment())
                true
            }

        findPreference<Preference>("backup")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity.pushFragment(BackupPrefsFragment())
                true
            }

        findPreference<Preference>("about")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                startActivity(Intent(activity, AboutActivity::class.java))
                false
            }

        findPreference<Preference>("feedback")
            ?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val emailIntent =
                    Utils.buildEmailIntent(requireContext(), null, Utils.EMAIL_SUPPORT)

                val activities =
                    activity.packageManager.queryIntentActivities(
                        emailIntent,
                        PackageManager.MATCH_DEFAULT_ONLY,
                    )

                if (activities.isNotEmpty()) {
                    startActivity(
                        Intent.createChooser(
                            emailIntent,
                            resources.getString(R.string.feedback),
                        ),
                    )
                } else {
                    Toast.makeText(
                        getActivity(),
                        resources.getString(R.string.send_email_to) + " " + Utils.EMAIL_SUPPORT,
                        Toast.LENGTH_LONG,
                    )
                        .show()
                }

                false
            }
    }
}
