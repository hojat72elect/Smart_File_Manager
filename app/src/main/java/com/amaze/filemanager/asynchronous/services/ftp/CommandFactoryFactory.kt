package com.amaze.filemanager.asynchronous.services.ftp

import com.amaze.filemanager.filesystem.ftpserver.AndroidFtpFileSystemView
import com.amaze.filemanager.filesystem.ftpserver.commands.AVBL
import com.amaze.filemanager.filesystem.ftpserver.commands.FEAT
import com.amaze.filemanager.filesystem.ftpserver.commands.PWD
import org.apache.ftpserver.command.CommandFactory
import org.apache.ftpserver.command.CommandFactoryFactory

/**
 * Custom [CommandFactory] factory with custom commands.
 */
object CommandFactoryFactory {
    /**
     * Encapsulate custom [CommandFactory] construction logic. Append custom AVBL and PWD command,
     * as well as feature flag in FEAT command if not using [AndroidFtpFileSystemView].
     */
    fun create(useAndroidFileSystem: Boolean): CommandFactory {
        val cf = CommandFactoryFactory()
        if (!useAndroidFileSystem) {
            cf.addCommand("AVBL", AVBL())
            cf.addCommand("FEAT", FEAT())
            cf.addCommand("PWD", PWD())
        }
        return cf.createCommandFactory()
    }
}
