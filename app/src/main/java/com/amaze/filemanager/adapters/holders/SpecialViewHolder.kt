package com.amaze.filemanager.adapters.holders

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.provider.UtilitiesProvider
import com.amaze.filemanager.ui.theme.AppTheme
import com.amaze.filemanager.utils.Utils

/**
 * Check [com.amaze.filemanager.adapters.RecyclerAdapter]'s doc.
 */
class SpecialViewHolder(
    c: Context,
    view: View,
    utilsProvider: UtilitiesProvider,
    val type: Int,
) : RecyclerView.ViewHolder(view) {
    // each data item is just a string in this case
    private val txtTitle: AppCompatTextView = view.findViewById(R.id.text)

    companion object {
        const val HEADER_FILES = 0
        const val HEADER_FOLDERS = 1
        const val HEADER_SYSTEM_APP = 2
        const val HEADER_USER_APP = 3
    }

    init {
        when (type) {
            HEADER_FILES -> txtTitle.setText(R.string.files)
            HEADER_FOLDERS -> txtTitle.setText(R.string.folders)
            HEADER_SYSTEM_APP -> txtTitle.setText(R.string.system_apps)
            HEADER_USER_APP -> txtTitle.setText(R.string.user_apps)
            else -> throw IllegalStateException(": $type")
        }


        if (utilsProvider.appTheme == AppTheme.LIGHT) {
            txtTitle.setTextColor(Utils.getColor(c, R.color.text_light))
        } else {
            txtTitle.setTextColor(Utils.getColor(c, R.color.text_dark))
        }
    }
}
