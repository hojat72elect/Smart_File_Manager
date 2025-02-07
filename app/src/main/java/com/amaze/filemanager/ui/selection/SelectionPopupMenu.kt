package com.amaze.filemanager.ui.selection

import android.annotation.SuppressLint
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import com.amaze.filemanager.R
import com.amaze.filemanager.adapters.RecyclerAdapter
import com.amaze.filemanager.ui.activities.MainActivity
import com.amaze.filemanager.ui.theme.AppTheme

class SelectionPopupMenu(
    private val recyclerAdapter: RecyclerAdapter,
    private val actionModeView: View,
    private val currentPath: String,
    context: Context,
) :
    PopupMenu(context, actionModeView), PopupMenu.OnMenuItemClickListener {
    companion object {
        private const val SIMILARITY_THRESHOLD = 500
        const val FUZZYNESS_FACTOR = 4

        fun invokeSelectionDropdown(
            recyclerAdapter: RecyclerAdapter,
            actionModeView: View,
            currentPath: String,
            mainActivity: MainActivity?,
        ) {
            mainActivity?.also {
                var currentContext: Context = mainActivity.applicationContext
                if (mainActivity.getAppTheme() == AppTheme.BLACK) {
                    currentContext =
                        ContextThemeWrapper(
                            mainActivity.applicationContext,
                            R.style.overflow_black,
                        )
                }
                val popupMenu =
                    SelectionPopupMenu(
                        recyclerAdapter,
                        actionModeView,
                        currentPath,
                        currentContext,
                    )
                popupMenu.inflate(R.menu.selection_criteria)
                recyclerAdapter.itemsDigested?.let { itemsDigested ->
                    if (itemsDigested.size > SIMILARITY_THRESHOLD) {
                        popupMenu.menu.findItem(R.id.select_similar).isVisible = false
                    }
                }
                if (recyclerAdapter.checkedItems.size < 2) {
                    popupMenu.menu.findItem(R.id.select_fill).isVisible = false
                }
                popupMenu.setOnMenuItemClickListener(popupMenu)
                popupMenu.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.select_all -> {
                // select_all
                recyclerAdapter.toggleChecked(
                    !recyclerAdapter
                        .areAllChecked(currentPath),
                    currentPath,
                )
            }

            R.id.select_inverse -> {
                recyclerAdapter.toggleInverse(currentPath)
            }

            R.id.select_by_type -> {
                recyclerAdapter.toggleSameTypes()
            }

            R.id.select_by_date -> {
                recyclerAdapter.toggleSameDates()
            }

            R.id.select_similar -> {
                recyclerAdapter.toggleSimilarNames()
            }

            R.id.select_fill -> {
                recyclerAdapter.toggleFill()
            }
        }
        actionModeView.invalidate()
        actionModeView.findViewById<AppCompatTextView>(R.id.item_count).text =
            recyclerAdapter
                .checkedItems.size.toString()
        return true
    }
}
