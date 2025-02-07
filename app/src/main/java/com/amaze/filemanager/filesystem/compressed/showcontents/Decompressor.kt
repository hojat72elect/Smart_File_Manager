package com.amaze.filemanager.filesystem.compressed.showcontents

import android.content.Context
import android.content.Intent
import com.amaze.filemanager.asynchronous.asynctasks.compress.CompressedHelperCallable
import com.amaze.filemanager.asynchronous.management.ServiceWatcherUtil
import com.amaze.filemanager.asynchronous.services.ExtractService

abstract class Decompressor(protected var context: Context) {
    lateinit var filePath: String

    /**
     * Separator must be "/"
     *
     * @param path end with "/" if it is a directory, does not if it's a file
     */
    abstract fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ): CompressedHelperCallable

    /** Decompress a file somewhere  */
    fun decompress(whereToDecompress: String) {
        val intent =
            Intent(context, ExtractService::class.java).also {
                it.putExtra(ExtractService.KEY_PATH_ZIP, filePath)
                it.putExtra(ExtractService.KEY_ENTRIES_ZIP, arrayOfNulls<String>(0))
                it.putExtra(ExtractService.KEY_PATH_EXTRACT, whereToDecompress)
            }
        ServiceWatcherUtil.runService(context, intent)
    }

    /**
     * Decompress files or dirs inside the compressed file.
     *
     * @param subDirectories separator is "/", ended with "/" if it is a directory, does not if it's a
     * file
     */
    fun decompress(
        whereToDecompress: String,
        subDirectories: Array<String?>,
    ) {
        subDirectories.filterNotNull().map {
            realRelativeDirectory(it)
        }.run {
            val intent =
                Intent(context, ExtractService::class.java).also {
                    it.putExtra(ExtractService.KEY_PATH_ZIP, filePath)
                    it.putExtra(ExtractService.KEY_ENTRIES_ZIP, subDirectories)
                    it.putExtra(ExtractService.KEY_PATH_EXTRACT, whereToDecompress)
                }
            ServiceWatcherUtil.runService(context, intent)
        }
    }

    /** Get the real relative directory path (useful if you converted the separator or something)  */
    protected open fun realRelativeDirectory(dir: String): String {
        return dir
    }
}
