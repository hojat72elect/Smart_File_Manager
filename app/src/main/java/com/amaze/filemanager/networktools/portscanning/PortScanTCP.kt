package com.amaze.filemanager.networktools.portscanning

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

object PortScanTCP {
    /**
     * Check if a port is open with TCP
     *
     * @param ia            - address to scan
     * @param portNo        - port to scan
     * @param timeoutMillis - timeout
     * @return - true if port is open, false if not or unknown
     */
    @JvmStatic
    @Suppress("LabeledExpression")
    fun scanAddress(
        ia: InetAddress?,
        portNo: Int,
        timeoutMillis: Int,
    ): Boolean {
        return Socket().let { s ->
            runCatching {
                s.connect(InetSocketAddress(ia, portNo), timeoutMillis)
                return@let true
            }.also {
                runCatching {
                    s.close()
                }.getOrNull()
            }.getOrDefault(false)
        }
    }
}
