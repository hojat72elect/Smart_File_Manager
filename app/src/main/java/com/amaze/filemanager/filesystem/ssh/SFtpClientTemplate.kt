package com.amaze.filemanager.filesystem.ssh

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.SFTPClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * Template class for executing actions with [SFTPClient] while leave the complexities of
 * handling connection and session setup/teardown to [SshClientUtils].
 */
abstract class SFtpClientTemplate<T>(url: String, closeClientOnFinish: Boolean = true) :
    SshClientTemplate<T>(url, closeClientOnFinish) {
    private val LOG: Logger = LoggerFactory.getLogger(javaClass)

    override fun executeWithSSHClient(sshClient: SSHClient): T? {
        var sftpClient: SFTPClient? = null
        var retval: T? = null
        try {
            sftpClient = sshClient.newSFTPClient()
            retval = execute(sftpClient)
        } catch (e: IOException) {
            LOG.error("Error executing template method", e)
        } finally {
            if (sftpClient != null && closeClientOnFinish) {
                try {
                    sftpClient.close()
                } catch (e: IOException) {
                    LOG.warn("Error closing SFTP client", e)
                }
            }
        }
        return retval
    }

    /**
     * Implement logic here.
     *
     * @param client [SFTPClient] instance, with connection opened and authenticated, and SSH
     * session had been set up.
     * @param <T> Requested return type
     * @return Result of the execution of the type requested
     */
    @Throws(IOException::class)
    abstract fun execute(client: SFTPClient): T?
}
