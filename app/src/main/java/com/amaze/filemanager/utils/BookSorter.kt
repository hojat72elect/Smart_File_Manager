package com.amaze.filemanager.utils

class BookSorter : Comparator<Array<String>> {
    override fun compare(
        lhs: Array<String>,
        rhs: Array<String>,
    ): Int {
        var result = lhs[0].compareTo(rhs[0], ignoreCase = true)
        if (result == 0) {
            // the title is same, compare their paths
            result = lhs[1].compareTo(rhs[1], ignoreCase = true)
        }
        return result
    }
}
