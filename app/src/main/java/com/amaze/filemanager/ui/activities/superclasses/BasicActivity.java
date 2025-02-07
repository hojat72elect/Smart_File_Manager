package com.amaze.filemanager.ui.activities.superclasses;

import androidx.appcompat.app.AppCompatActivity;

import com.amaze.filemanager.ui.colors.ColorPreferenceHelper;
import com.amaze.filemanager.ui.provider.UtilitiesProvider;
import com.amaze.filemanager.ui.theme.AppTheme;
import com.amaze.filemanager.application.AmazeFileManagerApplication;

public class BasicActivity extends AppCompatActivity {

    protected AmazeFileManagerApplication getAppConfig() {
        return (AmazeFileManagerApplication) getApplication();
    }

    public ColorPreferenceHelper getColorPreference() {
        return getAppConfig().getUtilsProvider().getColorPreference();
    }

    public AppTheme getAppTheme() {
        return getAppConfig().getUtilsProvider().getAppTheme();
    }

    public UtilitiesProvider getUtilsProvider() {
        return getAppConfig().getUtilsProvider();
    }
}
