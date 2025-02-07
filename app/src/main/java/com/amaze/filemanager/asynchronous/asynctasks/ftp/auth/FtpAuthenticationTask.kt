package com.amaze.filemanager.asynchronous.asynctasks.ftp.auth

import androidx.annotation.MainThread
import com.amaze.filemanager.R
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.asynchronous.asynctasks.Task
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.FTP_URI_PREFIX
import org.apache.commons.net.ftp.FTPClient
import org.json.JSONObject
import java.net.SocketException
import java.net.SocketTimeoutException

class FtpAuthenticationTask(
    private val protocol: String,
    private val host: String,
    private val port: Int,
    private val certInfo: JSONObject?,
    private val username: String,
    private val password: String?,
    private val explicitTls: Boolean = false,
) : Task<FTPClient, FtpAuthenticationTaskCallable> {
    override fun getTask(): FtpAuthenticationTaskCallable {
        return if (protocol == FTP_URI_PREFIX) {
            FtpAuthenticationTaskCallable(
                host,
                port,
                username,
                password ?: "",
            )
        } else {
            FtpsAuthenticationTaskCallable(
                host,
                port,
                certInfo!!,
                username,
                password ?: "",
                explicitTls,
            )
        }
    }

    @MainThread
    override fun onError(error: Throwable) {
        if (error is SocketException || error is SocketTimeoutException) {
            AmazeFileManagerApplication.toast(
                AmazeFileManagerApplication.getInstance(),
                AmazeFileManagerApplication.getInstance()
                    .resources
                    .getString(
                        R.string.ssh_connect_failed,
                        host,
                        port,
                        error.localizedMessage ?: error.message,
                    ),
            )
        }
    }

    @MainThread
    override fun onFinish(value: FTPClient) {
        android.util.Log.d("TEST", value.toString())
    }
}
