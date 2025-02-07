package com.amaze.filemanager.filesystem.root.base

import com.amaze.filemanager.exceptions.ShellCommandInvalidException
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException
import com.amaze.filemanager.ui.activities.MainActivity
import com.topjohnwu.superuser.Shell

open class IRootCommand {
    /**
     * Runs the command and stores output in a list. The listener is set on the handler thread [ ]
     * [MainActivity.handlerThread] thus any code run in callback must be thread safe. Command is run
     * from the root context (u:r:SuperSU0)
     *
     * @param cmd the command
     * @return a list of results. Null only if the command passed is a blocking call or no output is
     * there for the command passed
     */
    @Throws(ShellNotRunningException::class, ShellCommandInvalidException::class)
    fun runShellCommandToList(cmd: String): List<String> {
        var interrupt = false
        var errorCode: Int = -1
        // callback being called on a background handler thread
        val commandResult = runShellCommand(cmd)
        if (commandResult.code in 1..127) {
            interrupt = true
            errorCode = commandResult.code
        }
        val result = commandResult.out
        if (interrupt) {
            throw ShellCommandInvalidException("$cmd , error code - $errorCode")
        }
        return result
    }

    /**
     * Command is run from the root context (u:r:SuperSU0)
     *
     * @param cmd the command
     */
    @Throws(ShellNotRunningException::class)
    fun runShellCommand(cmd: String): Shell.Result {
        if (!Shell.getShell().isRoot) {
            throw ShellNotRunningException()
        }
        return Shell.su(cmd).exec()
    }
}
