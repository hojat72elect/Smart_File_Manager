package com.amaze.filemanager.utils

import android.graphics.Color

object PreferenceUtils {
    const val DEFAULT_CURRENT_TAB = 1

    const val DEFAULT_SAVED_PATHS = true

    @JvmStatic
    fun getStatusColor(color: Int): Int =
        Color.argb(
            Color.alpha(color),
            (Color.red(color) * 0.6f).toInt().coerceAtLeast(0),
            (Color.green(color) * 0.6f).toInt().coerceAtLeast(0),
            (Color.blue(color) * 0.6f).toInt().coerceAtLeast(0),
        )
}
