package com.amaze.filemanager.ui.views

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomScrollGridLayoutManager(context: Context?, spanCount: Int) :
    GridLayoutManager(context, spanCount) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int,
    ) {
        val linearSmoothScroller = CustomLinearSmoothScroller(recyclerView!!.context, false)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}
