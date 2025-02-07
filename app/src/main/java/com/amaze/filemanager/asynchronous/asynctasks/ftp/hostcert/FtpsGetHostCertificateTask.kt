package com.amaze.filemanager.asynchronous.asynctasks.ftp.hostcert

import com.amaze.filemanager.asynchronous.asynctasks.ftp.AbstractGetHostInfoTask
import org.json.JSONObject

class FtpsGetHostCertificateTask(
    private val host: String,
    private val port: Int,
    private val explicitTls: Boolean = false,
    callback: (JSONObject) -> Unit,
) : AbstractGetHostInfoTask<JSONObject, FtpsGetHostCertificateTaskCallable>(host, port, callback) {

    override fun getTask(): FtpsGetHostCertificateTaskCallable =
        FtpsGetHostCertificateTaskCallable(host, port, explicitTls)
}
