package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import com.amaze.filemanager.filesystem.HybridFileParcelable

data class SearchResult(val file: HybridFileParcelable, val matchRange: MatchRange)

typealias MatchRange = IntProgression

/** Returns the size of the [MatchRange] which means how many characters were matched */
fun MatchRange.size(): Int = this.last - this.first
