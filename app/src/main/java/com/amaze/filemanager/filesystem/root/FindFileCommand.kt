package com.amaze.filemanager.filesystem.root

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.filesystem.RootHelper
import com.amaze.filemanager.filesystem.root.base.IRootCommand

object FindFileCommand : IRootCommand() {
    /**
     * find file at given path in root
     *
     * @return boolean whether file was deleted or not
     */
    @Throws(ShellNotRunningException::class)
    fun findFile(path: String): Boolean {
        val result =
            runShellCommandToList(
                "find \"${RootHelper.getCommandLineString(path)}\"",
            )
        return result.isNotEmpty()
    }
}
