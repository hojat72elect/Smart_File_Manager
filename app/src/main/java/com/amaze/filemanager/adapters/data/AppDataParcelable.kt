package com.amaze.filemanager.adapters.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Suppress("LongParameterList")
class AppDataParcelable(
    var label: String,
    var path: String,
    var splitPathList: List<String>?,
    var packageName: String,
    var data: String,
    var fileSize: String,
    var size: Long,
    var lastModification: Long,
    var isSystemApp: Boolean,
    var openFileParcelable: OpenFileParcelable?,
) : Parcelable
