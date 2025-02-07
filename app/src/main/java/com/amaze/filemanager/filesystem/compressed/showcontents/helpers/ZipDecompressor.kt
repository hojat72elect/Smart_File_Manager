package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.ZipHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class ZipDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = ZipHelperCallable(context, filePath, path, addGoBackItem)
}
