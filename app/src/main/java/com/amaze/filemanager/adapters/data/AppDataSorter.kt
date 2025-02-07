package com.amaze.filemanager.adapters.data

import com.amaze.filemanager.utils.safeLet

class AppDataSorter(var sort: Int, isAscending: Boolean) :
    Comparator<AppDataParcelable?> {
    private val asc: Int = if (isAscending) 1 else -1

    /**
     * Compares two elements and return negative, zero and positive integer if first argument is
     * less than, equal to or greater than second
     */
    override fun compare(
        file1: AppDataParcelable?,
        file2: AppDataParcelable?,
    ): Int {
        safeLet(file1, file2) { f1, f2 ->
            if (f1.isSystemApp != f2.isSystemApp) {
                return if (f1.isSystemApp) -1 else 1
            }

            when (sort) {
                SORT_NAME -> {
                    // sort by name
                    return asc * f1.label.compareTo(f2.label, ignoreCase = true)
                }

                SORT_MODIF -> {
                    // sort by last modified
                    return asc *
                            java.lang.Long.valueOf(f1.lastModification)
                                .compareTo(f2.lastModification)
                }

                SORT_SIZE -> {
                    // sort by size
                    return asc * java.lang.Long.valueOf(f1.size).compareTo(f2.size)
                }

                else -> return 0
            }
        }
        return 0
    }

    companion object {
        const val SORT_NAME = 0
        const val SORT_MODIF = 1
        const val SORT_SIZE = 2
    }
}
