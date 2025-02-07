package com.amaze.filemanager.ui.theme;

import com.afollestad.materialdialogs.Theme;

/**
 * This enum represents the theme of the app (LIGHT or DARK)
 */
public enum AppTheme {
    LIGHT,
    DARK,
    BLACK;

    public Theme getMaterialDialogTheme() {
        switch (this) {
            default:
            case LIGHT:
                return Theme.LIGHT;
            case DARK:
            case BLACK:
                return Theme.DARK;
        }
    }
}
