package com.amaze.filemanager.ui.activities.texteditor

import java.io.File

data class ReturnedValueOnReadFile(
    val fileContents: String,
    val cachedFile: File?,
    val fileIsTooLong: Boolean,
)
