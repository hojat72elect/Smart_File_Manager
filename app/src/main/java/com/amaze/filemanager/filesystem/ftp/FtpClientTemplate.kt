package com.amaze.filemanager.filesystem.ftp

import java.io.IOException
import org.apache.commons.net.ftp.FTPClient

/**
 * Template class for executing actions with [NetCopyClient] while leave the complexities of
 * handling connection setup/teardown to [NetCopyClientUtils].
 */
abstract class FtpClientTemplate<T>(url: String, closeClientOnFinish: Boolean = true) :
    NetCopyClientTemplate<FTPClient, T>(url, closeClientOnFinish) {
    @Throws(IOException::class)
    final override fun execute(client: NetCopyClient<FTPClient>): T? {
        val ftpClient: FTPClient = client.getClientImpl()
        return executeWithFtpClient(ftpClient)
    }

    /**
     * Implement logic here.
     *
     * @param client [FTPClient] instance, with connection opened and authenticated
     * @param <T> Requested return type
     * @return Result of the execution of the type requested </T>
     **/
    @Throws(IOException::class)
    abstract fun executeWithFtpClient(ftpClient: FTPClient): T?
}
