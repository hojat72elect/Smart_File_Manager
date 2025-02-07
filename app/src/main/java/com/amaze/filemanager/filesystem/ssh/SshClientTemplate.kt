package com.amaze.filemanager.filesystem.ssh

import com.amaze.filemanager.filesystem.ftp.NetCopyClient
import com.amaze.filemanager.filesystem.ftp.NetCopyClientTemplate
import net.schmizz.sshj.SSHClient
import java.io.IOException

/**
 * Template class for executing actions with [SSHClient] while leave the complexities of
 * handling connection setup/teardown to [SshClientUtils].
 */
abstract class SshClientTemplate<T>(url: String, closeClientOnFinish: Boolean = true) :
    NetCopyClientTemplate<SSHClient, T>(url, closeClientOnFinish) {
    @Throws(IOException::class)
    final override fun execute(client: NetCopyClient<SSHClient>): T? {
        val sshClient: SSHClient = client.getClientImpl()
        return executeWithSSHClient(sshClient)
    }

    /**
     * Implement logic here.
     *
     * @param client [SSHClient] instance, with connection opened and authenticated
     * @param <T> Requested return type
     * @return Result of the execution of the type requested
    </T> */
    @Throws(IOException::class)
    abstract fun executeWithSSHClient(client: SSHClient): T?
}
