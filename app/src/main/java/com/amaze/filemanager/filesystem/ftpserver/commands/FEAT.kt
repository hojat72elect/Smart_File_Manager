package com.amaze.filemanager.filesystem.ftpserver.commands

import com.amaze.filemanager.R
import com.amaze.filemanager.application.AmazeFileManagerApplication
import org.apache.ftpserver.command.AbstractCommand
import org.apache.ftpserver.ftplet.DefaultFtpReply
import org.apache.ftpserver.ftplet.FtpReply
import org.apache.ftpserver.ftplet.FtpRequest
import org.apache.ftpserver.impl.FtpIoSession
import org.apache.ftpserver.impl.FtpServerContext

/**
 * Custom [org.apache.ftpserver.command.impl.FEAT] to add [AVBL] command to the list.
 */
class FEAT : AbstractCommand() {
    override fun execute(
        session: FtpIoSession,
        context: FtpServerContext,
        request: FtpRequest,
    ) {
        session.resetState()
        session.write(
            DefaultFtpReply(
                FtpReply.REPLY_211_SYSTEM_STATUS_REPLY,
                AmazeFileManagerApplication.getInstance().getString(R.string.ftp_command_FEAT),
            ),
        )
    }
}
