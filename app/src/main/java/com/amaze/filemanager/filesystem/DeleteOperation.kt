package com.amaze.filemanager.filesystem

import android.content.Context
import java.io.File

object DeleteOperation {
    private val LOG = "DeleteFileOperation"

    /**
     * Delete a folder.
     *
     * @param file The folder name.
     * @return true if successful.
     */
    @JvmStatic
    private fun rmdir(
        file: File,
        context: Context,
    ): Boolean {
        if (!file.exists()) return true
        val files = file.listFiles()
        if (files != null && files.size > 0) {
            for (child in files) {
                rmdir(child, context)
            }
        }

        // Try the normal way
        if (file.delete()) {
            return true
        }

        // Try with Storage Access Framework.

        val document = ExternalSdCardOperation.getDocumentFile(file, true, context)
        if (document != null && document.delete()) {
            return true
        }

        return !file.exists()
    }

    /**
     * Delete a file. May be even on external SD card.
     *
     * @param file the file to be deleted.
     * @return True if successfully deleted.
     */
    @JvmStatic
    fun deleteFile(
        file: File,
        context: Context,
    ): Boolean {
        // First try the normal deletion.
        val fileDelete = rmdir(file, context)
        if (file.delete() || fileDelete) return true

        // Try with Storage Access Framework.
        if (ExternalSdCardOperation.isOnExtSdCard(file, context)) {
            val document = ExternalSdCardOperation.getDocumentFile(file, false, context)
            document ?: return true
            return document.delete()
        }

        return !file.exists()
    }
}
