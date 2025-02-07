package com.amaze.filemanager.filesystem.compressed.extractcontents.helpers

import android.content.Context
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import org.apache.commons.compress.compressors.CompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream

class TarXzExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) : AbstractCompressedTarArchiveExtractor(
    context,
    filePath,
    outputPath,
    listener,
    updatePosition,
) {
    override fun getCompressorInputStreamClass(): Class<out CompressorInputStream> =
        XZCompressorInputStream::class.java
}
