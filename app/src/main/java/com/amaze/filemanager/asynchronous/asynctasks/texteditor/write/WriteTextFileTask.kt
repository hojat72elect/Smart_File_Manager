package com.amaze.filemanager.asynchronous.asynctasks.texteditor.write

import android.content.Context
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.amaze.filemanager.R
import com.amaze.filemanager.asynchronous.asynctasks.Task
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.fileoperations.exceptions.StreamNotFoundException
import com.amaze.filemanager.ui.activities.texteditor.TextEditorActivity
import com.amaze.filemanager.ui.activities.texteditor.TextEditorActivityViewModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.ref.WeakReference

class WriteTextFileTask(
    activity: TextEditorActivity,
    private val editTextString: String,
    private val textEditorActivityWR: WeakReference<TextEditorActivity>,
    private val appContextWR: WeakReference<Context>,
) : Task<Unit, WriteTextFileCallable> {
    private var log: Logger = LoggerFactory.getLogger(WriteTextFileTask::class.java)

    private val task: WriteTextFileCallable

    init {
        val viewModel: TextEditorActivityViewModel by activity.viewModels()
        task =
            WriteTextFileCallable(
                activity,
                activity.contentResolver,
                viewModel.file,
                editTextString,
                viewModel.cacheFile,
                activity.isRootExplorer,
            )
    }

    override fun getTask(): WriteTextFileCallable = task

    @MainThread
    override fun onError(error: Throwable) {
        log.error("Error on text write", error)
        val applicationContext = appContextWR.get() ?: return

        @StringRes val errorMessage: Int =
            when (error) {
                is StreamNotFoundException -> {
                    R.string.error_file_not_found
                }

                is IOException -> {
                    R.string.error_io
                }

                is ShellNotRunningException -> {
                    R.string.root_failure
                }

                else -> {
                    R.string.error
                }
            }
        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
    }

    @MainThread
    override fun onFinish(value: Unit) {
        val applicationContext = appContextWR.get() ?: return
        Toast.makeText(applicationContext, R.string.done, Toast.LENGTH_SHORT).show()
        val textEditorActivity = textEditorActivityWR.get() ?: return
        val viewModel: TextEditorActivityViewModel by textEditorActivity.viewModels()

        viewModel.original = editTextString
        viewModel.modified = false
        textEditorActivity.invalidateOptionsMenu()
    }
}
