package com.amaze.filemanager.filesystem.ftp

import net.schmizz.sshj.SSHClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SSHClientImpl(private val sshClient: SSHClient) : NetCopyClient<SSHClient> {
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(SSHClientImpl::class.java)
    }

    override fun getClientImpl() = sshClient

    override fun isConnectionValid(): Boolean = sshClient.isConnected && sshClient.isAuthenticated

    override fun expire() {
        if (sshClient.isConnected) {
            runCatching {
                sshClient.disconnect()
            }.onFailure {
                logger.warn("Error closing SSHClient connection", it)
            }
        }
    }
}
