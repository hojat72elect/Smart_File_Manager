package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object MakeDirectoryCommand : IRootCommand() {
    /**
     * Creates an empty directory using root
     *
     * @param path path to new directory
     * @param name name of directory
     */
    @Throws(ShellNotRunningException::class)
    fun makeDirectory(
        path: String,
        name: String,
    ) {
        val mountPoint = MountPathCommand.mountPath(path, MountPathCommand.READ_WRITE)
        val filePath = "$path/$name"
        runShellCommand("mkdir \"${RootHelper.getCommandLineString(filePath)}\"")
        mountPoint?.let { MountPathCommand.mountPath(it, MountPathCommand.READ_ONLY) }
    }
}
