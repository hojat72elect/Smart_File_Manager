package com.amaze.filemanager.utils

interface OnAsyncTaskFinished<T> {
    @Suppress("UndocumentedPublicFunction")
    fun onAsyncTaskFinished(data: T)
}
