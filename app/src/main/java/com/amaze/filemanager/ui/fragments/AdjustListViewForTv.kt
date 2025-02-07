package com.amaze.filemanager.ui.fragments

import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.ui.activities.MainActivity

interface AdjustListViewForTv<in T : RecyclerView.ViewHolder> {
    /**
     * Adjust list view focus scroll when using dpad.
     * Scroll few more elements up / down so that it's easier for user to see list
     */
    fun adjustListViewForTv(
        viewHolder: T,
        mainActivity: MainActivity,
    )
}
