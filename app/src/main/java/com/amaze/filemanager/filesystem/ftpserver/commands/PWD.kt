package com.amaze.filemanager.filesystem.ftpserver.commands

import org.apache.ftpserver.command.AbstractCommand
import org.apache.ftpserver.ftplet.FtpException
import org.apache.ftpserver.ftplet.FtpReply
import org.apache.ftpserver.ftplet.FtpRequest
import org.apache.ftpserver.impl.FtpIoSession
import org.apache.ftpserver.impl.FtpServerContext
import org.apache.ftpserver.impl.LocalizedFtpReply
import java.io.IOException

/**
 * Monkey-patch [org.apache.ftpserver.command.impl.PWD] to prevent true path exposed to end user.
 */
class PWD : AbstractCommand() {
    @Throws(IOException::class, FtpException::class)
    override fun execute(
        session: FtpIoSession,
        context: FtpServerContext,
        request: FtpRequest,
    ) {
        session.resetState()
        val fsView = session.fileSystemView
        var currDir =
            fsView.workingDirectory.absolutePath
                .substringAfter(fsView.homeDirectory.absolutePath)
        if (currDir.isEmpty()) {
            currDir = "/"
        }
        if (!currDir.startsWith("/")) {
            currDir = "/$currDir"
        }
        session.write(
            LocalizedFtpReply.translate(
                session,
                request,
                context,
                FtpReply.REPLY_257_PATHNAME_CREATED,
                "PWD",
                currDir,
            ),
        )
    }
}
