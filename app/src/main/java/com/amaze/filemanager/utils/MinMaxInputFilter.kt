package com.amaze.filemanager.utils

import android.text.InputFilter
import android.text.Spanned

class MinMaxInputFilter(private val min: Int, private val max: Int) : InputFilter {
    constructor(range: IntRange) : this(range.first, range.last)

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {
        runCatching {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) {
                return null
            }
        }
        return ""
    }

    private fun isInRange(
        minValue: Int,
        maxValue: Int,
        input: Int,
    ): Boolean {
        return if (maxValue > minValue) {
            input in minValue..maxValue
        } else {
            input in maxValue..minValue
        }
    }
}
