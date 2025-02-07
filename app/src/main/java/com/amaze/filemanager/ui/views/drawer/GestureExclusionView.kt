package com.amaze.filemanager.ui.views.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class GestureExclusionView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val gestureExclusionRects = mutableListOf<Rect>()

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        super.onLayout(changed, left, top, right, bottom)

        if (changed) {
            updateGestureExclusion()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateGestureExclusion()
    }

    private fun updateGestureExclusion() {
        visibility = VISIBLE
        setBackgroundColor(resources.getColor(android.R.color.transparent))
        gestureExclusionRects.clear()
        val rect = Rect()
        this.getGlobalVisibleRect(rect)
        gestureExclusionRects += rect
        systemGestureExclusionRects = gestureExclusionRects
    }
}
