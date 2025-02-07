package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import java.util.EnumSet

typealias SearchParameters = EnumSet<SearchParameter>

/**
 * Returns [SearchParameters] extended by [other]
 */
infix fun SearchParameters.and(other: SearchParameter): SearchParameters =
    SearchParameters.of(
        other,
        *this.toTypedArray(),
    )

/**
 * Returns [SearchParameters] extended by [other]
 */
operator fun SearchParameters.plus(other: SearchParameter): SearchParameters = this and other

/**
 * Returns [SearchParameters] that reflect the given Booleans
 */
fun searchParametersFromBoolean(
    showHiddenFiles: Boolean = false,
    isRegexEnabled: Boolean = false,
    isRegexMatchesEnabled: Boolean = false,
    isRoot: Boolean = false,
): SearchParameters {
    val searchParameterList = mutableListOf<SearchParameter>()

    if (showHiddenFiles) searchParameterList.add(SearchParameter.SHOW_HIDDEN_FILES)
    if (isRegexEnabled) searchParameterList.add(SearchParameter.REGEX)
    if (isRegexMatchesEnabled) searchParameterList.add(SearchParameter.REGEX_MATCHES)
    if (isRoot) searchParameterList.add(SearchParameter.ROOT)

    return if (searchParameterList.isEmpty()) {
        SearchParameters.noneOf(SearchParameter::class.java)
    } else {
        SearchParameters.copyOf(searchParameterList)
    }
}
