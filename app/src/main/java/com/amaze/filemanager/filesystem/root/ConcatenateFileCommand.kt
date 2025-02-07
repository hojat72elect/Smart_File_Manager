package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object ConcatenateFileCommand : IRootCommand() {
    /**
     * Concatenates (cat) file data to destination
     */
    @Throws(ShellNotRunningException::class)
    fun concatenateFile(
        sourcePath: String,
        destinationPath: String,
    ) {
        val mountPoint = MountPathCommand.mountPath(destinationPath, MountPathCommand.READ_WRITE)
        runShellCommand(
            "cat \"${RootHelper.getCommandLineString(sourcePath)}\"" +
                    " > \"${RootHelper.getCommandLineString(destinationPath)}\"",
        )
        mountPoint?.let { MountPathCommand.mountPath(it, MountPathCommand.READ_ONLY) }
    }
}
