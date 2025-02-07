package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.FileSearch.SearchFilter
import com.amaze.filemanager.filesystem.HybridFileParcelable
import java.util.Locale
import java.util.regex.Pattern

abstract class FileSearch(
    protected val query: String,
    protected val path: String,
    protected val searchParameters: SearchParameters,
) {
    private val mutableFoundFilesLiveData: MutableLiveData<List<SearchResult>> =
        MutableLiveData()
    val foundFilesLiveData: LiveData<List<SearchResult>> = mutableFoundFilesLiveData
    private val foundFilesList: MutableList<SearchResult> = mutableListOf()

    /**
     * Search for files, whose names match [query], starting from [path] and add them to
     * [foundFilesLiveData]
     */
    suspend fun search() {
        if (SearchParameter.REGEX !in searchParameters) {
            // regex not turned on so we use simpleFilter
            this.search(simpleFilter(query))
        } else {
            if (SearchParameter.REGEX_MATCHES !in searchParameters) {
                // only regex turned on so we use regexFilter
                this.search(regexFilter(query))
            } else {
                // regex turned on and names must match pattern so use regexMatchFilter
                this.search(regexMatchFilter(query))
            }
        }
    }

    /**
     * Search for files, whose names fulfill [filter], starting from [path] and add them to
     * [foundFilesLiveData].
     */
    protected abstract suspend fun search(filter: SearchFilter)

    /**
     * Add [file] to list of found files and post it to [foundFilesLiveData]
     */
    protected fun publishProgress(
        file: HybridFileParcelable,
        matchRange: MatchRange,
    ) {
        foundFilesList.add(SearchResult(file, matchRange))
        mutableFoundFilesLiveData.postValue(foundFilesList)
    }

    private fun simpleFilter(query: String): SearchFilter =
        SearchFilter { fileName ->
            // check case-insensitively if query is contained in fileName
            val start =
                fileName.lowercase(Locale.getDefault()).indexOf(
                    query.lowercase(
                        Locale.getDefault(),
                    ),
                )
            if (start >= 0) {
                start until start + query.length
            } else {
                null
            }
        }

    private fun regexFilter(query: String): SearchFilter {
        val pattern = regexPattern(query)
        return SearchFilter { fileName ->
            // check case-insensitively if the pattern compiled from query can be found in fileName
            val matcher = pattern.matcher(fileName)
            if (matcher.find()) {
                matcher.start() until matcher.end()
            } else {
                null
            }
        }
    }

    private fun regexMatchFilter(query: String): SearchFilter {
        val pattern = regexPattern(query)
        return SearchFilter { fileName ->
            // check case-insensitively if the pattern compiled from query matches fileName
            if (pattern.matcher(fileName).matches()) {
                fileName.indices
            } else {
                null
            }
        }
    }

    private fun regexPattern(query: String): Pattern =
        // compiles the given query into a Pattern
        Pattern.compile(
            bashRegexToJava(query),
            Pattern.CASE_INSENSITIVE,
        )

    /**
     * method converts bash style regular expression to java. See [Pattern]
     *
     * @return converted string
     */
    private fun bashRegexToJava(originalString: String): String {
        val stringBuilder = StringBuilder()
        for (i in originalString.indices) {
            when (originalString[i].toString() + "") {
                "*" -> stringBuilder.append("\\w*")
                "?" -> stringBuilder.append("\\w")
                else -> stringBuilder.append(originalString[i])
            }
        }
        return stringBuilder.toString()
    }

    fun interface SearchFilter {
        /**
         * If the file with the given [fileName] fulfills some predicate, returns the part that fulfills the predicate.
         * Otherwise returns null.
         */
        fun searchFilter(fileName: String): MatchRange?
    }
}
