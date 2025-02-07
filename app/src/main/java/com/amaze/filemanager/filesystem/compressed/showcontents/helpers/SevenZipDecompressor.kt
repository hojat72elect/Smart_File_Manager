package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.SevenZipHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class SevenZipDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = SevenZipHelperCallable(filePath, path, addGoBackItem)
}
