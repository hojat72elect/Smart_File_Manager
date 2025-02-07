package com.amaze.filemanager.filesystem.files.sort

/** Used by FileListSorter to get the needed information from a `Parcelable` */
interface ComparableParcelable {
    /** Returns if the parcelable represents a directory */
    fun isDirectory(): Boolean

    /** Returns the name of the item represented by the parcelable */
    fun getParcelableName(): String

    /** Returns the date of the item represented by the parcelable as a Long */
    fun getDate(): Long

    /** Returns the size of the item represented by the parcelable */
    fun getSize(): Long
}
