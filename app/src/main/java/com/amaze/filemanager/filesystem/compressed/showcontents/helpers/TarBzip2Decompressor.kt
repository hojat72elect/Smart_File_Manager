package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.TarBzip2HelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class TarBzip2Decompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = TarBzip2HelperCallable(context, filePath, path, addGoBackItem)
}
