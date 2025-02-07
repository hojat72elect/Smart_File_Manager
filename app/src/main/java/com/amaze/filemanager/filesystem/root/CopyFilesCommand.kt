package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.MountPathCommand.mountPath
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object CopyFilesCommand : IRootCommand() {
    /**
     * Copies files using root
     * @param source given source
     * @param destination given destination
     */
    @Throws(ShellNotRunningException::class)
    fun copyFiles(
        source: String,
        destination: String,
    ) {
        // remounting destination as rw
        val mountPoint = mountPath(destination, MountPathCommand.READ_WRITE)

        runShellCommand(
            "cp -r \"${RootHelper.getCommandLineString(source)}\" " +
                    "\"${RootHelper.getCommandLineString(destination)}\"",
        )

        // we mounted the filesystem as rw, let's mount it back to ro
        mountPoint?.let { mountPath(it, MountPathCommand.READ_ONLY) }
    }
}
