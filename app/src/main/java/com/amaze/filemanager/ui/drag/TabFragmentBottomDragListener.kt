package com.amaze.filemanager.ui.drag

import android.view.DragEvent
import android.view.View

class TabFragmentBottomDragListener(
    private val dragEnterCallBack: () -> Unit,
    private val dragExitCallBack: () -> Unit,
) : View.OnDragListener {
    override fun onDrag(
        p0: View?,
        p1: DragEvent?,
    ): Boolean {
        return when (p1?.action) {
            DragEvent.ACTION_DRAG_ENDED -> {
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                dragEnterCallBack()
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                dragExitCallBack()
                true
            }

            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }

            DragEvent.ACTION_DROP -> {
                true
            }

            else -> false
        }
    }
}
