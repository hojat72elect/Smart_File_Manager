package com.amaze.filemanager.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.activities.superclasses.ThemedActivity
import com.amaze.filemanager.ui.colors.ColorPreferenceHelper
import com.amaze.filemanager.ui.fragments.preferencefragments.BasePrefsFragment
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants
import com.amaze.filemanager.ui.fragments.preferencefragments.PrefsFragment
import com.amaze.filemanager.ui.theme.AppTheme
import com.amaze.filemanager.utils.PreferenceUtils
import com.amaze.filemanager.utils.Utils
import java.io.File

class PreferencesActivity : ThemedActivity(), FolderChooserDialog.FolderCallback {
    private companion object {
        const val SAVED_INSTANCE_STATE_KEY = "savedInstanceState"
    }

    lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        var savedInstanceState = savedInstanceState
        if (savedInstanceState == null && intent.hasExtra(SAVED_INSTANCE_STATE_KEY)) {
            savedInstanceState = intent.getBundleExtra(SAVED_INSTANCE_STATE_KEY)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        layout = findViewById(R.id.activity_preferences)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.displayOptions =
            ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        initStatusBarResources(layout)

        if (savedInstanceState == null) {
            val fragment = PrefsFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.preferences_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.action = Intent.CATEGORY_LAUNCHER
        finish()
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            false
        }
    }

    override fun recreate() {
        val bundle = Bundle()
        onSaveInstanceState(bundle)
        val intent = Intent(this, javaClass)
        intent.putExtra(SAVED_INSTANCE_STATE_KEY, bundle)

        finish()
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Push a new fragment into the stack
     */
    fun pushFragment(fragment: BasePrefsFragment) {
        supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            replace(R.id.preferences_container, fragment)
            supportActionBar?.title = getString(fragment.title)
            addToBackStack(null)
        }
    }

    /**
     * Rebuild the nav bar
     *
     * Used to update color
     */
    fun invalidateNavBar() {
        val primaryColor =
            ColorPreferenceHelper
                .getPrimary(currentColorPreference, MainActivity.currentTab)

        val colouredNavigation = getBoolean(PreferencesConstants.PREFERENCE_COLORED_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val tabStatusColor = PreferenceUtils.getStatusColor(primaryColor)
        window.statusBarColor = tabStatusColor
        when {
            colouredNavigation -> {
                window.navigationBarColor = tabStatusColor
            }

            getAppTheme() == AppTheme.BLACK -> {
                window.navigationBarColor = Color.BLACK
            }

            getAppTheme() == AppTheme.DARK -> {
                window.navigationBarColor = Utils.getColor(this, R.color.holo_dark_background)
            }

            getAppTheme() == AppTheme.LIGHT -> {
                window.navigationBarColor = Color.WHITE
            }
        }

        if (getAppTheme() == AppTheme.BLACK) {
            window.decorView.setBackgroundColor(Utils.getColor(this, android.R.color.black))
        }
    }

    override fun onFolderSelection(
        dialog: FolderChooserDialog,
        folder: File,
    ) {
        supportFragmentManager.fragments.lastOrNull { it is BasePrefsFragment }?.let {
            (it as BasePrefsFragment).onFolderSelection(dialog, folder)
        }
    }

    override fun onFolderChooserDismissed(dialog: FolderChooserDialog) {
        supportFragmentManager.fragments.lastOrNull { it is BasePrefsFragment }?.let {
            (it as BasePrefsFragment).onFolderChooserDismissed(dialog)
        }
    }
}
