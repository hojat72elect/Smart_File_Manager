package com.amaze.filemanager.database.typeconverters

import android.util.Log
import androidx.room.TypeConverter
import com.amaze.filemanager.database.models.StringWrapper
import com.amaze.filemanager.utils.PasswordUtil
import com.amaze.filemanager.utils.PasswordUtil.decryptPassword
import com.amaze.filemanager.utils.PasswordUtil.encryptPassword

/**
 * [TypeConverter] for password strings encrypted by [PasswordUtil].
 *
 * @see StringWrapper
 *
 * @see PasswordUtil.encryptPassword
 * @see PasswordUtil.decryptPassword
 */
object EncryptedStringTypeConverter {
    @JvmStatic
    private val TAG = EncryptedStringTypeConverter::class.java.simpleName

    /**
     * Converts value in database to string.
     */
    @JvmStatic
    @TypeConverter
    fun toPassword(encryptedStringEntryInDb: String): StringWrapper {
        return runCatching {
            StringWrapper(
                decryptPassword(encryptedStringEntryInDb),
            )
        }.onFailure {
            Log.e(TAG, "Error decrypting password", it)
        }.getOrElse {
            StringWrapper(encryptedStringEntryInDb)
        }
    }

    /**
     * Encrypt given password in plaintext for storage in database.
     */
    @JvmStatic
    @TypeConverter
    fun fromPassword(unencryptedPasswordString: StringWrapper): String? {
        return runCatching {
            encryptPassword(
                unencryptedPasswordString.value,
            )
        }.onFailure {
            Log.e(TAG, "Error encrypting password", it)
        }.getOrElse {
            unencryptedPasswordString.value
        }
    }
}
