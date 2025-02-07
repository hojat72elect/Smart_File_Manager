package com.amaze.filemanager.filesystem.files

import com.amaze.filemanager.adapters.data.LayoutElementParcelable
import com.amaze.filemanager.filesystem.files.sort.ComparableParcelable
import com.amaze.filemanager.filesystem.files.sort.DirSortBy
import com.amaze.filemanager.filesystem.files.sort.SortBy
import com.amaze.filemanager.filesystem.files.sort.SortType
import java.util.Locale
import kotlin.Boolean
import kotlin.Comparator
import kotlin.Int
import kotlin.String

/**
 * [Comparator] implementation to sort [LayoutElementParcelable]s.
 */
class FileListSorter(
    dirArg: DirSortBy,
    sortType: SortType,
) : Comparator<ComparableParcelable> {
    private var dirsOnTop = dirArg
    private val asc: Int = sortType.sortOrder.sortFactor
    private val sort: SortBy = sortType.sortBy

    private fun isDirectory(path: ComparableParcelable): Boolean {
        return path.isDirectory()
    }

    /** Compares the names of [file1] and [file2] */
    private fun compareName(
        file1: ComparableParcelable,
        file2: ComparableParcelable,
    ): Int {
        return file1.getParcelableName().compareTo(file2.getParcelableName(), ignoreCase = true)
    }

    /**
     * Compares two elements and return negative, zero and positive integer if first argument is less
     * than, equal to or greater than second
     */
    override fun compare(
        file1: ComparableParcelable,
        file2: ComparableParcelable,
    ): Int {

        if (dirsOnTop == DirSortBy.DIR_ON_TOP) {
            if (isDirectory(file1) && !isDirectory(file2)) {
                return -1
            } else if (isDirectory(file2) && !isDirectory(file1)) {
                return 1
            }
        } else if (dirsOnTop == DirSortBy.FILE_ON_TOP) {
            if (isDirectory(file1) && !isDirectory(file2)) {
                return 1
            } else if (isDirectory(file2) && !isDirectory(file1)) {
                return -1
            }
        }

        when (sort) {
            SortBy.NAME -> {
                // sort by name
                return asc * compareName(file1, file2)
            }

            SortBy.LAST_MODIFIED -> {
                // sort by last modified
                return asc * (file1.getDate()).compareTo(file2.getDate())
            }

            SortBy.SIZE -> {
                // sort by size
                return if (!isDirectory(file1) && !isDirectory(file2)) {
                    asc * (file1.getSize()).compareTo(file2.getSize())
                } else {
                    compareName(file1, file2)
                }
            }

            SortBy.TYPE -> {
                // sort by type
                return if (!isDirectory(file1) && !isDirectory(file2)) {
                    val extensionA = getExtension(file1.getParcelableName())
                    val extensionB = getExtension(file2.getParcelableName())
                    val res = asc * extensionA.compareTo(extensionB)
                    if (res == 0) {
                        asc * compareName(file1, file2)
                    } else {
                        res
                    }
                } else {
                    compareName(file1, file2)
                }
            }

            SortBy.RELEVANCE -> {
                // This case should not be called because it is not defined
                return 0
            }
        }
    }

    companion object {
        /**
         * Convenience method to get the file extension in given path.
         *
         * TODO: merge with same definition somewhere else (if any)
         */
        @JvmStatic
        fun getExtension(a: String): String {
            return a.substringAfterLast('.').lowercase(Locale.getDefault())
        }
    }
}
