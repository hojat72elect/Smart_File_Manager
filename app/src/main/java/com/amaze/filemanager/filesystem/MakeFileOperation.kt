package com.amaze.filemanager.filesystem

import android.content.Context
import com.amaze.filemanager.ui.icons.MimeTypes
import com.amaze.filemanager.utils.AppConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

// This object is here to not polute the global namespace
// All functions must be static
object MakeFileOperation {
    private val log: Logger = LoggerFactory.getLogger(MakeFileOperation::class.java)

    /**
     * Get a temp file.
     *
     * @param file The base file for which to create a temp file.
     * @return The temp file.
     */
    @JvmStatic
    fun getTempFile(
        file: File,
        context: Context,
    ): File {
        val extDir = context.getExternalFilesDir(null)
        return File(extDir, file.name)
    }

    /**
     * Make normal file
     * @param file File
     * @param context Context
     * @return true for success and false for failed
     */
    @JvmStatic
    fun mkfile(
        file: File?,
        context: Context,
    ): Boolean {
        if (file == null) return false
        if (file.exists()) {
            // nothing to create.
            return !file.isDirectory
        }

        // Try the normal way
        try {
            if (file.createNewFile()) {
                return true
            }
        } catch (e: IOException) {
            log.warn("failed to make file", e)
        }

        // Try with Storage Access Framework.
        if (ExternalSdCardOperation.isOnExtSdCard(file, context)) {
            val document = ExternalSdCardOperation.getDocumentFile(file.parentFile, true, context)
            // getDocumentFile implicitly creates the directory.
            return try {
                (
                        document?.createFile(
                            MimeTypes.getMimeType(file.path, file.isDirectory),
                            file.name,
                        )
                                != null
                        )
            } catch (e: UnsupportedOperationException) {
                log.warn("Failed to create file on sd card using document file", e)
                false
            }
        }
        return false

    }

    /**
     * Make text file
     * @param data file data
     * @param path path
     * @param fileName file name
     * @return true for success and false for failed
     */
    @JvmStatic
    fun mktextfile(
        data: String?,
        path: String?,
        fileName: String,
    ): Boolean {
        val f =
            File(
                path,
                "$fileName${AppConstants.NEW_FILE_DELIMITER}${AppConstants.NEW_FILE_EXTENSION_TXT}",
            )
        var out: FileOutputStream? = null
        var outputWriter: OutputStreamWriter? = null
        return try {
            if (f.createNewFile()) {
                out = FileOutputStream(f, false)
                outputWriter = OutputStreamWriter(out)
                outputWriter.write(data)
                true
            } else {
                false
            }
        } catch (io: IOException) {
            log.warn("Error writing file contents", io)
            false
        } finally {
            try {
                if (outputWriter != null) {
                    outputWriter.flush()
                    outputWriter.close()
                }
                if (out != null) {
                    out.flush()
                    out.close()
                }
            } catch (e: IOException) {
                log.warn("Error closing file output stream", e)
            }
        }
    }
}
