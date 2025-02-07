package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object MakeFileCommand : IRootCommand() {
    /**
     * Creates an empty file using root
     *
     * @param path path to new file
     */
    @Throws(ShellNotRunningException::class)
    fun makeFile(path: String) {
        val mountPoint = MountPathCommand.mountPath(path, MountPathCommand.READ_WRITE)
        runShellCommand("touch \"${RootHelper.getCommandLineString(path)}\"")
        mountPoint?.let { MountPathCommand.mountPath(it, MountPathCommand.READ_ONLY) }
    }
}
