package com.amaze.filemanager.utils

import java.util.Arrays

class OneCharacterCharSequence(private val value: Char, override val length: Int) : CharSequence {
    override fun get(index: Int): Char =
        if (index < length) {
            value
        } else {
            throw IndexOutOfBoundsException()
        }

    override fun subSequence(
        startIndex: Int,
        endIndex: Int,
    ): CharSequence = OneCharacterCharSequence(value, endIndex - startIndex)

    override fun toString(): String {
        val array = CharArray(length)
        Arrays.fill(array, value)
        return String(array)
    }
}
