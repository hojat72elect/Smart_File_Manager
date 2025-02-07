package com.amaze.filemanager.filesystem.files

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import com.amaze.filemanager.filesystem.HybridFile
import org.slf4j.LoggerFactory

object MediaConnectionUtils {
    private val LOG = LoggerFactory.getLogger(MediaConnectionUtils::class.java)

    /**
     * Invokes MediaScannerConnection#scanFile for the given files
     *
     * @param context the context
     * @param hybridFiles files to be scanned
     */
    @JvmStatic
    fun scanFile(
        context: Context,
        hybridFiles: Array<HybridFile>,
    ) {
        val paths = arrayOfNulls<String>(hybridFiles.size)

        for (i in hybridFiles.indices) paths[i] = hybridFiles[i].path

        MediaScannerConnection.scanFile(
            context,
            paths,
            null,
        ) { path: String, _: Uri? ->
            LOG.info("MediaConnectionUtils#scanFile finished scanning path$path")
        }
    }
}
