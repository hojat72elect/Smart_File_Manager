package com.amaze.filemanager.asynchronous.asynctasks.ssh

import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool
import com.amaze.filemanager.filesystem.ssh.CustomSshJConfig
import com.amaze.filemanager.filesystem.ssh.SshClientUtils
import net.schmizz.sshj.transport.verification.HostKeyVerifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.PublicKey
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch

class GetSshHostFingerprintTaskCallable(
    private val hostname: String,
    private val port: Int,
    private val firstContact: Boolean = false,
) : Callable<PublicKey> {
    companion object {
        @JvmStatic
        private val logger: Logger =
            LoggerFactory.getLogger(
                GetSshHostFingerprintTaskCallable::class.java,
            )
    }

    override fun call(): PublicKey {
        var holder: PublicKey? = null
        val latch = CountDownLatch(1)
        val sshClient =
            NetCopyClientConnectionPool.sshClientFactory
                .create(CustomSshJConfig()).also {
                    it.connectTimeout = NetCopyClientConnectionPool.CONNECT_TIMEOUT
                    it.addHostKeyVerifier(
                        object : HostKeyVerifier {
                            override fun verify(
                                hostname: String?,
                                port: Int,
                                key: PublicKey?,
                            ): Boolean {
                                holder = key
                                latch.countDown()
                                return true
                            }

                            override fun findExistingAlgorithms(
                                hostname: String?,
                                port: Int,
                            ): MutableList<String> = Collections.emptyList()
                        },
                    )
                }
        return runCatching {
            sshClient.connect(hostname, port)
            latch.await()
            holder!!
        }.onFailure {
            if (!firstContact) {
                logger.error("Unable to connect to [$hostname:$port]", it)
            }
            latch.countDown()
        }.getOrThrow().also {
            SshClientUtils.tryDisconnect(sshClient)
        }
    }
}
