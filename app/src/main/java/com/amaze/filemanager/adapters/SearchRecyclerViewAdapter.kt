package com.amaze.filemanager.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.R
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.SearchResult
import com.amaze.filemanager.ui.activities.MainActivity
import com.amaze.filemanager.ui.colors.ColorPreference
import java.util.Random

class SearchRecyclerViewAdapter :
    ListAdapter<SearchResult, SearchRecyclerViewAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(
                oldItem: SearchResult,
                newItem: SearchResult,
            ): Boolean {
                return oldItem.file.path == newItem.file.path &&
                        oldItem.file.name == newItem.file.name
            }

            override fun areContentsTheSame(
                oldItem: SearchResult,
                newItem: SearchResult,
            ): Boolean {
                return oldItem.file.path == newItem.file.path &&
                        oldItem.file.name == newItem.file.name &&
                        oldItem.matchRange == newItem.matchRange
            }
        },
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        type: Int,
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_row_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: SearchRecyclerViewAdapter.ViewHolder,
        position: Int,
    ) {
        val (file, matchResult) = getItem(position)

        val colorPreference =
            (AmazeFileManagerApplication.getInstance().mainActivityContext as MainActivity).currentColorPreference

        val fileName = SpannableString(file.name)
        fileName.setSpan(
            ForegroundColorSpan(colorPreference.accent),
            matchResult.first,
            matchResult.last + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )

        holder.fileNameTV.text = fileName
        holder.filePathTV.text = file.path.substring(0, file.path.lastIndexOf("/"))

        holder.colorView.setBackgroundColor(getRandomColor(holder.colorView.context))

        if (file.isDirectory) {
            holder.colorView.setBackgroundColor(colorPreference.primaryFirstTab)
        } else {
            holder.colorView.setBackgroundColor(colorPreference.accent)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileNameTV: AppCompatTextView = view.findViewById(R.id.searchItemFileNameTV)
        val filePathTV: AppCompatTextView = view.findViewById(R.id.searchItemFilePathTV)
        val colorView: View = view.findViewById(R.id.searchItemSampleColorView)

        init {

            view.setOnClickListener {

                val (file, _) = getItem(adapterPosition)

                if (!file.isDirectory) {
                    file.openFile(
                        AmazeFileManagerApplication.getInstance().mainActivityContext as MainActivity?,
                        false,
                    )
                } else {
                    (AmazeFileManagerApplication.getInstance().mainActivityContext as MainActivity?)
                        ?.goToMain(file.path)
                }

                (AmazeFileManagerApplication.getInstance().mainActivityContext as MainActivity?)
                    ?.appbar?.searchView?.hideSearchView()
            }
        }
    }

    private fun getRandomColor(context: Context): Int {
        return ContextCompat.getColor(
            context,
            ColorPreference.availableColors[
                Random().nextInt(
                    ColorPreference.availableColors.size - 1,
                ),
            ],
        )
    }
}
