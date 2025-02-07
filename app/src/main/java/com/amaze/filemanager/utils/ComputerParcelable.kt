package com.amaze.filemanager.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComputerParcelable(val address: String, val name: String) : Parcelable {
    override fun toString(): String = "$name [$address]"

    override fun hashCode(): Int = address.hashCode()

    override fun equals(other: Any?): Boolean =
        other is ComputerParcelable && other.address == this.address
}
