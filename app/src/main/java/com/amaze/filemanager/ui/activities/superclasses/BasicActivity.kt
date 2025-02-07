package com.amaze.filemanager.ui.activities.superclasses

import androidx.appcompat.app.AppCompatActivity
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.ui.colors.ColorPreferenceHelper
import com.amaze.filemanager.ui.provider.UtilitiesProvider
import com.amaze.filemanager.ui.theme.AppTheme

open class BasicActivity : AppCompatActivity() {

    private fun getAppConfig() = application as AmazeFileManagerApplication

    fun getColorPreference(): ColorPreferenceHelper = getAppConfig().utilsProvider.colorPreference

    fun getAppTheme(): AppTheme = getAppConfig().utilsProvider.appTheme

    fun getUtilsProvider(): UtilitiesProvider = getAppConfig().utilsProvider
}