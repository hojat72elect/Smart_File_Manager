package com.amaze.filemanager.fileoperations.filesystem

import androidx.annotation.IntDef

const val UNDEFINED = -1
const val DELETE = 0
const val COPY = 1
const val MOVE = 2
const val NEW_FOLDER = 3
const val RENAME = 4
const val NEW_FILE = 5
const val EXTRACT = 6
const val COMPRESS = 7
const val SAVE_FILE = 8

@IntDef(UNDEFINED, DELETE, COPY, MOVE, NEW_FOLDER, RENAME, NEW_FILE, EXTRACT, COMPRESS, SAVE_FILE)
annotation class OperationType
