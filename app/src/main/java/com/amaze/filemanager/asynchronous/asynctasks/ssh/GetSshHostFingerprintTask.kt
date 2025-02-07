package com.amaze.filemanager.asynchronous.asynctasks.ssh

import com.amaze.filemanager.asynchronous.asynctasks.ftp.AbstractGetHostInfoTask
import java.security.PublicKey

class GetSshHostFingerprintTask(
    private val hostname: String,
    private val port: Int,
    private val firstContact: Boolean,
    callback: (PublicKey) -> Unit,
) : AbstractGetHostInfoTask<PublicKey, GetSshHostFingerprintTaskCallable>(
    hostname,
    port,
    callback,
) {
    override fun getTask(): GetSshHostFingerprintTaskCallable =
        GetSshHostFingerprintTaskCallable(hostname, port, firstContact)
}
