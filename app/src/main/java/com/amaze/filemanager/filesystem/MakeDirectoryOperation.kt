package com.amaze.filemanager.filesystem

import android.content.Context
import com.amaze.filemanager.fileoperations.filesystem.OpenMode
import com.amaze.filemanager.utils.OTGUtil
import jcifs.smb.SmbException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

// This object is here to not polute the global namespace
// All functions must be static
object MakeDirectoryOperation {
    private val log: Logger = LoggerFactory.getLogger(MakeDirectoryOperation::class.java)

    /**
     * Create a folder. The folder may even be on external SD card for Kitkat.
     *
     * @param file The folder to be created.
     * @return True if creation was successful.
     */
    @JvmStatic
    @Deprecated("use {@link #mkdirs(Context, HybridFile)}")
    fun mkdir(
        file: File?,
        context: Context,
    ): Boolean {
        if (file == null) return false
        if (file.exists()) {
            // nothing to create.
            return file.isDirectory
        }

        // Try the normal way
        if (file.mkdirs()) {
            return true
        }

        // Try with Storage Access Framework.
        if (ExternalSdCardOperation.isOnExtSdCard(file, context)) {
            val document = ExternalSdCardOperation.getDocumentFile(file, true, context)
            document ?: return false
            // getDocumentFile implicitly creates the directory.
            return document.exists()
        }

        return false
    }

    /**
     * Creates the directories on given [file] path, including nonexistent parent directories.
     * So use proper [HybridFile] constructor as per your need.
     *
     * @return true if successfully created directory, otherwise returns false.
     */
    @JvmStatic
    fun mkdirs(
        context: Context,
        file: HybridFile,
    ): Boolean {
        var isSuccessful = true
        when (file.mode) {
            OpenMode.SMB ->
                try {
                    val smbFile = file.smbFile
                    smbFile.mkdirs()
                } catch (e: SmbException) {
                    log.warn("failed to make directory in smb", e)
                    isSuccessful = false
                }

            OpenMode.OTG -> {
                val documentFile = OTGUtil.getDocumentFile(file.getPath(), context, true)
                isSuccessful = documentFile != null
            }

            OpenMode.FILE -> isSuccessful = mkdir(File(file.getPath()), context)
            // With ANDROID_DATA will not accept create directory
            OpenMode.ANDROID_DATA -> isSuccessful = false
            else -> isSuccessful = true
        }
        return isSuccessful
    }
}
