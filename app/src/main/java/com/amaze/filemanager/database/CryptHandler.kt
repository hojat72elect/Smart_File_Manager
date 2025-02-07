package com.amaze.filemanager.database

import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.database.models.explorer.EncryptedEntry
import io.reactivex.schedulers.Schedulers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CryptHandler {
    private val log: Logger = LoggerFactory.getLogger(CryptHandler::class.java)
    private val database: ExplorerDatabase = AmazeFileManagerApplication.getInstance().explorerDatabase

    /**
     * Add [EncryptedEntry] to database.
     */
    fun addEntry(encryptedEntry: EncryptedEntry) {
        database.encryptedEntryDao().insert(encryptedEntry).subscribeOn(Schedulers.io()).subscribe()
    }

    /**
     * Remove [EncryptedEntry] of specified path.
     */
    fun clear(path: String) {
        database.encryptedEntryDao().delete(path).subscribeOn(Schedulers.io()).subscribe()
    }

    /**
     * Update specified new [EncryptedEntry] in database.
     */
    fun updateEntry(
        newEncryptedEntry: EncryptedEntry,
    ) {
        database.encryptedEntryDao().update(newEncryptedEntry).subscribeOn(Schedulers.io())
            .subscribe()
    }

    /**
     * Find [EncryptedEntry] of specified path. Returns null if not exist.
     */
    fun findEntry(path: String): EncryptedEntry? {
        return runCatching {
            database.encryptedEntryDao().select(path).subscribeOn(Schedulers.io()).blockingGet()
        }.onFailure {
            log.error(it.message!!)
        }.getOrNull()
    }

    val allEntries: Array<EncryptedEntry>
        get() {
            val encryptedEntryList =
                database.encryptedEntryDao().list().subscribeOn(Schedulers.io()).blockingGet()
            return encryptedEntryList.toTypedArray()
        }
}
