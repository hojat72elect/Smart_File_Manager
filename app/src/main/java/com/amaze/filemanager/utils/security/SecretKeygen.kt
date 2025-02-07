package com.amaze.filemanager.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.amaze.filemanager.filesystem.files.CryptUtil
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator

object SecretKeygen {

    /**
     * Return [Key] in application. Generate one if it doesn't exist in AndroidKeyStore.
     *
     * @return AES key for API 23 or above, RSA key for API 18 or above, or else null
     */
    fun getSecretKey() = getAesSecretKey()

    /**
     * Gets a secret key from Android key store. If no key has been generated with a given alias then
     * generate a new one
     */
    @Throws(
        GeneralSecurityException::class,
        IOException::class,
    )
    private fun getAesSecretKey(): Key {
        val keyStore = KeyStore.getInstance(CryptUtil.KEY_STORE_ANDROID)
        keyStore.load(null)
        return if (!keyStore.containsAlias(CryptUtil.KEY_ALIAS_AMAZE)) {
            val keyGenerator =
                KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    CryptUtil.KEY_STORE_ANDROID,
                )
            val builder =
                KeyGenParameterSpec.Builder(
                    CryptUtil.KEY_ALIAS_AMAZE,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
            builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            builder.setRandomizedEncryptionRequired(false)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        } else {
            keyStore.getKey(CryptUtil.KEY_ALIAS_AMAZE, null)
        }
    }

}
