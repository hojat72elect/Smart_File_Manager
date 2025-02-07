package com.amaze.filemanager.filesystem.files.sort

/** Represents the way in which directories and files should be sorted */
enum class DirSortBy {
    DIR_ON_TOP,
    FILE_ON_TOP,
    NONE_ON_TOP,
    ;

    companion object {
        /** Returns the corresponding [DirSortBy] to [index] */
        @JvmStatic
        fun getDirSortBy(index: Int): DirSortBy {
            return when (index) {
                0 -> DIR_ON_TOP
                1 -> FILE_ON_TOP
                2 -> NONE_ON_TOP
                else -> NONE_ON_TOP
            }
        }
    }
}
