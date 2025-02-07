package com.amaze.filemanager.filesystem.files.sort

import android.content.Context
import com.amaze.filemanager.R

/**
 * Represents the sort by types.
 * [index] is the index of the sort in the xml string array resource
 * [sortDirectory] indicates if the sort can be used to sort an directory.
 */
enum class SortBy(val index: Int, val sortDirectory: Boolean) {
    NAME(0, true),
    LAST_MODIFIED(1, true),
    SIZE(2, true),
    TYPE(3, true),
    RELEVANCE(4, false),
    ;

    /** Returns the corresponding string resource of the enum */
    fun toResourceString(context: Context): String {
        return when (this) {
            NAME -> context.resources.getString(R.string.sort_name)
            LAST_MODIFIED -> context.resources.getString(R.string.lastModified)
            SIZE -> context.resources.getString(R.string.sort_size)
            TYPE -> context.resources.getString(R.string.type)
            RELEVANCE -> context.resources.getString(R.string.sort_relevance)
        }
    }

    companion object {
        private const val NAME_INDEX = 0
        private const val LAST_MODIFIED_INDEX = 1
        private const val SIZE_INDEX = 2
        private const val TYPE_INDEX = 3
        private const val RELEVANCE_INDEX = 4

        /** Returns the SortBy corresponding to [index] which can be used to sort directories */
        @JvmStatic
        fun getDirectorySortBy(index: Int): SortBy {
            return when (index) {
                NAME_INDEX -> NAME
                LAST_MODIFIED_INDEX -> LAST_MODIFIED
                SIZE_INDEX -> SIZE
                TYPE_INDEX -> TYPE
                else -> NAME
            }
        }

        /** Returns the SortBy corresponding to [index] */
        @JvmStatic
        fun getSortBy(index: Int): SortBy {
            return when (index) {
                NAME_INDEX -> NAME
                LAST_MODIFIED_INDEX -> LAST_MODIFIED
                SIZE_INDEX -> SIZE
                TYPE_INDEX -> TYPE
                RELEVANCE_INDEX -> RELEVANCE
                else -> NAME
            }
        }
    }
}
