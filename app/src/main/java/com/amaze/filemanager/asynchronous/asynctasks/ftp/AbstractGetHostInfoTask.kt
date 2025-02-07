package com.amaze.filemanager.asynchronous.asynctasks.ftp

import android.app.ProgressDialog
import android.widget.Toast
import androidx.annotation.MainThread
import com.amaze.filemanager.R
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.asynchronous.asynctasks.Task
import java.util.concurrent.Callable

abstract class AbstractGetHostInfoTask<V, T : Callable<V>>(
    private val hostname: String,
    private val port: Int,
    private val callback: (V) -> Unit,
) : Task<V, T> {
    private lateinit var progressDialog: ProgressDialog

    /**
     * Routine to run before passing control to worker thread, usually for UI related operations.
     */
    @MainThread
    open fun onPreExecute() {
        AmazeFileManagerApplication.getInstance().run {
            progressDialog =
                ProgressDialog.show(
                    this.mainActivityContext,
                    "",
                    this.resources.getString(R.string.processing),
                )
        }
    }

    @MainThread
    override fun onError(error: Throwable) {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        Toast.makeText(
            AmazeFileManagerApplication.getInstance(),
            AmazeFileManagerApplication.getInstance()
                .resources
                .getString(
                    R.string.ssh_connect_failed,
                    hostname,
                    port,
                    error.localizedMessage,
                ),
            Toast.LENGTH_LONG,
        ).show()
    }

    @MainThread
    override fun onFinish(value: V) {
        callback(value)
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}
