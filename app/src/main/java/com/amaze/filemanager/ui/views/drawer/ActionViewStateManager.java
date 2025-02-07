package com.amaze.filemanager.ui.views.drawer;

import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatImageButton;

/**
 * This manages to set the color of the selected ActionView and unset the ActionView that is not
 * selected anymore
 */
public class ActionViewStateManager {

    private final @ColorInt int idleIconColor;
    private final @ColorInt int selectedIconColor;
    private AppCompatImageButton lastItemSelected = null;

    public ActionViewStateManager(@ColorInt int idleColor, @ColorInt int accentColor) {
        idleIconColor = idleColor;
        selectedIconColor = accentColor;
    }

    public void deselectCurrentActionView() {
        if (lastItemSelected != null) {
            lastItemSelected.setColorFilter(idleIconColor);
            lastItemSelected = null;
        }
    }

    public void selectActionView(MenuItem item) {
        if (lastItemSelected != null) {
            lastItemSelected.setColorFilter(idleIconColor);
        }
        if (item.getActionView() != null) {
            lastItemSelected = (AppCompatImageButton) item.getActionView();
            lastItemSelected.setColorFilter(selectedIconColor);
        }
    }
}
