package com.amaze.filemanager.asynchronous.asynctasks.compress

import android.content.Context
import com.amaze.filemanager.R
import com.amaze.filemanager.adapters.data.CompressedObjectParcelable
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.filesystem.compressed.CompressedHelper
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveException
import org.apache.commons.compress.archivers.ArchiveInputStream

abstract class AbstractCommonsArchiveHelperCallable(
    context: Context,
    private val filePath: String,
    private val relativePath: String,
    goBack: Boolean,
) : CompressedHelperCallable(goBack) {
    private val context: WeakReference<Context> = WeakReference(context)

    /**
     * Subclasses implement this method to create [ArchiveInputStream] instances with given archive
     * as [InputStream].
     *
     * @param inputStream archive as [InputStream]
     */
    abstract fun createFrom(inputStream: InputStream): ArchiveInputStream

    @Throws(ArchiveException::class)
    @Suppress("LabeledExpression")
    public override fun addElements(elements: ArrayList<CompressedObjectParcelable>) {
        try {
            createFrom(FileInputStream(filePath)).use { tarInputStream ->
                var entry: ArchiveEntry?
                while (tarInputStream.nextEntry.also { entry = it } != null) {
                    entry?.run {
                        var name = name
                        if (!CompressedHelper.isEntryPathValid(name)) {
                            AmazeFileManagerApplication.toast(
                                context.get(),
                                context.get()!!
                                    .getString(R.string.multiple_invalid_archive_entries),
                            )
                            return@run
                        }
                        if (name.endsWith(CompressedHelper.SEPARATOR)) {
                            name = name.substring(0, name.length - 1)
                        }
                        val isInBaseDir =
                            (relativePath == "" && !name.contains(CompressedHelper.SEPARATOR))
                        val isInRelativeDir = (
                                name.contains(CompressedHelper.SEPARATOR) &&
                                        name.substring(
                                            0,
                                            name.lastIndexOf(CompressedHelper.SEPARATOR)
                                        )
                                        == relativePath
                                )
                        if (isInBaseDir || isInRelativeDir) {
                            elements.add(
                                CompressedObjectParcelable(
                                    name,
                                    lastModifiedDate.time,
                                    size,
                                    isDirectory,
                                ),
                            )
                        }
                    }
                }
            }
        } catch (e: IOException) {
            throw ArchiveException(String.format("Tarball archive %s is corrupt", filePath), e)
        }
    }
}
