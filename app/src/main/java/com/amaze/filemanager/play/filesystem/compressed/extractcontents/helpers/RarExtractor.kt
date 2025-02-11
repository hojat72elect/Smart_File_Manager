package com.amaze.filemanager.play.filesystem.compressed.extractcontents.helpers

import android.content.Context
import android.os.Build
import com.amaze.filemanager.asynchronous.management.ServiceWatcherUtil
import com.amaze.filemanager.fileoperations.filesystem.compressed.ArchivePasswordCache
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import com.amaze.filemanager.filesystem.FileUtil
import com.amaze.filemanager.filesystem.MakeDirectoryOperation
import com.amaze.filemanager.filesystem.compressed.CompressedHelper
import com.amaze.filemanager.filesystem.compressed.extractcontents.Extractor
import com.amaze.filemanager.filesystem.files.GenericCopyUtil
import com.github.junrar.Archive
import com.github.junrar.exception.CorruptHeaderException
import com.github.junrar.exception.MainHeaderNullException
import com.github.junrar.exception.RarException
import com.github.junrar.exception.UnsupportedRarV5Exception
import com.github.junrar.rarfile.FileHeader
import org.apache.commons.compress.PasswordRequiredException
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.CRC32
import java.util.zip.CheckedOutputStream

class RarExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) : Extractor(context, filePath, outputPath, listener, updatePosition) {
    private val isRobolectricTest = Build.HARDWARE == "robolectric"

    @Throws(IOException::class)
    override fun extractWithFilter(filter: Filter) {
        try {
            var totalBytes: Long = 0
            val rarFile =
                runCatching {
                    ArchivePasswordCache.getInstance()[filePath]?.let {
                        Archive(File(filePath), it).also { archive ->
                            archive.password = it
                        }
                    } ?: Archive(File(filePath))
                }.onFailure {
                    when {
                        // Hack. CorruptHeaderException will throw if archive is really corrupt or
                        // password-protected RAR with wrong password, hence have to distinguish two
                        // situations
                        (
                                !ArchivePasswordCache.getInstance().containsKey(filePath) &&
                                        CorruptHeaderException::class.java.isAssignableFrom(it::class.java)
                                ) or
                                MainHeaderNullException::class.java.isAssignableFrom(it::class.java) -> {
                            throw BadArchiveNotice(it)
                        }

                        UnsupportedRarV5Exception::class.java.isAssignableFrom(it::class.java) -> {
                            throw it
                        }

                        else -> {
                            throw PasswordRequiredException(filePath)
                        }
                    }
                }.getOrNull()!!

            if (rarFile.isPasswordProtected || rarFile.isEncrypted) {
                if (ArchivePasswordCache.getInstance().containsKey(filePath)) {
                    runCatching {
                        tryExtractSmallestFileInArchive(context, rarFile)
                    }.onFailure {
                        throw PasswordRequiredException(filePath)
                    }.onSuccess {
                        File(it).delete()
                    }
                } else {
                    throw PasswordRequiredException(filePath)
                }
            }

            val fileHeaders: List<FileHeader>
            // iterating archive elements to find file names that are to be extracted
            rarFile.fileHeaders.partition { header ->
                CompressedHelper.isEntryPathValid(header.fileName)
            }.apply {
                fileHeaders = first
                totalBytes = first.sumOf { it.fullUnpackSize }
                invalidArchiveEntries = second.map { it.fileName }
            }

            if (fileHeaders.isNotEmpty()) {
                listener.onStart(totalBytes, fileHeaders[0].fileName)
                fileHeaders.forEach { entry ->
                    if (!listener.isCancelled) {
                        listener.onUpdate(entry.fileName)
                        extractEntry(context, rarFile, entry, outputPath)
                    }
                }
                listener.onFinish()
            } else {
                throw EmptyArchiveNotice()
            }
        } catch (e: MainHeaderNullException) {
            throw BadArchiveNotice(e)
        } catch (e: RarException) {
            throw IOException(e)
        }
    }

