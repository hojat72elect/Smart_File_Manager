package com.amaze.filemanager.asynchronous.asynctasks.compress

import com.amaze.filemanager.adapters.data.CompressedObjectParcelable
import com.amaze.filemanager.fileoperations.filesystem.compressed.ArchivePasswordCache
import com.amaze.filemanager.fileoperations.sevenz.SevenZFile
import com.amaze.filemanager.filesystem.compressed.CompressedHelper
import com.amaze.filemanager.filesystem.compressed.CompressedHelper.SEPARATOR
import org.apache.commons.compress.PasswordRequiredException
import org.apache.commons.compress.archivers.ArchiveException
import java.io.File
import java.io.IOException

class SevenZipHelperCallable(
    private val filePath: String,
    private val relativePath: String,
    goBack: Boolean,
) :
    CompressedHelperCallable(goBack) {
    @Throws(ArchiveException::class)
    @Suppress("Detekt.RethrowCaughtException")
    override fun addElements(elements: ArrayList<CompressedObjectParcelable>) {
        try {
            val sevenzFile =
                if (ArchivePasswordCache.getInstance().containsKey(filePath)) {
                    SevenZFile(
                        File(filePath),
                        ArchivePasswordCache.getInstance()[filePath]!!.toCharArray(),
                    )
                } else {
                    SevenZFile(File(filePath))
                }

            val entriesMap = sevenzFile.entries.associateBy { it.name }
            val entries = HashSet<String>()

            // Start filter out the paths we need to present based on relativePath

            entries.addAll(
                consolidate(
                    entriesMap.keys.filter {
                        it.startsWith(relativePath)
                    },
                    if (relativePath == "") {
                        0
                    } else if (relativePath.isNotBlank() && !relativePath.contains(SEPARATOR)) {
                        1
                    } else {
                        relativePath.count { it == CompressedHelper.SEPARATOR_CHAR } + 1
                    },
                ),
            )

            entries.forEach { path ->
                if (entriesMap.containsKey(path)) {
                    entriesMap[path]?.let { entry ->
                        elements.add(
                            CompressedObjectParcelable(
                                entry.name,
                                try {
                                    entry.lastModifiedDate.time
                                } catch (e: UnsupportedOperationException) {
                                    logger.warn("Unable to get modified date for 7zip file", e)
                                    0L
                                },
                                entry.size,
                                entry.isDirectory,
                            ),
                        )
                    }
                } else {
                    elements.add(
                        CompressedObjectParcelable(
                            path,
                            0L,
                            0,
                            true,
                        ),
                    )
                }
            }
        } catch (e: PasswordRequiredException) {
            // this is so that the caller can use onError to ask the user for the password
            throw e
        } catch (e: IOException) {
            throw ArchiveException(String.format("7zip archive %s is corrupt", filePath))
        }
    }

    private fun consolidate(
        paths: Collection<String>,
        level: Int = 0,
    ): Set<String> {
        return paths.mapNotNull { path ->
            when (level) {
                0 -> {
                    if (path.contains(SEPARATOR)) {
                        path.substringBefore(SEPARATOR)
                    } else {
                        path
                    }
                }

                else -> {
                    if (path.contains(SEPARATOR)) {
                        path.split(SEPARATOR).let {
                            if (it.size > level) {
                                it.subList(0, level + 1).joinToString(SEPARATOR)
                            } else {
                                null
                            }
                        }
                    } else {
                        null
                    }
                }
            }
        }.toSet()
    }
}
