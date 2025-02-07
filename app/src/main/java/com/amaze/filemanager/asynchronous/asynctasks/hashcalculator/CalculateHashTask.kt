package com.amaze.filemanager.asynchronous.asynctasks.hashcalculator

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.amaze.filemanager.R
import com.amaze.filemanager.asynchronous.asynctasks.Task
import com.amaze.filemanager.filesystem.HybridFileParcelable
import com.amaze.filemanager.filesystem.files.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.concurrent.Callable

data class Hash(val md5: String, val sha: String)

class CalculateHashTask(
    private val file: HybridFileParcelable,
    context: Context,
    view: View,
) : Task<Hash, Callable<Hash>> {
    private val log: Logger = LoggerFactory.getLogger(CalculateHashTask::class.java)

    private val task: Callable<Hash> =
        if (file.isSftp && !file.isDirectory(context)) {
            CalculateHashSftpCallback(file)
        } else if (file.isFtp || file.isDirectory(context)) {
            // Don't do this. Especially when FTPClient requires thread safety.
            DoNothingCalculateHashCallback()
        } else {
            CalculateHashCallback(file, context)
        }

    private val context = WeakReference(context)
    private val view = WeakReference(view)

    override fun getTask(): Callable<Hash> = task

    override fun onError(error: Throwable) {
        log.error("Error on calculate hash", error)
        updateView(null)
    }

    override fun onFinish(value: Hash) {
        updateView(value)
    }

    private fun updateView(hashes: Hash?) {
        val context = context.get()
        context ?: return

        val view = view.get()
        view ?: return

        val md5Text = hashes?.md5 ?: context.getString(R.string.unavailable)
        val shaText = hashes?.sha ?: context.getString(R.string.unavailable)

        val md5HashText = view.findViewById<AppCompatTextView>(R.id.t9)
        val sha256Text = view.findViewById<AppCompatTextView>(R.id.t10)

        val mMD5LinearLayout = view.findViewById<LinearLayout>(R.id.properties_dialog_md5)
        val mSHA256LinearLayout = view.findViewById<LinearLayout>(R.id.properties_dialog_sha256)

        if (!file.isDirectory(context) && file.getSize() != 0L) {
            md5HashText.text = md5Text
            sha256Text.text = shaText
            mMD5LinearLayout.setOnLongClickListener {
                FileUtils.copyToClipboard(context, md5Text)
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.md5).uppercase(Locale.getDefault()) +
                            " " +
                            context.resources.getString(R.string.properties_copied_clipboard),
                    Toast.LENGTH_SHORT,
                )
                    .show()
                false
            }
            mSHA256LinearLayout.setOnLongClickListener {
                FileUtils.copyToClipboard(context, shaText)
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.hash_sha256) + " " +
                            context.resources.getString(R.string.properties_copied_clipboard),
                    Toast.LENGTH_SHORT,
                )
                    .show()
                false
            }
        } else {
            mMD5LinearLayout.visibility = View.GONE
            mSHA256LinearLayout.visibility = View.GONE
        }
    }
}
