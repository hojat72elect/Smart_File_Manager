package com.amaze.filemanager.utils

import kotlin.concurrent.Volatile

/**
 * Base class to handle progress of services operation Utilized for generation of notification,
 * talking to {@link ProcessViewerFragment} through {@link DatapointParcelable}
 */
class ProgressHandler {

    /**
     * total number of bytes to be processed; Volatile because non volatile long r/w are not atomic
     * (see Java Language Specification 17.7)
     */
    @Volatile
    var totalSize = 0L

    /**
     * total bytes written in process so far; Volatile because non volatile long r/w are not atomic
     * (see Java Language Specification 17.7)
     */
    @Volatile
    private var writtenSize = 0L

    fun getWrittenSize() = writtenSize

    // Total number of source files to be processed.
    @Volatile
    var sourceFiles = 0

    //  Number of source files processed so far
    @Volatile
    var sourceFilesProcessed = 0

    // The name of the File currently being processed
    @Volatile
    var fileName: String? = null


    // Indicates whether the service has been cancelled or not; it's false by default
    @Volatile
    var isCancelled = false

    // Callback interface to interact with process viewer fragment and notification
    @Volatile
    var progressListener: ProgressListener? = null

    /**
     * Publish progress after calculating the write length.
     * @param newPosition The position of byte for file being processed.
     */
    @Synchronized
    fun addWrittenLength(newPosition: Long) {
        val speedRaw = newPosition - writtenSize
        this.writtenSize = newPosition

        progressListener?.onProgressed(speedRaw)
    }

    @Synchronized
    fun getPercentProgress(): Float {
        if (totalSize == 0L) return 0f // Sometimes the total size is 0, because of metadata not being measured

        return (writtenSize / totalSize) * 100F
    }

    /**
     * An interface responsible for talking to this object Utilized by relevant service and eventually
     * for notification generation and process viewer fragment.
     */
    interface ProgressListener {
        /**
         * @param speed raw write speed in bytes
         */
        fun onProgressed(speed: Long)
    }
}