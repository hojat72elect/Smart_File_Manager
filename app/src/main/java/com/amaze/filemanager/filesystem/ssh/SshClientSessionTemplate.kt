package com.amaze.filemanager.filesystem.ssh

import net.schmizz.sshj.connection.channel.direct.Session
import java.io.IOException

abstract class SshClientSessionTemplate<T>
/**
 * Constructor.
 *
 * @param url SSH connection URL, in the form of `
 * ssh://<username>:<password>@<host>:<port>` or `
 * ssh://<username>@<host>:<port>`
 */(
    @JvmField val url: String,
) {
    /**
     * Implement logic here.
     *
     * @param sshClientSession [Session] instance, with connection opened and authenticated
     * @param <T> Requested return type
     * @return Result of the execution of the type requested
     */
    @Throws(IOException::class)
    abstract fun execute(sshClientSession: Session): T
}
