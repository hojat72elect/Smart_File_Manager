package com.amaze.filemanager.fileoperations.sevenz

import java.util.Objects

/**
 * Combines a SevenZMethod with configuration options for the method.
 * The exact type and interpretation of options depends on the method being configured. Currently
 * supported are:
 *
 * <table>
 * <caption>Options</caption>
 * <tr><th>Method</th><th>Option Type</th><th>Description</th></tr>
 * <tr><td>BZIP2</td><td>Number</td><td>Block Size - a number between 1 and 9</td></tr>
 * <tr><td>DEFLATE</td><td>Number</td><td>Compression Level - an number between 1 and 9</td></tr>
 * <tr><td>LZMA2</td><td>Number</td><td>Dictionary Size - a number between 4096 and 768 MiB (768 &lt;&lt; 20)</td></tr>
 * <tr><td>LZMA2</td><td>org.tukaani.xz.LZMA2Options</td><td>Whole set of LZMA2 options.</td></tr>
 * <tr><td>DELTA_FILTER</td><td>Number</td><td>Delta Distance - a number between 1 and 256</td></tr>
 * </table>
 *
 * @param method  the method to use.
 * @param options the options to use.
 * @throws IllegalArgumentException if the method doesn't understand the options specified, it will throw an error.
 */
data class SevenZMethodConfiguration(
    val method: SevenZMethod?,
    val options: Any?
) {

    init {
        if (options != null && Coders.findByMethod(method).canAcceptOptions(options).not()) {
            throw IllegalArgumentException("The $method method doesn't support options of type ${options::class}")
        }
    }

    override fun hashCode() = method.hashCode()

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other == null || this::class != other::class) return false

        val otherSevenZMethodConfiguration = other as SevenZMethodConfiguration
        return Objects.equals(method, otherSevenZMethodConfiguration.method) && Objects.equals(
            options,
            otherSevenZMethodConfiguration.options
        )


    }
}