package com.amaze.filemanager.asynchronous.asynctasks.compress

import android.content.Context
import com.amaze.filemanager.filesystem.compressed.extractcontents.Extractor
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.CompressorInputStream
import java.io.InputStream
import java.lang.reflect.Constructor

abstract class AbstractCompressedTarArchiveHelperCallable(
    context: Context,
    filePath: String,
    relativePath: String,
    goBack: Boolean,
) :
    AbstractCommonsArchiveHelperCallable(context, filePath, relativePath, goBack) {
    private val compressorInputStreamConstructor: Constructor<out CompressorInputStream>

    init {
        compressorInputStreamConstructor =
            getCompressorInputStreamClass()
                .getDeclaredConstructor(InputStream::class.java)
        compressorInputStreamConstructor.isAccessible = true
    }

    /**
     * Subclasses implement this method to specify the [CompressorInputStream] class to be used. It
     * will be used to create the backing inputstream beneath [TarArchiveInputStream] in
     * [createFrom].
     *
     * @return Class representing the implementation will be handling
     */
    abstract fun getCompressorInputStreamClass(): Class<out CompressorInputStream>

    override fun createFrom(inputStream: InputStream): TarArchiveInputStream {
        return runCatching {
            TarArchiveInputStream(compressorInputStreamConstructor.newInstance(inputStream))
        }.getOrElse {
            throw Extractor.BadArchiveNotice(it)
        }
    }
}
