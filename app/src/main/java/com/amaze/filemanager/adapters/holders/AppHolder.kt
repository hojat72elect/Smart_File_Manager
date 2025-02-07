package com.amaze.filemanager.adapters.holders

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.views.ThemedTextView
import com.amaze.filemanager.utils.Utils

class AppHolder(view: View) : RecyclerView.ViewHolder(view) {
    @JvmField
    val apkIcon: AppCompatImageView = view.findViewById(R.id.apk_icon)

    @JvmField
    val txtTitle: ThemedTextView = view.findViewById(R.id.firstline)

    @JvmField
    val rl: RelativeLayout = view.findViewById(R.id.second)

    @JvmField
    val txtDesc: AppCompatTextView = view.findViewById(R.id.date)

    @JvmField
    val about: AppCompatImageButton = view.findViewById(R.id.properties)

    @JvmField
    val summary: RelativeLayout = view.findViewById(R.id.summary)

    @JvmField
    val packageName: AppCompatTextView = view.findViewById(R.id.appManagerPackageName)

    init {
        apkIcon.visibility = View.VISIBLE
        packageName.visibility = View.VISIBLE

        val layoutParams = txtDesc.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(
            txtDesc.marginLeft,
            txtDesc.marginTop,
            Utils.dpToPx(view.context, 4),
            txtDesc.marginBottom,
        )
        txtDesc.layoutParams = layoutParams

        view.findViewById<AppCompatImageView>(R.id.picture_icon).visibility = View.GONE
        view.findViewById<AppCompatImageView>(R.id.generic_icon).visibility = View.GONE
    }
}
