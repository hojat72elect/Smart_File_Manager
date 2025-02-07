package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import android.content.Context
import com.amaze.filemanager.filesystem.HybridFileParcelable
import com.amaze.filemanager.filesystem.root.ListFilesCommand.listFiles

class BasicSearch(
    query: String,
    path: String,
    searchParameters: SearchParameters,
    context: Context,
) : FileSearch(query, path, searchParameters) {
    private val applicationContext = context.applicationContext

    override suspend fun search(filter: SearchFilter) {
        listFiles(
            path,
            SearchParameter.ROOT in searchParameters,
            SearchParameter.SHOW_HIDDEN_FILES in searchParameters,
            { },
        ) { hybridFileParcelable: HybridFileParcelable ->
            if (SearchParameter.SHOW_HIDDEN_FILES in searchParameters ||
                !hybridFileParcelable.isHidden
            ) {
                val resultRange =
                    filter.searchFilter(hybridFileParcelable.getName(applicationContext))
                if (resultRange != null) {
                    publishProgress(hybridFileParcelable, resultRange)
                }
            }
        }
    }
}
