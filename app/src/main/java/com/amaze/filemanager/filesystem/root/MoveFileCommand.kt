package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object MoveFileCommand : IRootCommand() {
    /**
     * Move files using root
     * @param path source path
     * @param destination
     */
    @Throws(ShellNotRunningException::class)
    fun moveFile(
        path: String,
        destination: String,
    ) {
        // remounting destination as rw
        val mountPoint = MountPathCommand.mountPath(destination, MountPathCommand.READ_WRITE)
        val command =
            "mv \"${RootHelper.getCommandLineString(path)}\"" +
                    " \"${RootHelper.getCommandLineString(destination)}\""
        runShellCommand(command)
        mountPoint?.let { MountPathCommand.mountPath(it, MountPathCommand.READ_ONLY) }
    }
}
