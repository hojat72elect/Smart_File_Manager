package com.amaze.filemanager.adapters.data

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes

/** Identifies a mounted volume  */
data class StorageDirectoryParcelable(
    @JvmField
    val path: String,
    @JvmField
    val name: String,
    @JvmField
    @DrawableRes
    val iconRes: Int,
) : Parcelable {
    constructor(im: Parcel) : this(
        path = im.readString()!!,
        name = im.readString()!!,
        iconRes = im.readInt(),
    )

    override fun describeContents() = 0

    override fun writeToParcel(
        parcel: Parcel,
        i: Int,
    ) {
        parcel.writeString(path)
        parcel.writeString(name)
        parcel.writeInt(iconRes)
    }

    companion object CREATOR : Parcelable.Creator<StorageDirectoryParcelable> {
        override fun createFromParcel(parcel: Parcel): StorageDirectoryParcelable {
            return StorageDirectoryParcelable(parcel)
        }

        override fun newArray(size: Int): Array<StorageDirectoryParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
