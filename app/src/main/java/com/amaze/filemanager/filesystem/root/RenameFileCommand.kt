package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.exceptions.ShellCommandInvalidException
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object RenameFileCommand : IRootCommand() {
    private val log: Logger = LoggerFactory.getLogger(RenameFileCommand::class.java)

    /**
     * Renames file using root
     *
     * @param oldPath path to file before rename
     * @param newPath path to file after rename
     * @return if rename was successful or not
     */
    @Throws(ShellNotRunningException::class)
    fun renameFile(
        oldPath: String,
        newPath: String,
    ): Boolean {
        val mountPoint = MountPathCommand.mountPath(oldPath, MountPathCommand.READ_WRITE)
        val command =
            "mv \"${RootHelper.getCommandLineString(oldPath)}\"" +
                    " \"${RootHelper.getCommandLineString(newPath)}\""
        return try {
            val output = runShellCommandToList(command)
            mountPoint?.let { MountPathCommand.mountPath(it, MountPathCommand.READ_ONLY) }
            output.isEmpty()
        } catch (e: ShellCommandInvalidException) {
            log.warn("failed to rename file", e)
            false
        }
    }
}
