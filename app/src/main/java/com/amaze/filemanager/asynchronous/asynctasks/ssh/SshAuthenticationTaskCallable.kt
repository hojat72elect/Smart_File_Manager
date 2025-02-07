package com.amaze.filemanager.asynchronous.asynctasks.ssh

import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool
import com.amaze.filemanager.filesystem.ssh.CustomSshJConfig
import com.amaze.filemanager.utils.PasswordUtil
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.KeyType
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import java.net.URLDecoder.decode
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.util.concurrent.Callable
import kotlin.text.Charsets.UTF_8

class SshAuthenticationTaskCallable(
    private val hostname: String,
    private val port: Int,
    private val hostKey: String,
    private val username: String,
    private val password: String? = null,
    private val privateKey: KeyPair? = null,
) : Callable<SSHClient> {
    init {
        require(
            true == password?.isNotEmpty() || privateKey != null,
        ) {
            "Must provide either password or privateKey"
        }
    }

    override fun call(): SSHClient {
        val sshClient =
            NetCopyClientConnectionPool.sshClientFactory
                .create(CustomSshJConfig()).also {
                    it.addHostKeyVerifier(hostKey)
                    it.connectTimeout = NetCopyClientConnectionPool.CONNECT_TIMEOUT
                }
        return run {
            sshClient.connect(hostname, port)
            if (privateKey != null) {
                sshClient.authPublickey(
                    decode(username, UTF_8.name()),
                    object : KeyProvider {
                        override fun getPrivate(): PrivateKey = privateKey.private

                        override fun getPublic(): PublicKey = privateKey.public

                        override fun getType(): KeyType = KeyType.fromKey(public)
                    },
                )
                sshClient
            } else {
                sshClient.authPassword(
                    decode(username, UTF_8.name()),
                    decode(
                        PasswordUtil.decryptPassword(
                            password!!,
                        ),
                        UTF_8.name(),
                    ),
                )
                sshClient
            }
        }
    }
}
