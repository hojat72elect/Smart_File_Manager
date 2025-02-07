package com.amaze.filemanager.filesystem.ftp

/**
 * Base interface for defining client class that interacts with a remote server.
 */
interface NetCopyClient<T> {
    /**
     * Returns the physical client implementation.
     */
    fun getClientImpl(): T

    /**
     * Answers if the connection of the underlying client is still valid.
     */
    fun isConnectionValid(): Boolean

    /**
     * Answers if the client returned by [getClientImpl] requires thread safety.
     *
     * [NetCopyClientUtils.execute] will see this flag and enforce locking as necessary.
     */
    fun isRequireThreadSafety(): Boolean = false

    /**
     * Implement logic to expire the underlying connection if it went stale, timeout, etc.
     */
    fun expire(): Unit
}
