package com.amaze.filemanager.asynchronous.asynctasks

import android.os.AsyncTask
import android.text.TextUtils
import com.amaze.filemanager.ui.activities.texteditor.SearchResultIndex
import com.amaze.filemanager.utils.OnAsyncTaskFinished
import com.amaze.filemanager.utils.OnProgressUpdate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.LineNumberReader
import java.io.StringReader

class SearchTextTask(
    private val textToSearch: String,
    private val searchedText: String,
    private val updateListener: OnProgressUpdate<SearchResultIndex>,
    private val listener: OnAsyncTaskFinished<List<SearchResultIndex>>,
) : AsyncTask<Unit, SearchResultIndex, List<SearchResultIndex>>() {
    private val lineNumberReader: LineNumberReader

    private val log: Logger = LoggerFactory.getLogger(SearchTextTask::class.java)

    override fun doInBackground(vararg params: Unit): List<SearchResultIndex> {
        if (TextUtils.isEmpty(searchedText)) {
            return emptyList()
        }

        val searchResultIndices = ArrayList<SearchResultIndex>()
        var charIndex = 0
        while (charIndex < textToSearch.length - searchedText.length) {
            if (isCancelled) break
            val nextPosition = textToSearch.indexOf(searchedText, charIndex)
            if (nextPosition == -1) {
                break
            }
            try {
                lineNumberReader.skip((nextPosition - charIndex).toLong())
            } catch (e: IOException) {
                log.warn("failed to search text", e)
            }
            charIndex = nextPosition
            val index =
                SearchResultIndex(
                    charIndex,
                    charIndex + searchedText.length,
                    lineNumberReader.lineNumber,
                )
            searchResultIndices.add(index)
            publishProgress(index)
            charIndex++
        }

        return searchResultIndices
    }

    override fun onProgressUpdate(vararg values: SearchResultIndex) {
        updateListener.onUpdate(values[0])
    }

    override fun onPostExecute(searchResultIndices: List<SearchResultIndex>) {
        listener.onAsyncTaskFinished(searchResultIndices)
    }

    init {
        val stringReader = StringReader(textToSearch)
        lineNumberReader = LineNumberReader(stringReader)
    }
}
