package com.amaze.filemanager.fileoperations.sevenz

object NID {
    const val kEnd = 0x00
    const val kHeader = 0x01
    const val kArchiveProperties = 0x02
    const val kAdditionalStreamsInfo = 0x03
    const val kMainStreamsInfo = 0x04
    const val kFilesInfo = 0x05
    const val kPackInfo = 0x06
    const val kUnpackInfo = 0x07
    const val kSubStreamsInfo = 0x08
    const val kSize = 0x09
    const val kCRC = 0x0A
    const val kFolder = 0x0B
    const val kCodersUnpackSize = 0x0C
    const val kNumUnpackStream = 0x0D
    const val kEmptyStream = 0x0E
    const val kEmptyFile = 0x0F
    const val kAnti = 0x10
    const val kName = 0x11
    const val kCTime = 0x12
    const val kATime = 0x13
    const val kMTime = 0x14
    const val kWinAttributes = 0x15
    const val kEncodedHeader = 0x17
    const val kStartPos = 0x18
    const val kDummy = 0x19
}