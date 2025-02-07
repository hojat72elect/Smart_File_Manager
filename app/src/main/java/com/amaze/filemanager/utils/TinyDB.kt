package com.amaze.filemanager.utils

import android.content.SharedPreferences
import android.text.TextUtils

/**
 * Extract from: https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo Author:
 * https://github.com/kcochibili
 */
object TinyDB {
    /*
     *  The "‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK. U-201A
     *  + U-2017 + U-201A are used for separating the items in a list.
     */
    private const val DIVIDER = "‚‗‚"

    /**
     * Put array of Boolean into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param array array of Booleans to be added
     */
    @JvmStatic
    fun putBooleanArray(
        preferences: SharedPreferences,
        key: String?,
        array: Array<Boolean>,
    ) {
        preferences.edit().putString(key, TextUtils.join(DIVIDER, array)).apply()
    }

    /**
     * Get parsed array of Booleans from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return Array of Booleans
     */
    @JvmStatic
    fun getBooleanArray(
        preferences: SharedPreferences,
        key: String?,
        defaultValue: Array<Boolean>?,
    ): Array<Boolean>? {
        val prefValue = preferences.getString(key, "")
        if (prefValue == "") {
            return defaultValue
        }

        return TextUtils.split(prefValue, DIVIDER).map {
            java.lang.Boolean.valueOf(it)
        }.toTypedArray()
    }
}
