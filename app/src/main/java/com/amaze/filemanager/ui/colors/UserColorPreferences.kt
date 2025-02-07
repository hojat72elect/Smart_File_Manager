package com.amaze.filemanager.ui.colors

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
class UserColorPreferences(
    @ColorInt val primaryFirstTab: Int,
    @ColorInt val primarySecondTab: Int,
    @ColorInt val accent: Int,
    @ColorInt val iconSkin: Int,
) : Parcelable
