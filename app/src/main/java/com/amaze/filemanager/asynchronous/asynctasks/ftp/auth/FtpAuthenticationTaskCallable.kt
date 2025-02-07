package com.amaze.filemanager.asynchronous.asynctasks.ftp.auth

import androidx.annotation.WorkerThread
import com.amaze.filemanager.filesystem.ftp.FTPClientImpl
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.CONNECT_TIMEOUT
import com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.FTP_URI_PREFIX
import com.amaze.filemanager.utils.PasswordUtil
import net.schmizz.sshj.userauth.UserAuthException
import org.apache.commons.net.ftp.FTPClient
import java.net.URLDecoder.decode
import java.util.concurrent.Callable
import kotlin.text.Charsets.UTF_8

open class FtpAuthenticationTaskCallable(
    protected val hostname: String,
    protected val port: Int,
    protected val username: String,
    protected val password: String,
) : Callable<FTPClient> {
    @WorkerThread
    override fun call(): FTPClient {
        val ftpClient = createFTPClient()
        ftpClient.connectTimeout = CONNECT_TIMEOUT
        ftpClient.controlEncoding = UTF_8.name()
        ftpClient.connect(hostname, port)
        val loginSuccess =
            if (username.isBlank() && password.isBlank()) {
                ftpClient.login(
                    FTPClientImpl.ANONYMOUS,
                    FTPClientImpl.generateRandomEmailAddressForLogin(),
                )
            } else {
                ftpClient.login(
                    decode(username, UTF_8.name()),
                    decode(
                        PasswordUtil.decryptPassword(password),
                        UTF_8.name(),
                    ),
                )
            }
        return if (loginSuccess) {
            ftpClient.enterLocalPassiveMode()
            ftpClient
        } else {
            throw UserAuthException("Login failed")
        }
    }

    protected open fun createFTPClient(): FTPClient =
        NetCopyClientConnectionPool.ftpClientFactory.create(FTP_URI_PREFIX)
}
