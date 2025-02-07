package com.amaze.filemanager.filesystem.ftp

import java.io.IOException

abstract class NetCopyClientTemplate<ClientType, T>
/**
 * Constructor, with closeClientOnFinish set to true (that the connection must close after `
 * execute`.
 *
 * @param url SSH connection URL, in the form of `
 * ssh://<username>:<password>@<host>:<port>` or `
 * ssh://<username>@<host>:<port>`
 */
@JvmOverloads
constructor(
    @JvmField val url: String,
    @JvmField val closeClientOnFinish: Boolean = true,
) {
    /**
     * Implement logic here.
     *
     * @param client [NetCopyClient] instance, with connection opened and authenticated
     * @param <T> Requested return type
     * @return Result of the execution of the type requested </T>
     **/
    @Throws(IOException::class)
    abstract fun execute(client: NetCopyClient<ClientType>): T?
}
