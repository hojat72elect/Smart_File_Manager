package com.amaze.filemanager.utils

import android.util.Base64
import com.amaze.filemanager.BuildConfig
import com.amaze.filemanager.filesystem.files.CryptUtil
import com.amaze.filemanager.utils.security.SecretKeygen
import java.io.IOException
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

object PasswordUtil {
    // 12 byte long IV supported by android for GCM
    private const val IV = BuildConfig.CRYPTO_IV

    /** Helper method to encrypt plain text password  */
    @Throws(
        GeneralSecurityException::class,
        IOException::class,
    )
    private fun aesEncryptPassword(
        plainTextPassword: String,
        base64Options: Int,
    ): String? {
        val cipher = Cipher.getInstance(CryptUtil.ALGO_AES)
        val gcmParameterSpec = GCMParameterSpec(128, IV.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeygen.getSecretKey(), gcmParameterSpec)
        val encodedBytes = cipher.doFinal(plainTextPassword.toByteArray())
        return Base64.encodeToString(encodedBytes, base64Options)
    }

    /** Helper method to decrypt cipher text password  */
    @Throws(
        GeneralSecurityException::class,
        IOException::class,
    )
    private fun aesDecryptPassword(
        cipherPassword: String,
        base64Options: Int,
    ): String {
        val cipher = Cipher.getInstance(CryptUtil.ALGO_AES)
        val gcmParameterSpec = GCMParameterSpec(128, IV.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, SecretKeygen.getSecretKey(), gcmParameterSpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(cipherPassword, base64Options))
        return String(decryptedBytes)
    }

    /** Method handles encryption of plain text on various APIs  */
    @Throws(GeneralSecurityException::class, IOException::class)
    fun encryptPassword(
        plainText: String,
        base64Options: Int = Base64.URL_SAFE,
    ) = aesEncryptPassword(plainText, base64Options)


    /** Method handles decryption of cipher text on various APIs  */
    @Throws(GeneralSecurityException::class, IOException::class)
    fun decryptPassword(
        cipherText: String,
        base64Options: Int = Base64.URL_SAFE,
    ) = aesDecryptPassword(cipherText, base64Options)

}
