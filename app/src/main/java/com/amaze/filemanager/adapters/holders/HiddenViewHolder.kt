package com.amaze.filemanager.adapters.holders

import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.R

/**
 * This is the ViewHolder that formats the hidden files as defined in bookmarkrow.xml.
 *
 * @see com.amaze.filemanager.adapters.HiddenAdapter
 */
class HiddenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @JvmField
    val deleteButton: AppCompatImageButton = view.findViewById(R.id.delete_button)

    @JvmField
    val textTitle: AppCompatTextView = view.findViewById(R.id.filename)

    @JvmField
    val textDescription: AppCompatTextView = view.findViewById(R.id.file_path)

    @JvmField
    val row: LinearLayout = view.findViewById(R.id.bookmarkrow)
}
