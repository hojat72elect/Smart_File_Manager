package com.amaze.filemanager.utils.smb

import android.net.Uri
import android.text.TextUtils
import com.amaze.filemanager.fileoperations.filesystem.DOESNT_EXIST
import com.amaze.filemanager.fileoperations.filesystem.WRITABLE_ON_REMOTE
import com.amaze.filemanager.filesystem.ftp.NetCopyConnectionInfo
import com.amaze.filemanager.filesystem.ftp.NetCopyConnectionInfo.Companion.AT
import com.amaze.filemanager.filesystem.ftp.NetCopyConnectionInfo.Companion.COLON
import com.amaze.filemanager.filesystem.ftp.NetCopyConnectionInfo.Companion.QUESTION_MARK
import com.amaze.filemanager.filesystem.smb.CifsContexts.createWithDisableIpcSigningCheck
import com.amaze.filemanager.utils.PasswordUtil
import com.amaze.filemanager.utils.urlDecoded
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbException
import jcifs.smb.SmbFile
import org.slf4j.LoggerFactory
import java.net.MalformedURLException

/**
 * Class provides various utility methods for SMB client.
 */
object SmbUtil {
    @JvmStatic
    private val LOG = LoggerFactory.getLogger(SmbUtil::class.java)

    const val PARAM_DISABLE_IPC_SIGNING_CHECK = "disableIpcSigningCheck"

    /** Parse path to decrypt smb password  */
    @JvmStatic
    fun getSmbDecryptedPath(
        path: String,
    ): String {
        return buildPath(path, withPassword = {
            PasswordUtil.decryptPassword(it.urlDecoded())
        })
    }

    /** Parse path to encrypt smb password  */
    @JvmStatic
    fun getSmbEncryptedPath(
        path: String,
    ): String {
        return buildPath(path, withPassword = {
            PasswordUtil.encryptPassword(it)
        })
    }

    // At this point, credential is URL encoded to be safe from special chars.
    // No need to call URLEncoder.encode() again
    private fun buildPath(
        path: String,
        withPassword: (String) -> String?,
    ): String {
        if (!(path.contains(COLON) && path.contains(AT))) {
            // smb path doesn't have any credentials
            return path
        }
        val buffer = StringBuilder()
        NetCopyConnectionInfo(path).let { connectionInfo ->
            buffer.append(connectionInfo.prefix).append(
                connectionInfo.username.ifEmpty { "" },
            )
            if (false == connectionInfo.password?.isEmpty()) {
                val password = withPassword.invoke(connectionInfo.password)
                buffer.append(COLON).append(password?.replace("\n", ""))
            }
            buffer.append(AT).append(connectionInfo.host)
            if (connectionInfo.port > 0) {
                buffer.append(COLON).append(connectionInfo.port)
            }
            connectionInfo.defaultPath?.apply {
                buffer.append(this)
            }
            if (path.contains(QUESTION_MARK)) {
                buffer.append(QUESTION_MARK).append(path.substringAfter(QUESTION_MARK))
            }
        }
        return buffer.toString().replace("\n", "")
    }

    /**
     * Factory method to return [SmbFile] from given path.
     */
    @JvmStatic
    @Throws(MalformedURLException::class)
    fun create(path: String): SmbFile {
        val uri = Uri.parse(getSmbDecryptedPath(path))
        val disableIpcSigningCheck =
            uri.getQueryParameter(
                PARAM_DISABLE_IPC_SIGNING_CHECK,
            ).toBoolean()

        val userInfo = uri.userInfo
        return SmbFile(
            if (path.indexOf('?') < 0) path else path.substring(0, path.indexOf('?')),
            createWithDisableIpcSigningCheck(path, disableIpcSigningCheck)
                .withCredentials(createFrom(userInfo)),
        )
    }

    /**
     * Create [NtlmPasswordAuthenticator] from given userInfo parameter.
     *
     *
     * Logic borrowed directly from jcifs-ng's own code. They should make that protected
     * constructor public...
     *
     * @param userInfo authentication string, must be already URL decoded. [Uri] shall do this
     * for you already
     * @return [NtlmPasswordAuthenticator] instance
     */
    private fun createFrom(userInfo: String?): NtlmPasswordAuthenticator {
        return if (!TextUtils.isEmpty(userInfo)) {
            var dom: String? = null
            val user: String?
            var pass: String? = null
            var u: Int
            val end = userInfo!!.length
            var i = 0
            u = 0
            while (i < end) {
                val c = userInfo[i]
                if (c == ';') {
                    dom = userInfo.substring(0, i)
                    u = i + 1
                } else if (c == ':') {
                    pass = userInfo.substring(i + 1)
                    break
                }
                i++
            }
            user = userInfo.substring(u, i)
            NtlmPasswordAuthenticator(dom, user, pass)
        } else {
            NtlmPasswordAuthenticator()
        }
    }

    /**
     * SMB version of MainActivityHelper.checkFolder.
     *
     * @param path SMB path
     * @return com.amaze.filemanager.filesystem.FolderStateKt.DOESNT_EXIST if specified SMB
     * path doesn't exist on server, else com.amaze.filemanager.filesystem.FolderStateKt.WRITABLE_ON_REMOTE.
     */
    @Suppress("LabeledExpression")
    @JvmStatic
    fun checkFolder(path: String): Int {
        return Single.fromCallable {
            try {
                val smbFile = create(path)
                if (!smbFile.exists() || !smbFile.isDirectory) return@fromCallable DOESNT_EXIST
            } catch (e: SmbException) {
                LOG.warn("Error checking folder existence, assuming not exist", e)
                return@fromCallable DOESNT_EXIST
            } catch (e: MalformedURLException) {
                LOG.warn("Error checking folder existence, assuming not exist", e)
                return@fromCallable DOESNT_EXIST
            }
            WRITABLE_ON_REMOTE
        }.subscribeOn(Schedulers.io())
            .blockingGet()
    }
}
