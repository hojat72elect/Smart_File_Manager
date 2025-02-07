package com.amaze.filemanager.asynchronous.asynctasks.ftp.auth

import com.amaze.filemanager.filesystem.ftp.FTPClientImpl
import com.amaze.filemanager.filesystem.ftp.FTPClientImpl.Companion.ARG_TLS
import com.amaze.filemanager.filesystem.ftp.FTPClientImpl.Companion.TLS_EXPLICIT
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.FTPS_URI_PREFIX
import com.amaze.filemanager.utils.PasswordUtil
import com.amaze.filemanager.utils.X509CertificateUtil
import com.amaze.filemanager.utils.X509CertificateUtil.FINGERPRINT
import net.schmizz.sshj.userauth.UserAuthException
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPSClient
import org.json.JSONObject
import javax.net.ssl.HostnameVerifier

class FtpsAuthenticationTaskCallable(
    hostname: String,
    port: Int,
    private val certInfo: JSONObject,
    username: String,
    password: String,
    private val explicitTls: Boolean,
) : FtpAuthenticationTaskCallable(hostname, port, username, password) {
    override fun call(): FTPClient {
        val ftpClient = createFTPClient() as FTPSClient
        ftpClient.connectTimeout = NetCopyClientConnectionPool.CONNECT_TIMEOUT
        ftpClient.controlEncoding = Charsets.UTF_8.name()
        ftpClient.connect(hostname, port)
        val loginSuccess =
            if (username.isBlank() && password.isBlank()) {
                ftpClient.login(
                    FTPClientImpl.ANONYMOUS,
                    FTPClientImpl.generateRandomEmailAddressForLogin(),
                )
            } else {
                ftpClient.login(
                    username,
                    PasswordUtil.decryptPassword(password),
                )
            }
        return if (loginSuccess) {
            // RFC 2228 set protection buffer size to 0
            ftpClient.execPBSZ(0)
            // RFC 2228 set data protection level to PRIVATE
            ftpClient.execPROT("P")
            ftpClient.enterLocalPassiveMode()
            ftpClient
        } else {
            throw UserAuthException("Login failed")
        }
    }

    @Suppress("LabeledExpression")
    override fun createFTPClient(): FTPClient {
        val uri =
            buildString {
                append(FTPS_URI_PREFIX)
                if (explicitTls) {
                    append("?$ARG_TLS=$TLS_EXPLICIT")
                }
            }
        return (
                NetCopyClientConnectionPool.ftpClientFactory.create(uri)
                        as FTPSClient
                ).apply {
                this.hostnameVerifier =
                    HostnameVerifier { _, session ->
                        return@HostnameVerifier if (session.peerCertificateChain.isNotEmpty()) {
                            X509CertificateUtil.parse(
                                session.peerCertificateChain.first(),
                            )[FINGERPRINT] == certInfo.get(FINGERPRINT)
                        } else {
                            false
                        }
                    }
            }
    }
}
