package com.amaze.filemanager.filesystem.files.sort

/** Describes how to sort with [sortBy] and which direction to use for the sort with [sortOrder] */
data class SortType(val sortBy: SortBy, val sortOrder: SortOrder) {
    /**
     * Returns the Int corresponding to the combination of [sortBy] and [sortOrder]
     */
    fun toDirectorySortInt(): Int {
        val sortIndex = if (sortBy.sortDirectory) sortBy.index else 0
        return when (sortOrder) {
            SortOrder.ASC -> sortIndex
            SortOrder.DESC -> sortIndex + 4
        }
    }

    companion object {
        /**
         * Returns the [SortType] with the [SortBy] and [SortOrder] corresponding to [index]
         */
        @JvmStatic
        fun getDirectorySortType(index: Int): SortType {
            val sortOrder = if (index <= 3) SortOrder.ASC else SortOrder.DESC
            val normalizedIndex = if (index <= 3) index else index - 4
            val sortBy = SortBy.getDirectorySortBy(normalizedIndex)
            return SortType(sortBy, sortOrder)
        }
    }
}
