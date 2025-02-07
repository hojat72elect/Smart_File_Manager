package com.amaze.filemanager.filesystem.files.sort

/**
 * Represents the direction the sort should be ordered
 *
 * [sortFactor] is the factor that should be multiplied to the result of `compareTo()` to achieve the correct sort direction
 */
enum class SortOrder(val sortFactor: Int) {
    ASC(1),
    DESC(-1),
}
