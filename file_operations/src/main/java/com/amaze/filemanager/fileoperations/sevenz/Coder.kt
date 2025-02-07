package com.amaze.filemanager.fileoperations.sevenz

data class Coder(
    @JvmField
    var decompressionMethodId: ByteArray = byteArrayOf(),
    @JvmField
    var numInStreams: Long = 0L,
    @JvmField
    var numOutStreams: Long = 0L,
    @JvmField
    var properties: ByteArray = byteArrayOf(),
)