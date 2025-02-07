package com.amaze.filemanager.adapters.holders

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.views.ThemedTextView

class CompressedItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // each data item is just a string in this case
    @JvmField
    val genericIcon: AppCompatImageView = view.findViewById(R.id.generic_icon)

    @JvmField
    val txtTitle: ThemedTextView = view.findViewById(R.id.firstline)

    @JvmField
    val txtDesc: AppCompatTextView = view.findViewById(R.id.secondLine)

    @JvmField
    val date: AppCompatTextView = view.findViewById(R.id.date)

    @JvmField
    val rl: View = view.findViewById(R.id.second)

    @JvmField
    val checkImageView: AppCompatImageView = view.findViewById(R.id.check_icon)
}
