package com.amaze.filemanager.fileoperations.sevenz

data class BindPair(@JvmField var inIndex: Long = 0, @JvmField var outIndex: Long = 0) {
    override fun toString(): String {
        return "BindPair binding input $inIndex to output $outIndex"
    }
}