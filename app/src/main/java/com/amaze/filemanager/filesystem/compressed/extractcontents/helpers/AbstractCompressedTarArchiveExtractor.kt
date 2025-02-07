package com.amaze.filemanager.filesystem.compressed.extractcontents.helpers

import android.content.Context
import com.amaze.filemanager.fileoperations.utils.UpdatePosition
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.CompressorInputStream
import java.io.InputStream
import java.lang.reflect.Constructor

abstract class AbstractCompressedTarArchiveExtractor(
    context: Context,
    filePath: String,
    outputPath: String,
    listener: OnUpdate,
    updatePosition: UpdatePosition,
) :
    AbstractCommonsArchiveExtractor(context, filePath, outputPath, listener, updatePosition) {
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
            throw BadArchiveNotice(it)
        }
    }
}
