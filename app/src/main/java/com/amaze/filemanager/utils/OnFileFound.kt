package com.amaze.filemanager.utils

import com.amaze.filemanager.filesystem.HybridFileParcelable

/**
 * This allows the caller of a function to know when a file has been found and deal with it ASAP.
 */
fun interface OnFileFound {
    @Suppress("UndocumentedPublicFunction")
    fun onFileFound(file: HybridFileParcelable)
}
