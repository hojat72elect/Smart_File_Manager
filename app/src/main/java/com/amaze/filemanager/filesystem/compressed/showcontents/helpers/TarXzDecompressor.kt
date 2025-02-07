package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.TarXzHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class TarXzDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = TarXzHelperCallable(context, filePath, path, addGoBackItem)
}
