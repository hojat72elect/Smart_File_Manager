package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.TarGzHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class TarGzDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = TarGzHelperCallable(context, filePath, path, addGoBackItem)
}
