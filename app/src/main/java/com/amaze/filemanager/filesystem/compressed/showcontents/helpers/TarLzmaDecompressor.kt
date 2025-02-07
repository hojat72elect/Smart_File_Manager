package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.TarLzmaHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class TarLzmaDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = TarLzmaHelperCallable(context, filePath, path, addGoBackItem)
}
