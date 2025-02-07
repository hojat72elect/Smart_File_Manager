package com.amaze.filemanager.filesystem.ftp

import org.apache.commons.net.ProtocolCommandEvent
import org.apache.commons.net.ProtocolCommandListener
import org.apache.commons.net.SocketClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

/**
 * [ProtocolCommandListener] that logs output to a slf4j [Logger].
 *
 * Can adjust the logger level by specifying the [loggerLevel] parameter.
 */
internal class Slf4jPrintCommandListener(
    private val nologin: Boolean = true,
    private val eolMarker: Char = 0.toChar(),
    private val directionMarker: Boolean = false,
    private val loggerLevel: Level = Level.DEBUG,
) :
    ProtocolCommandListener {
    private val logger: Logger = LoggerFactory.getLogger(SocketClient::class.java)

    private val logMessage: (String) -> Unit = { msg ->
        when (loggerLevel) {
            Level.INFO -> logger.info(msg)
            Level.DEBUG -> logger.debug(msg)
            Level.ERROR -> logger.error(msg)
            Level.WARN -> logger.warn(msg)
            Level.TRACE -> logger.trace(msg)
        }
    }

    override fun protocolCommandSent(event: ProtocolCommandEvent) {
        val sb = StringBuilder()
        if (directionMarker) {
            sb.append("> ")
        }
        if (nologin) {
            val cmd = event.command
            if ("PASS".equals(cmd, ignoreCase = true) || "USER".equals(cmd, ignoreCase = true)) {
                sb.append(cmd)
                sb.append(" *******") // Don't bother with EOL marker for this!
            } else {
                sb.append(getPrintableString(event.message))
            }
        } else {
            sb.append(getPrintableString(event.message))
        }
        logMessage.invoke(sb.toString())
    }

    override fun protocolReplyReceived(event: ProtocolCommandEvent) {
        val msg =
            if (directionMarker) {
                "< ${event.message}"
            } else {
                event.message
            }
        logMessage.invoke(msg)
    }

    private fun getPrintableString(msg: String): String {
        if (eolMarker.code == 0) {
            return msg
        }
        val pos = msg.indexOf(SocketClient.NETASCII_EOL)
        if (pos > 0) {
            val sb = StringBuilder()
            sb.append(msg.substring(0, pos))
            sb.append(eolMarker)
            sb.append(msg.substring(pos))
            return sb.toString()
        }
        return msg
    }
}
