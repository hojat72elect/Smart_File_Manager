package com.amaze.filemanager.asynchronous.asynctasks.compress

import android.content.Context
import org.apache.commons.compress.compressors.CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

class TarBzip2HelperCallable(
    context: Context,
    filePath: String,
    relativePath: String,
    goBack: Boolean,
) :
    AbstractCompressedTarArchiveHelperCallable(context, filePath, relativePath, goBack) {
    override fun getCompressorInputStreamClass(): Class<out CompressorInputStream> =
        BZip2CompressorInputStream::class.java
}
