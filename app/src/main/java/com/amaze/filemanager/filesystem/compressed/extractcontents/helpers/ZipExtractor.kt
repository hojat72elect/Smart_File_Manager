package com.amaze.filemanager.filesystem.compressed.extractcontents.helpers

import android.content.Context
import android.os.Build
import com.amaze.filemanager.R
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.fileoperations.filesystem.compressed.ArchivePasswordCache
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import com.amaze.filemanager.filesystem.FileUtil
import com.amaze.filemanager.filesystem.MakeDirectoryOperation
import com.amaze.filemanager.filesystem.compressed.CompressedHelper
import com.amaze.filemanager.filesystem.compressed.extractcontents.Extractor
import com.amaze.filemanager.filesystem.files.GenericCopyUtil
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.FileHeader
import org.apache.commons.compress.PasswordRequiredException
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException

class ZipExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) : Extractor(context, filePath, outputPath, listener, updatePosition) {
    private val isRobolectricTest = Build.HARDWARE == "robolectric"

    @Throws(IOException::class)
    override fun extractWithFilter(filter: Filter) {
        var totalBytes: Long = 0
        val entriesToExtract: MutableList<FileHeader> = ArrayList()
        try {
            val zipfile = ZipFile(filePath)
            if (ArchivePasswordCache.getInstance().containsKey(filePath)) {
                zipfile.setPassword(ArchivePasswordCache.getInstance()[filePath]!!.toCharArray())
            }

            // iterating archive elements to find file names that are to be extracted
            zipfile.fileHeaders.forEach { obj ->
                val fileHeader = obj as FileHeader
                if (CompressedHelper.isEntryPathValid(fileHeader.fileName)) {
                    if (filter.shouldExtract(fileHeader.fileName, fileHeader.isDirectory)) {
                        entriesToExtract.add(fileHeader)
                        totalBytes += fileHeader.uncompressedSize
                    }
                } else {
                    invalidArchiveEntries.add(fileHeader.fileName)
                }
            }
            if (entriesToExtract.size > 0) {
                listener.onStart(totalBytes, entriesToExtract[0].fileName)
                for (entry in entriesToExtract) {
                    if (!listener.isCancelled) {
                        listener.onUpdate(entry.fileName)
                        extractEntry(context, zipfile, entry, outputPath)
                    }
                }
            } else {
                throw EmptyArchiveNotice()
            }
            listener.onFinish()
        } catch (e: ZipException) {
            if (true == e.message?.lowercase()?.contains("password")) {
                // Hack.
                // zip4j uses ZipException for all problems, so we need to distinguish password
                // related problems and throw PasswordRequiredException here
                throw PasswordRequiredException(e.message)
            } else {
                throw BadArchiveNotice(e)
            }
        }
    }

    /**
     * Method extracts [FileHeader] from [ZipFile]
     *
     * @param zipFile zip file from which entriesToExtract are to be extracted
     * @param entry zip entry that is to be extracted
     * @param outputDir output directory
     */
    @Throws(IOException::class)
    private fun extractEntry(
        context: Context,
        zipFile: ZipFile,
        entry: FileHeader,
        outputDir: String,
    ) {
        val outputFile = File(outputDir, fixEntryName(entry.fileName))
        if (!outputFile.canonicalPath.startsWith(outputDir) &&
            (isRobolectricTest && !outputFile.canonicalPath.startsWith("/private$outputDir"))
        ) {
            throw IOException("Incorrect ZipEntry path!")
        }
        if (entry.isDirectory) {
            // zip entry is a directory, return after creating new directory
            MakeDirectoryOperation.mkdir(outputFile, context)
            return
        }
        if (outputFile.parentFile!!.exists().not()) {
            // creating directory if not already exists
            MakeDirectoryOperation.mkdir(outputFile.parentFile, context)
        }
        BufferedInputStream(zipFile.getInputStream(entry)).use { inputStream ->
            FileUtil.getOutputStream(outputFile, context)?.let { fileOutputStream ->
                BufferedOutputStream(fileOutputStream).run {
                    var len: Int
                    val buf = ByteArray(GenericCopyUtil.DEFAULT_BUFFER_SIZE)
                    while (inputStream.read(buf).also { len = it } != -1) {
                        if (!listener.isCancelled) {
                            write(buf, 0, len)
                            updatePosition.updatePosition(len.toLong())
                        } else {
                            break
                        }
                    }
                    close()
                    outputFile.setLastModified(entry.lastModifiedTimeEpoch)
                }
            } ?: AmazeFileManagerApplication.toast(
                context,
                context.getString(
                    R.string.error_archive_cannot_extract,
                    entry.fileName,
                    outputDir,
                ),
            )
        }
    }
}
