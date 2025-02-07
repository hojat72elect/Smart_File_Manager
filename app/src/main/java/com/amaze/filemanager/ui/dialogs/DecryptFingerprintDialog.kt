package com.amaze.filemanager.ui.dialogs

import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import com.amaze.filemanager.R
import com.amaze.filemanager.filesystem.files.CryptUtil
import com.amaze.filemanager.filesystem.files.EncryptDecryptUtils.DecryptButtonCallbackInterface
import com.amaze.filemanager.ui.activities.MainActivity
import com.amaze.filemanager.utils.FingerprintHandler
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Decrypt dialog prompt for user fingerprint.
 */
object DecryptFingerprintDialog {
    /**
     * Display dialog prompting user for fingerprint in order to decrypt file.
     */
    @JvmStatic
    @Throws(
        GeneralSecurityException::class,
        IOException::class,
    )
    fun show(
        c: Context,
        main: MainActivity,
        intent: Intent,
        decryptButtonCallbackInterface: DecryptButtonCallbackInterface,
    ) {
        val manager = BiometricManager.from(c)
        if (manager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS) {
            val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(c.getString(R.string.crypt_decrypt))
                    .setDescription(c.getString(R.string.crypt_fingerprint_authenticate))
                    .setConfirmationRequired(false)
                    .setNegativeButtonText(c.getString(android.R.string.cancel))
                    .build()

            val handler =
                FingerprintHandler(main, intent, promptInfo, decryptButtonCallbackInterface)
            val `object` = BiometricPrompt.CryptoObject(CryptUtil.initCipher())
            handler.authenticate(`object`)
        }
    }
}
