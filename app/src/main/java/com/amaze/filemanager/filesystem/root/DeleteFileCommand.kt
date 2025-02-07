package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object DeleteFileCommand : IRootCommand() {
    /**
     * Recursively removes a path with it's contents (if any)
     *
     * @return boolean whether file was deleted or not
     */
    @Throws(ShellNotRunningException::class)
    fun deleteFile(path: String): Boolean {
        val mountPoint = MountPathCommand.mountPath(path, MountPathCommand.READ_WRITE)
        val result =
            runShellCommandToList(
                "rm -rf \"${RootHelper.getCommandLineString(path)}\"",
            )

        mountPoint?.let { MountPathCommand.mountPath(it, MountPathCommand.READ_ONLY) }

        return result.isNotEmpty()
    }
}
