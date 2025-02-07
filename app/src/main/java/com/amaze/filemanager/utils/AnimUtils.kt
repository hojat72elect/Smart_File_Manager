package com.amaze.filemanager.utils

import android.content.Context
import android.os.Handler
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.views.ThemedTextView

/** Utility methods for working with animations.  */
object AnimUtils {
    private var fastOutSlowIn: Interpolator? = null

    @JvmStatic
    fun getFastOutSlowInInterpolator(context: Context?): Interpolator? {
        if (fastOutSlowIn == null) {
            fastOutSlowIn =
                AnimationUtils.loadInterpolator(context, R.interpolator.fast_out_slow_in)
        }
        return fastOutSlowIn
    }

    /**
     * Animates filenames textview to marquee after a delay. Make sure to set TextView.setSelected to false in order to stop the marquee later
     */
    @JvmStatic
    fun marqueeAfterDelay(
        delayInMillis: Int,
        marqueeView: ThemedTextView,
    ) {
        Handler()
            .postDelayed(
                {
                    // marquee works only when text view has focus
                    marqueeView.isSelected = true
                },
                delayInMillis.toLong(),
            )
    }
}
