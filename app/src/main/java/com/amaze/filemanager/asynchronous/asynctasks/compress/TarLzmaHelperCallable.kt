package com.amaze.filemanager.asynchronous.asynctasks.compress

import android.content.Context
import org.apache.commons.compress.compressors.CompressorInputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream

class TarLzmaHelperCallable(
    context: Context,
    filePath: String,
    relativePath: String,
    goBack: Boolean,
) :
    AbstractCompressedTarArchiveHelperCallable(context, filePath, relativePath, goBack) {
    override fun getCompressorInputStreamClass(): Class<out CompressorInputStream> =
        LZMACompressorInputStream::class.java
}
