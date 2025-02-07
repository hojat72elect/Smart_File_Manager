package com.amaze.filemanager.asynchronous.asynctasks.compress

import android.content.Context
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.InputStream

class TarHelperCallable(
    context: Context,
    filePath: String,
    relativePath: String,
    goBack: Boolean,
) :
    AbstractCommonsArchiveHelperCallable(context, filePath, relativePath, goBack) {
    override fun createFrom(inputStream: InputStream): ArchiveInputStream =
        TarArchiveInputStream(inputStream)
}
