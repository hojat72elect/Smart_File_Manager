package com.amaze.filemanager.adapters.holders

import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.views.ThemedTextView

/**
 * Check RecyclerAdapter's doc. TODO load everything related to this item here instead of in
 * RecyclerAdapter.
 */
class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // each data item is just a string in this case
    @JvmField
    val pictureIcon: AppCompatImageView? = view.findViewById(R.id.picture_icon)

    @JvmField
    val genericIcon: AppCompatImageView = view.findViewById(R.id.generic_icon)

    @JvmField
    val apkIcon: AppCompatImageView? = view.findViewById(R.id.apk_icon)

    @JvmField
    val imageView1: AppCompatImageView? = view.findViewById(R.id.icon_thumb)

    @JvmField
    val txtTitle: ThemedTextView = view.findViewById(R.id.firstline)

    @JvmField
    val txtDesc: AppCompatTextView = view.findViewById(R.id.secondLine)

    @JvmField
    val date: AppCompatTextView = view.findViewById(R.id.date)

    @JvmField
    val perm: AppCompatTextView = view.findViewById(R.id.permis)

    @JvmField
    val baseItemView: View = view.findViewById(R.id.second)

    @JvmField
    val genericText: AppCompatTextView? = view.findViewById(R.id.generictext)

    @JvmField
    val about: AppCompatImageButton = view.findViewById(R.id.properties)

    @JvmField
    val checkImageView: AppCompatImageView? = view.findViewById(R.id.check_icon)

    @JvmField
    val checkImageViewGrid: AppCompatImageView? = view.findViewById(R.id.check_icon_grid)

    @JvmField
    val iconLayout: RelativeLayout? = view.findViewById(R.id.icon_frame_grid)

    @JvmField
    val dummyView: View? = view.findViewById(R.id.dummy_view)
}
