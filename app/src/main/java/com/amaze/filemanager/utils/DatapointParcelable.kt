package com.amaze.filemanager.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class stores the AbstractProgressiveService progress variables. This class also acts
 * as data carrier to communicate with ProcessViewerFragment
 *
 * @param name name of source file being copied
 * @param amountOfSourceFiles total number of source files to be copied
 * @param sourceProgress which file is being copied from total number of files
 * @param totalSize total size of all source files combined
 * @param byteProgress current byte position in total bytes pool
 * @param speedRaw bytes being copied per sec
 * @param move allows changing the text from "Copying" to "Moving" in case of copy
 * @param completed if the operation has finished
 */
@Parcelize
data class DatapointParcelable(
    val name: String?,
    val amountOfSourceFiles: Int,
    val sourceProgress: Int,
    val totalSize: Long,
    val byteProgress: Long,
    val speedRaw: Long,
    val move: Boolean,
    val completed: Boolean,
) : Parcelable {
    companion object {
        /**
         * For the first datapoint, everything is 0 or false except the params. Allows move boolean to
         * change the text from "Copying" to "Moving" in case of copy.
         *
         * @param name name of source file being copied
         * @param amountOfSourceFiles total number of source files to be copied
         * @param totalSize total size of all source files combined
         * @param move allows changing the text from "Copying" to "Moving" in case of copy
         */
        fun buildDatapointParcelable(
            name: String?,
            amountOfSourceFiles: Int,
            totalSize: Long,
            move: Boolean,
        ): DatapointParcelable =
            DatapointParcelable(
                name,
                amountOfSourceFiles,
                0,
                totalSize,
                0,
                0,
                move,
                false,
            )
    }
}
