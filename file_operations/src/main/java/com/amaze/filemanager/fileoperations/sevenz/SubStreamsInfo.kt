package com.amaze.filemanager.fileoperations.sevenz

import java.util.BitSet

/**
 * Properties for non-empty files.
 */
data class SubStreamsInfo(
    // Unpacked size of each unpacked stream.
    @JvmField
    var unpackSizes: LongArray = longArrayOf(),
    // Whether CRC is present for each unpacked stream.
    @JvmField
    var hasCrc: BitSet = BitSet(),
    // CRCs of unpacked streams, if present.
    @JvmField
    var crcs: LongArray = longArrayOf()
)