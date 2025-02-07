package com.amaze.filemanager.asynchronous.asynctasks.hashcalculator

import java.util.concurrent.Callable

/**
 * A do-nothing callback that will not perform calculations on file hashes.
 */
class DoNothingCalculateHashCallback : Callable<Hash> {
    override fun call(): Hash = Hash("", "")
}
