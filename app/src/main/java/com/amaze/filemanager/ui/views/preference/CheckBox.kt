package com.amaze.filemanager.ui.views.preference

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Switch
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference

class CheckBox(context: Context, attrs: AttributeSet) : SwitchPreference(context, attrs) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        clearListenerInViewGroup(holder.itemView as ViewGroup)
        super.onBindViewHolder(holder)
    }

    /**
     * Clear listener in Switch for specify ViewGroup.
     *
     * @param viewGroup The ViewGroup that will need to clear the listener.
     */
    private fun clearListenerInViewGroup(viewGroup: ViewGroup) {
        for (n in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(n)
            if (childView is Switch) {
                childView.setOnCheckedChangeListener(null)
                return
            } else if (childView is ViewGroup) {
                clearListenerInViewGroup(childView)
            }
        }
    }
}
