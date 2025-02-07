package com.amaze.filemanager.filesystem.compressed.extractcontents.helpers

import android.content.Context
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import org.apache.commons.compress.compressors.CompressorInputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream

class LzmaExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) : AbstractCommonsCompressedFileExtractor(
    context,
    filePath,
    outputPath,
    listener,
    updatePosition,
) {
    override fun getCompressorInputStreamClass(): Class<out CompressorInputStream> {
        return LZMACompressorInputStream::class.java
    }
}
