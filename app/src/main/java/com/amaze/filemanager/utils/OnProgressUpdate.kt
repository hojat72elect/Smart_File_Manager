package com.amaze.filemanager.utils

/**
 * General interface for updating data before it's finished loading.
 */
interface OnProgressUpdate<T> {
    @Suppress("UndocumentedPublicFunction")
    fun onUpdate(data: T)
}
