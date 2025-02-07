package com.amaze.filemanager.filesystem

import android.net.Uri

object SafRootHolder {
    var uriRoot: Uri? = null
        @JvmStatic set
        @JvmStatic get
    var volumeLabel: String? = null
        @JvmStatic set
        @JvmStatic get
}
