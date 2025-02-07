package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import android.database.Cursor
import android.provider.MediaStore
import com.amaze.filemanager.filesystem.RootHelper
import kotlinx.coroutines.isActive
import java.io.File
import kotlin.coroutines.coroutineContext

class IndexedSearch(
    query: String,
    path: String,
    searchParameters: SearchParameters,
    private val cursor: Cursor,
) : FileSearch(query, path, searchParameters) {
    override suspend fun search(filter: SearchFilter) {
        if (cursor.count > 0 && cursor.moveToFirst()) {
            do {
                val nextPath =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA),
                    )
                val displayName =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME),
                    )
                if (nextPath != null && displayName != null && nextPath.contains(path)) {
                    val resultRange = filter.searchFilter(displayName)
                    if (resultRange != null) {
                        val hybridFileParcelable =
                            RootHelper.generateBaseFile(
                                File(nextPath),
                                SearchParameter.SHOW_HIDDEN_FILES in searchParameters,
                            )
                        if (hybridFileParcelable != null) {
                            publishProgress(hybridFileParcelable, resultRange)
                        }
                    }
                }
            } while (cursor.moveToNext() && coroutineContext.isActive)
        }

        cursor.close()
    }
}
