package com.amaze.filemanager.ui.views.drawer

import android.view.MenuItem
import androidx.lifecycle.ViewModel

class DrawerViewModel : ViewModel() {
    private val menuMetadataMap = HashMap<HasherOfMenuItem, MenuMetadata>()

    fun getDrawerMetadata(item: MenuItem): MenuMetadata {
        return requireNotNull(menuMetadataMap[item.toNonLeaking()])
    }

    /**
     * Put drawer meta data
     * @param item menu item
     * @param metadata menu meta data
     */
    fun putDrawerMetadata(
        item: MenuItem,
        metadata: MenuMetadata,
    ) {
        menuMetadataMap[item.toNonLeaking()] = metadata
    }
}
