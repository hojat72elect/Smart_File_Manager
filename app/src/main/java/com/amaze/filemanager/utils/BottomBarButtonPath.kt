package com.amaze.filemanager.utils

import androidx.annotation.DrawableRes

/**
 * This lets BottomBar be independent of the Fragment MainActivity is housing
 */
interface BottomBarButtonPath {
    /**
     * This allows the fragment to change the path represented in the BottomBar directly
     */
    fun changePath(path: String)

    val path: String?

    @get:DrawableRes
    val rootDrawable: Int
}
