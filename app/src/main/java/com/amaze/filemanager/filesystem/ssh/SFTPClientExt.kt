package com.amaze.filemanager.filesystem.ssh

import net.schmizz.sshj.sftp.FileAttributes
import net.schmizz.sshj.sftp.OpenMode
import net.schmizz.sshj.sftp.PacketType
import net.schmizz.sshj.sftp.RemoteFile
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.sftp.SFTPEngine
import java.io.IOException
import java.util.EnumSet
import java.util.concurrent.TimeUnit

const val READ_AHEAD_MAX_UNCONFIRMED_READS: Int = 16

/**
 * Monkey-patch [SFTPEngine.open] until sshj adds back read ahead support in [RemoteFile].
 */
@Throws(IOException::class)
fun SFTPEngine.openWithReadAheadSupport(
    path: String,
    modes: Set<OpenMode>,
    fa: FileAttributes,
): RemoteFile {
    val handle: ByteArray =
        request(
            newRequest(PacketType.OPEN).putString(path, subsystem.remoteCharset)
                .putUInt32(OpenMode.toMask(modes).toLong()).putFileAttributes(fa),
        ).retrieve(timeoutMs.toLong(), TimeUnit.MILLISECONDS)
            .ensurePacketTypeIs(PacketType.HANDLE).readBytes()
    return RemoteFile(this, path, handle)
}

/**
 * Monkey-patch [SFTPClient.open] until sshj adds back read ahead support in [RemoteFile].
 */
fun SFTPClient.openWithReadAheadSupport(path: String): RemoteFile {
    return sftpEngine.openWithReadAheadSupport(
        path,
        EnumSet.of(OpenMode.READ),
        FileAttributes.EMPTY,
    )
}
