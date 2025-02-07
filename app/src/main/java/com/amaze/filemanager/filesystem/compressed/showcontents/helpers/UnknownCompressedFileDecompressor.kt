package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.UnknownCompressedFileHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

/**
 * Used by files compressed with gzip, bz2, lzma and xz.
 */
class UnknownCompressedFileDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = UnknownCompressedFileHelperCallable(filePath, addGoBackItem)
}
