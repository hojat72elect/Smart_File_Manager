package com.amaze.filemanager.asynchronous.asynctasks.ftp.hostcert

import androidx.annotation.WorkerThread
import com.amaze.filemanager.filesystem.ftp.FTPClientImpl.Companion.ARG_TLS
import com.amaze.filemanager.filesystem.ftp.FTPClientImpl.Companion.TLS_EXPLICIT
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.CONNECT_TIMEOUT
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.FTPS_URI_PREFIX
import com.amaze.filemanager.utils.X509CertificateUtil
import org.apache.commons.net.ftp.FTPSClient
import org.json.JSONObject
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import javax.net.ssl.HostnameVerifier

open class FtpsGetHostCertificateTaskCallable(
    private val hostname: String,
    private val port: Int,
    private val explicitTls: Boolean = false,
) : Callable<JSONObject> {
    @WorkerThread
    override fun call(): JSONObject? {
        val latch = CountDownLatch(1)
        var result: JSONObject? = null
        val ftpClient = createFTPClient()
        ftpClient.connectTimeout = CONNECT_TIMEOUT
        ftpClient.controlEncoding = Charsets.UTF_8.name()
        ftpClient.hostnameVerifier =
            HostnameVerifier { _, session ->
                if (session.peerCertificateChain.isNotEmpty()) {
                    val certinfo = X509CertificateUtil.parse(session.peerCertificateChain[0])
                    result = JSONObject(certinfo)
                }
                latch.countDown()
                true
            }
        ftpClient.connect(hostname, port)
        latch.await()
        ftpClient.disconnect()
        return result
    }

    protected open fun createFTPClient(): FTPSClient =
        NetCopyClientConnectionPool.ftpClientFactory.create(
            if (explicitTls) {
                "$FTPS_URI_PREFIX?$ARG_TLS=$TLS_EXPLICIT"
            } else {
                FTPS_URI_PREFIX
            },
        ) as FTPSClient
}
