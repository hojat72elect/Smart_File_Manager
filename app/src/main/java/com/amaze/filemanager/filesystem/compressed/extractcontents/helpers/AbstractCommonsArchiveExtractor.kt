package com.amaze.filemanager.filesystem.compressed.extractcontents.helpers

import android.content.Context
import com.amaze.filemanager.R
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import com.amaze.filemanager.filesystem.FileUtil
import com.amaze.filemanager.filesystem.MakeDirectoryOperation
import com.amaze.filemanager.filesystem.compressed.extractcontents.Extractor
import com.amaze.filemanager.filesystem.files.GenericCopyUtil
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

abstract class AbstractCommonsArchiveExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) : Extractor(context, filePath, outputPath, listener, updatePosition) {
    /**
     * Subclasses implement this method to create [ArchiveInputStream] instances with given archive
     * as [InputStream].
     *
     * @param inputStream archive as [InputStream]
     */
    abstract fun createFrom(inputStream: InputStream): ArchiveInputStream

    @Throws(IOException::class)
    @Suppress("EmptyWhileBlock")
    override fun extractWithFilter(filter: Filter) {
        var totalBytes: Long = 0
        val archiveEntries = ArrayList<ArchiveEntry>()
        var inputStream = createFrom(FileInputStream(filePath))
        var archiveEntry: ArchiveEntry?
        try {
            while (inputStream.nextEntry.also { archiveEntry = it } != null) {
                archiveEntry?.run {
                    if (filter.shouldExtract(name, isDirectory)) {
                        archiveEntries.add(this)
                        totalBytes += size
                    }
                }
            }
            if (archiveEntries.size > 0) {
                listener.onStart(totalBytes, archiveEntries[0].name)
                inputStream.close()
                inputStream = createFrom(FileInputStream(filePath))
                archiveEntries.forEach { entry ->
                    if (!listener.isCancelled) {
                        listener.onUpdate(entry.name)
                        // TAR is sequential, you need to walk all the way to the file you want
                        while (entry.hashCode() != inputStream.nextEntry.hashCode()) {
                        }
                        extractEntry(context, inputStream, entry, outputPath)
                    }
                }
                inputStream.close()
                listener.onFinish()
            } else {
                throw EmptyArchiveNotice()
            }
        } finally {
            inputStream.close()
        }
    }

    @Throws(IOException::class)
    protected fun extractEntry(
        context: Context,
        inputStream: ArchiveInputStream,
        entry: ArchiveEntry,
        outputDir: String,
    ) {
        if (entry.isDirectory) {
            MakeDirectoryOperation.mkdir(File(outputDir, entry.name), context)
            return
        }
        val outputFile = File(outputDir, entry.name)
        if (false == outputFile.parentFile?.exists()) {
            MakeDirectoryOperation.mkdir(outputFile.parentFile, context)
        }
        FileUtil.getOutputStream(outputFile, context)?.let { fileOutputStream ->
            BufferedOutputStream(fileOutputStream).run {
                var len: Int
                val buf = ByteArray(GenericCopyUtil.DEFAULT_BUFFER_SIZE)
                while (inputStream.read(buf).also { len = it } != -1) {
                    write(buf, 0, len)
                    updatePosition.updatePosition(len.toLong())
                }
                close()
                outputFile.setLastModified(entry.lastModifiedDate.time)
            }
        } ?: AmazeFileManagerApplication.toast(
            context,
            context.getString(
                R.string.error_archive_cannot_extract,
                entry.name,
                outputDir,
            ),
        )
    }
}
