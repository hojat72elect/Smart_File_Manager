package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object MountPathCommand : IRootCommand() {
    const val READ_ONLY = "RO"
    const val READ_WRITE = "RW"

    /**
     * Mount filesystem associated with path for writable access (rw) Since we don't have the root of
     * filesystem to remount, we need to parse output of # mount command.
     *
     * @param pathArg the path on which action to perform
     * @param operation RO or RW
     * @return String the root of mount point that was ro, and mounted to rw; null otherwise
     */
    @Throws(ShellNotRunningException::class)
    fun mountPath(
        pathArg: String,
        operation: String,
    ): String? {
        val path = RootHelper.getCommandLineString(pathArg)
        return when (operation) {
            READ_WRITE -> mountReadWrite(path)
            READ_ONLY -> {
                val command = "umount -r \"$path\""
                runShellCommand(command)
                null
            }

            else -> null
        }
    }

    private fun mountReadWrite(path: String): String? {
        val command = "mount"
        val output = runShellCommandToList(command)
        var mountPoint = ""
        var mountArgument: String? = null
        for (line in output) {
            val words = line.split(" ").toTypedArray()


            val mountPointOutputFromShell: String = words[2]
            val mountPointFileSystemTypeFromShell: String = words[4]
            val mountPointArgumentFromShell: String = words[5]

            if (path.startsWith(mountPointOutputFromShell)) {
                // current found point is bigger than last one, hence not a conflicting one
                // we're finding the best match, this omits for eg. / and /sys when we're actually
                // looking for /system
                if (mountPointOutputFromShell.length > mountPoint.length) {
                    mountPoint = mountPointOutputFromShell
                    mountArgument = mountPointArgumentFromShell
                }
            }
        }

        if (mountPoint != "" && mountArgument != null) {
            // we have the mountpoint, check for mount options if already rw
            if (mountArgument.contains("rw")) {
                // already a rw filesystem return
                return null
            } else if (mountArgument.contains("ro")) {
                // read-only file system, remount as rw
                val mountCommand = "mount -o rw,remount $mountPoint"
                val mountOutput = runShellCommandToList(mountCommand)
                return if (mountOutput.isNotEmpty()) {
                    // command failed, and we got a reason echo'ed
                    null
                } else {
                    mountPoint
                }
            }
        }
        return null
    }
}
