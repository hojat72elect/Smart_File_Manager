package com.amaze.filemanager.play.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.filesystem.compressed.CompressedHelper
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor
import com.amaze.filemanager.play.asynchronous.asynctasks.compress.RarHelperCallable
import com.github.junrar.rarfile.FileHeader

class RarDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = RarHelperCallable(filePath, path, addGoBackItem)

    override fun realRelativeDirectory(dir: String): String {
        var dir = dir
        if (dir.endsWith(CompressedHelper.SEPARATOR)) {
            dir = dir.substring(0, dir.length - 1)
        }
        return dir.replace(CompressedHelper.SEPARATOR.toCharArray()[0], '\\')
    }

    companion object {
        /**
         * Helper method to convert RAR [FileHeader] entries containing backslashes back to slashes.
         *
         * @param file RAR entry as [FileHeader] object
         */
        @JvmStatic
        fun convertName(file: FileHeader): String {
            val name = file.fileName.replace('\\', '/')
            return if (file.isDirectory) {
                name + CompressedHelper.SEPARATOR
            } else {
                name
            }
        }
    }
}
