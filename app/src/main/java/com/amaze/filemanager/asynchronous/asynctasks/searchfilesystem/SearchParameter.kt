package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

enum class SearchParameter {
    ROOT,
    REGEX,
    REGEX_MATCHES,
    SHOW_HIDDEN_FILES,
    ;

    /**
     * Returns [SearchParameters] containing `this` and [other].
     */
    infix fun and(other: SearchParameter): SearchParameters = SearchParameters.of(this, other)

    /**
     * Returns [SearchParameters] containing `this` and [other].
     */
    operator fun plus(other: SearchParameter): SearchParameters = this and other
}
