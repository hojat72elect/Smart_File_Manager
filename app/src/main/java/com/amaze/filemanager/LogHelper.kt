package com.amaze.filemanager

import org.slf4j.LoggerFactory

object LogHelper {
    private val LOG = LoggerFactory.getLogger(LogHelper::class.java)

    /**
     * Logs the message while we're in production, but if we're in debug mode, it will throw an error.
     */
    @JvmStatic
    fun logOnProductionOrCrash(message: String) {
        if (BuildConfig.DEBUG)
            throw IllegalStateException(message)
        else
            LOG.error(message)

    }
}