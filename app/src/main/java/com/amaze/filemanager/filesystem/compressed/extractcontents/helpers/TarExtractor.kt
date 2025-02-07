package com.amaze.filemanager.filesystem.compressed.extractcontents.helpers

import android.content.Context
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.InputStream

class TarExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) : AbstractCommonsArchiveExtractor(
    context,
    filePath,
    outputPath,
    listener,
    updatePosition,
) {
    override fun createFrom(inputStream: InputStream): TarArchiveInputStream =
        runCatching {
            TarArchiveInputStream(inputStream)
        }.getOrElse {
            throw BadArchiveNotice(it)
        }
}
