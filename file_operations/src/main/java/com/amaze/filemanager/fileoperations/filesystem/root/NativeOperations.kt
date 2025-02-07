package com.amaze.filemanager.fileoperations.filesystem.root

object NativeOperations {
    init {
        System.loadLibrary("rootoperations")
    }

    /** Whether path file is directory or not  */
    @JvmStatic
    external fun isDirectory(path: String?): Boolean
}
