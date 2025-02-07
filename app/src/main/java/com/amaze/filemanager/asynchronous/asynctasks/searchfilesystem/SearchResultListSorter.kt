package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import com.amaze.filemanager.filesystem.files.FileListSorter
import com.amaze.filemanager.filesystem.files.sort.DirSortBy
import com.amaze.filemanager.filesystem.files.sort.SortBy
import com.amaze.filemanager.filesystem.files.sort.SortType
import java.util.Date
import java.util.concurrent.TimeUnit

class SearchResultListSorter(
    private val dirArg: DirSortBy,
    private val sortType: SortType,
    private val searchTerm: String,
) : Comparator<SearchResult> {
    private val fileListSorter: FileListSorter by lazy { FileListSorter(dirArg, sortType) }

    private val relevanceComparator: Comparator<SearchResult> by lazy {
        Comparator { o1, o2 ->
            val currentTime = Date().time
            val comparator =
                compareBy<SearchResult> { (item, matchRange) ->
                    // the match percentage of the search term in the name
                    val matchPercentageScore =
                        matchRange.size().toDouble() / item.getParcelableName().length.toDouble()

                    // if the name starts with the search term
                    val startScore = (matchRange.first == 0).toInt()

                    // if the search term is surrounded by separators
                    // e.g. "my-cat" more relevant than "mysterious" for search term "my"
                    val wordScore =
                        item.getParcelableName().split('-', '_', '.', ' ').any {
                            it.contentEquals(
                                searchTerm,
                                ignoreCase = true,
                            )
                        }.toInt()

                    val modificationDate = item.getDate()
                    // the time difference as minutes
                    val timeDiff =
                        TimeUnit.MILLISECONDS.toMinutes(currentTime - modificationDate)
                    // 30 days as minutes
                    val relevantModificationPeriod = TimeUnit.DAYS.toMinutes(30)
                    val timeScore =
                        if (timeDiff < relevantModificationPeriod) {
                            // if the file was modified within the last 30 days, the recency is normalized
                            (relevantModificationPeriod - timeDiff) /
                                    relevantModificationPeriod.toDouble()
                        } else {
                            // for all older modification time, the recency doesn't change the relevancy
                            0.0
                        }

                    1.2 * matchPercentageScore +
                            0.7 * startScore +
                            0.7 * wordScore +
                            0.6 * timeScore
                }
            // Reverts the sorting to make most relevant first
            comparator.compare(o1, o2) * -1
        }
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    override fun compare(
        result1: SearchResult,
        result2: SearchResult,
    ): Int {
        return when (sortType.sortBy) {
            SortBy.RELEVANCE -> relevanceComparator.compare(result1, result2)
            SortBy.SIZE, SortBy.TYPE, SortBy.LAST_MODIFIED, SortBy.NAME ->
                fileListSorter.compare(result1.file, result2.file)
        }
    }
}
