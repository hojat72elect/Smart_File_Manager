package com.amaze.filemanager.fileoperations.filesystem

import androidx.annotation.IntDef

const val DOESNT_EXIST = 0
const val WRITABLE_OR_ON_SDCARD = 1

// For Android 5
const val CAN_CREATE_FILES = 2
const val WRITABLE_ON_REMOTE = 3

@IntDef(DOESNT_EXIST, WRITABLE_OR_ON_SDCARD, CAN_CREATE_FILES, WRITABLE_ON_REMOTE)
annotation class FolderState
