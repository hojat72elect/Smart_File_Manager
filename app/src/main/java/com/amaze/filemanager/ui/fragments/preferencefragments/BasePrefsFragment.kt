package com.amaze.filemanager.ui.fragments.preferencefragments

import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
import com.amaze.filemanager.ui.activities.PreferencesActivity
import java.io.File

abstract class BasePrefsFragment : PreferenceFragmentCompat(), FolderChooserDialog.FolderCallback {
    protected val activity: PreferencesActivity
        get() = requireActivity() as PreferencesActivity

    abstract val title: Int

    override fun onResume() {
        super.onResume()

        activity.supportActionBar?.title = getString(title)
    }

    override fun onFolderSelection(
        dialog: FolderChooserDialog,
        folder: File,
    ) {
        dialog.dismiss()
    }

    override fun onFolderChooserDismissed(dialog: FolderChooserDialog) {
        dialog.dismiss()
    }
}
