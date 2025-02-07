package com.amaze.filemanager.filesystem

import android.content.Context
import android.util.Log
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.MakeDirectoryOperation.mkdir
import com.amaze.filemanager.filesystem.root.RenameFileCommand.renameFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.channels.FileChannel

object RenameOperation {
    private val LOG = "RenameOperation"

    /**
     * Copy a file. The target file may even be on external SD card for Kitkat.
     *
     * @param source The source file
     * @param target The target file
     * @return true if the copying was successful.
     */
    @JvmStatic
    private fun copyFile(
        source: File,
        target: File,
        context: Context,
    ): Boolean {
        var inStream: FileInputStream? = null
        var outStream: OutputStream? = null
        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null
        try {
            inStream = FileInputStream(source)
            // First try the normal way
            if (FileProperties.isWritable(target)) {
                // standard way
                outStream = FileOutputStream(target)
                inChannel = inStream.channel
                outChannel = outStream.channel
                inChannel.transferTo(0, inChannel.size(), outChannel)
            } else {
                // Storage Access Framework
                val targetDocument = ExternalSdCardOperation.getDocumentFile(target, false, context)
                targetDocument ?: throw IOException("Couldn't get DocumentFile")
                outStream = context.contentResolver.openOutputStream(targetDocument.uri)

                if (outStream != null) {
                    // Both for SAF and for Kitkat, write to output stream.
                    val buffer = ByteArray(16384) // MAGIC_NUMBER
                    var bytesRead: Int
                    while (inStream.read(buffer).also { bytesRead = it } != -1) {
                        outStream.write(buffer, 0, bytesRead)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(
                LOG,
                "Error when copying file from ${source.absolutePath} to ${target.absolutePath}",
                e,
            )
            return false
        } finally {
            try {
                inStream?.close()
            } catch (e: IOException) {
                // ignore exception
            }
            try {
                outStream?.close()
            } catch (e: IOException) {
                // ignore exception
            }
            try {
                inChannel?.close()
            } catch (e: IOException) {
                // ignore exception
            }
            try {
                outChannel?.close()
            } catch (e: IOException) {
                // ignore exception
            }
        }
        return true
    }

    @JvmStatic
    @Throws(ShellNotRunningException::class)
    private fun rename(
        f: File,
        name: String,
        root: Boolean,
    ): Boolean {
        val parentName = f.parent ?: return false
        val parentFile = f.parentFile ?: return false

        val newPath = "$parentName/$name"
        if (parentFile.canWrite()) {
            return f.renameTo(File(newPath))
        } else if (root) {
            renameFile(f.path, newPath)
            return true
        }
        return false
    }

    /**
     * Rename a folder. In case of extSdCard in Kitkat, the old folder stays in place, but files are
     * moved.
     *
     * @param source The source folder.
     * @param target The target folder.
     * @return true if the renaming was successful.
     */
    @JvmStatic
    @Throws(ShellNotRunningException::class)
    fun renameFolder(
        source: File,
        target: File,
        context: Context,
    ): Boolean {
        // First try the normal rename.
        if (rename(source, target.name, false)) {
            return true
        }
        if (target.exists()) {
            return false
        }

        // Try the Storage Access Framework if it is just a rename within the same parent folder.
        if (source.parent == target.parent && ExternalSdCardOperation.isOnExtSdCard(source, context)) {
            val document = ExternalSdCardOperation.getDocumentFile(source, true, context)
            document ?: return false
            if (document.renameTo(target.name)) {
                return true
            }
        }

        // Try the manual way, moving files individually.
        if (!mkdir(target, context)) {
            return false
        }
        val sourceFiles = source.listFiles() as Array<File>?
        sourceFiles ?: return true
        for (sourceFile in sourceFiles) {
            val fileName = sourceFile.name
            val targetFile = File(target, fileName)
            if (!copyFile(sourceFile, targetFile, context)) {
                // stop on first error
                return false
            }
        }
        // Only after successfully copying all files, delete files on source folder.
        for (sourceFile in sourceFiles) {
            if (!DeleteOperation.deleteFile(sourceFile, context)) {
                // stop on first error
                return false
            }
        }
        return true
    }
}
