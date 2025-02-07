package com.amaze.filemanager.ui.views

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomScrollLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int,
    ) {
        val linearSmoothScroller = CustomLinearSmoothScroller(recyclerView!!.context, true)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}
