package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import android.content.Context
import com.amaze.filemanager.fileoperations.filesystem.OpenMode
import com.amaze.filemanager.filesystem.HybridFile
import kotlinx.coroutines.isActive
import org.slf4j.LoggerFactory
import kotlin.coroutines.coroutineContext

class DeepSearch(
    query: String,
    path: String,
    searchParameters: SearchParameters,
    context: Context,
    private val openMode: OpenMode,
) : FileSearch(query, path, searchParameters) {

    private val applicationContext: Context = context.applicationContext

    /**
     * Search for occurrences of a given text in file names and publish the result.
     */
    override suspend fun search(filter: SearchFilter) {
        val directory = HybridFile(openMode, path)
        if (directory.isSmb) return

        if (directory.isDirectory(applicationContext)) {
            // you have permission to read this directory
            val worklist = ArrayDeque<HybridFile>()
            worklist.add(directory)
            while (coroutineContext.isActive && worklist.isNotEmpty()) {
                val nextFile = worklist.removeFirst()
                nextFile.forEachChildrenFile(
                    applicationContext,
                    SearchParameter.ROOT in searchParameters,
                ) { file ->
                    if (!file.isHidden || SearchParameter.SHOW_HIDDEN_FILES in searchParameters) {
                        val resultRange = filter.searchFilter(file.getName(applicationContext))
                        if (resultRange != null) {
                            publishProgress(file, resultRange)
                        }
                        if (file.isDirectory(applicationContext)) {
                            worklist.add(file)
                        }
                    }
                }
            }
        } else {
            LOG.warn("Cannot search " + directory.path + ": Permission Denied")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DeepSearch::class.java)
    }
}
