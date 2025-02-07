package com.amaze.filemanager.utils

import java.net.URLDecoder.decode
import java.net.URLEncoder.encode
import java.nio.charset.Charset

/**
 * Allow null checks on more than one parameters at the same time.
 * Alternative of doing nested p1?.let p2?.let
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    block: (T1, T2, T3, T4, T5) -> R?,
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) {
        block(
            p1,
            p2,
            p3,
            p4,
            p5,
        )
    } else {
        null
    }
}

/**
 * Allow null checks on more than one parameters at the same time.
 * Alternative of doing nested p1?.let p2?.let
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: (T1, T2, T3) -> R?,
): R? {
    return if (p1 != null && p2 != null && p3 != null) {
        block(
            p1,
            p2,
            p3,
        )
    } else {
        null
    }
}

/**
 * Allow null checks on more than one parameters at the same time.
 * Alternative of doing nested p1?.let p2?.let
 */
inline fun <T1 : Any, T2 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    block: (T1, T2) -> R?,
): R? {
    return if (p1 != null && p2 != null) {
        block(
            p1,
            p2,
        )
    } else {
        null
    }
}

/**
 * Convert a byte array to its hex string representation.
 *
 * Optionally takes a separator parameter.
 */
fun ByteArray.toHex(separatorStr: String = ""): String =
    joinToString(separator = separatorStr) { eachByte ->
        "%02x".format(eachByte)
    }

/**
 * Test a [List] for given path. Assumed paths in the list are not ending with /, so check for
 * both ended with or not ended with / with the given path parameter.
 */
fun List<*>.containsPath(path: String): Boolean {
    return this.contains(path) ||
            (path.endsWith('/') && this.contains(path.substringBeforeLast('/')))
}

/**
 * Convenience method to return a string in URL encoded form, with specified [Charset].
 *
 * @param charset [Charset] to encode string. Default is UTF-8
 */
fun String.urlEncoded(charset: Charset = Charsets.UTF_8): String {
    return encode(this, charset.name())
}

/**
 * Convenience method to return a string in URL decoded form, with specified [Charset].
 *
 * @param charset [Charset] to decode string. Default is UTF-8
 */
fun String.urlDecoded(charset: Charset = Charsets.UTF_8): String {
    return decode(this, charset.name())
}
