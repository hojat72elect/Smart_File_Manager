package com.amaze.filemanager.fileoperations.sevenz

/**
 * Map between folders, files and streams.
 */
data class StreamMap(
    // The first Archive.packStream index of each folder.
    @JvmField
    var folderFirstPackStreamIndex: IntArray = intArrayOf(),
    // Offset to beginning of this pack stream's data, relative to the beginning of the first pack
    // stream.
    @JvmField
    var packStreamOffsets: LongArray = longArrayOf(),
    // Index of first file for each folder.
    @JvmField
    var folderFirstFileIndex: IntArray = intArrayOf(),
    // Index of folder for each file.
    @JvmField
    var fileFolderIndex: IntArray = intArrayOf()
) {
    override fun toString(): String {
        return "StreamMap with indices of ${folderFirstPackStreamIndex.size} folders, offsets of " +
                "${packStreamOffsets.size} packed streams, first files of " +
                "${folderFirstFileIndex.size} folders and folder indices for " +
                "${fileFolderIndex.size} files"
    }
}
