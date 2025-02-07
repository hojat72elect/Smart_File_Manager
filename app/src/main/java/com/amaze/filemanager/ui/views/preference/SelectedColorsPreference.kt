package com.amaze.filemanager.ui.views.preference

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.dialogs.ColorPickerDialog
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants
import com.amaze.filemanager.ui.views.CircularColorsView

/**
 * This is the external notification that shows some text and a CircularColorsView.
 */
class SelectedColorsPreference(context: Context, attrs: AttributeSet) :
    DialogPreference(context, attrs) {
    private var colors =
        intArrayOf(
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
        )
    private var backgroundColor = 0
    private var visibility = View.VISIBLE
    private var selectedIndex = -1

    init {
        widgetLayoutResource = R.layout.selectedcolors_preference
        dialogLayoutResource = R.layout.dialog_colorpicker
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
        dialogIcon = null
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        (holder.findViewById(R.id.colorsection) as CircularColorsView).let { colorsView ->
            colorsView.setColors(colors[0], colors[1], colors[2], colors[3])
            colorsView.setDividerColor(backgroundColor)
            colorsView.visibility = visibility
        }
    }

    override fun getSummary(): CharSequence {
        val colorPickerPref =
            sharedPreferences?.getInt(
                PreferencesConstants.PREFERENCE_COLOR_CONFIG,
                ColorPickerDialog.NO_DATA,
            ) ?: ColorPickerDialog.NO_DATA
        return context.getString(ColorPickerDialog.getTitle(colorPickerPref))
    }

    override fun onGetDefaultValue(
        a: TypedArray,
        index: Int,
    ): Any {
        return a.getString(index)!!
    }

    override fun onSaveInstanceState(): Parcelable {
        val myState = ColorPickerDialog.SavedState(super.onSaveInstanceState())
        myState.selectedItem = selectedIndex
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state.javaClass != ColorPickerDialog.SavedState::class.java) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as ColorPickerDialog.SavedState
        selectedIndex = myState.selectedItem
        super.onRestoreInstanceState(myState.superState)
    }
}