    @Throws(IOException::class)
    private fun extractEntry(
        context: Context,
        rarFile: Archive,
        entry: FileHeader,
        outputDir: String,
    ) {
        var _entry = entry
        val entrySpawnsVolumes = entry.isSplitAfter
        val name =
            fixEntryName(entry.fileName).replace(
                "\\\\".toRegex(),
                CompressedHelper.SEPARATOR,
            )
        val outputFile = File(outputDir, name)
        if (!outputFile.canonicalPath.startsWith(outputDir) &&
            (isRobolectricTest && !outputFile.canonicalPath.startsWith("/private$outputDir"))
        ) {
            throw IOException("Incorrect RAR FileHeader path!")
        }
        if (entry.isDirectory) {
            MakeDirectoryOperation.mkdir(outputFile, context)
            outputFile.setLastModified(entry.mTime.time)
            return
        }
        if (!outputFile.parentFile.exists()) {
            MakeDirectoryOperation.mkdir(outputFile.parentFile, context)
            outputFile.parentFile.setLastModified(entry.mTime.time)
        }
        /* junrar doesn't throw exceptions if wrong archive password is supplied, until extracted file
           CRC is compared against the one stored in archive. So we can only rely on verifying CRC
           during extracting
         */
        val inputStream = BufferedInputStream(rarFile.getInputStream(entry))
        val outputStream =
            CheckedOutputStream(
                BufferedOutputStream(FileUtil.getOutputStream(outputFile, context)),
                CRC32(),
            )
        try {
            var len: Int
            val buf = ByteArray(GenericCopyUtil.DEFAULT_BUFFER_SIZE)
            while (inputStream.read(buf).also { len = it } != -1) {
                if (!listener.isCancelled) {
                    outputStream.write(buf, 0, len)
                    ServiceWatcherUtil.position += len.toLong()
                } else {
                    break
                }
            }
            /* In multi-volume archives, FileHeader may have changed as the other parts of the
               archive is processed. Need to lookup the FileHeader in the volume the archive
               currently resides on again, as the correct CRC of the extract file will be there
               instead.
             */
            if (entrySpawnsVolumes) {
                _entry = rarFile.fileHeaders.find { it.fileName.equals(entry.fileName) }!!
            }

            /* junrar does not provide convenient way to verify archive password is correct, we
               can only rely on post-extract file checksum matching to see if the file is
               extracted correctly = correct password.

               RAR header stores checksum in signed 2's complement (as hex though). Some bitwise
               ops needed to compare with CheckOutputStream used above, which always produces
               checksum in unsigned long
             */
            if (_entry.fileCRC.toLong() and 0xffffffffL != outputStream.checksum.value) {
                throw IOException("Checksum verification failed for entry $name")
            }
        } finally {
            outputStream.close()
            inputStream.close()
            outputFile.setLastModified(entry.mTime.time)
        }
    }

    private fun tryExtractSmallestFileInArchive(
        context: Context,
        archive: Archive,
    ): String {
        archive.fileHeaders ?: throw IOException(CorruptHeaderException())
        with(
            archive.fileHeaders.filter {
                !it.isDirectory
            },
        ) {
            if (isEmpty()) {
                throw IOException(CorruptHeaderException())
            } else {
                associateBy({ it.fileName }, { it.fullUnpackSize })
                    .minByOrNull {
                        it.value
                    }!!.run {
                        val header =
                            archive.fileHeaders.find {
                                it.fileName.equals(this.key)
                            }!!
                        val filename =
                            fixEntryName(header.fileName).replace(
                                "\\\\".toRegex(),
                                CompressedHelper.SEPARATOR,
                            )
                        extractEntry(
                            context,
                            archive,
                            header,
                            context.externalCacheDir!!.absolutePath
                        )
                        return "${context.externalCacheDir!!.absolutePath}/$filename"
                    }
            }
        }
    }
}
