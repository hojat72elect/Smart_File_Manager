package com.amaze.filemanager.filesystem.compressed.showcontents.helpers

import android.content.Context
import com.amaze.filemanager.asynchronous.asynctasks.compress.TarHelperCallable
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor

class TarDecompressor(context: Context) : Decompressor(context) {
    override fun changePath(
        path: String,
        addGoBackItem: Boolean,
    ) = TarHelperCallable(context, filePath, path, addGoBackItem)
}
