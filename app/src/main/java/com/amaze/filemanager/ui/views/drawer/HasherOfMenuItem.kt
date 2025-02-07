package com.amaze.filemanager.ui.views.drawer

import android.view.MenuItem

/**
 * This is a sort of wrapper, only used to provide a hash for MenuItems
 */
data class HasherOfMenuItem(
    val groupId: Int,
    val itemId: Int,
    val title: CharSequence?,
    val ordering: Int,
)

fun MenuItem.toNonLeaking(): HasherOfMenuItem {
    return HasherOfMenuItem(groupId, itemId, title, order)
}
