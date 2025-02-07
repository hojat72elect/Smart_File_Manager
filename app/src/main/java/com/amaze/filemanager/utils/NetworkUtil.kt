package com.amaze.filemanager.utils

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException

object NetworkUtil {
    private val log: Logger = LoggerFactory.getLogger(NetworkUtil::class.java)

    private fun getConnectivityManager(context: Context) =
        context.applicationContext.getSystemService(Service.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    /**
     * Is the device connected to local network, either Ethernet or Wifi?
     */
    @JvmStatic
    fun isConnectedToLocalNetwork(context: Context): Boolean {
        val cm = getConnectivityManager(context)
        var connected: Boolean

        connected = cm.activeNetwork?.let { activeNetwork ->
            cm.getNetworkCapabilities(activeNetwork)?.let { ni ->
                ni.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or
                        ni.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        } ?: false


        if (!connected) {
            connected = runCatching {
                NetworkInterface.getNetworkInterfaces().toList().find { netInterface ->
                    netInterface.displayName.startsWith("rndis") or
                            netInterface.displayName.startsWith("wlan")
                }
            }.getOrElse { null } != null
        }

        return connected
    }

    /**
     * Is the device connected to Wifi?
     */
    @JvmStatic
    fun isConnectedToWifi(context: Context): Boolean {
        val cm = getConnectivityManager(context)
        return cm.activeNetwork?.let {
            cm.getNetworkCapabilities(it)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } ?: false
    }

    /**
     * Determine device's IP address.
     *
     * Caveat: doesn't handle IPv6 addresses well. Forcing return IPv4 if possible.
     */
    @JvmStatic
    fun getLocalInetAddress(context: Context): InetAddress? {
        if (!isConnectedToLocalNetwork(context)) {
            return null
        }
        if (isConnectedToWifi(context)) {
            val wm =
                context.applicationContext.getSystemService(Service.WIFI_SERVICE)
                        as WifiManager
            val ipAddress = wm.connectionInfo.ipAddress
            return if (ipAddress == 0) null else intToInet(ipAddress)
        }
        runCatching {
            NetworkInterface.getNetworkInterfaces().iterator().forEach { networkInterface ->
                networkInterface.inetAddresses.iterator().forEach { address ->
                    // this is the condition that sometimes gives problems
                    if (!address.isLoopbackAddress &&
                        !address.isLinkLocalAddress &&
                        address is Inet4Address
                    ) {
                        return address
                    }
                }
            }
        }.onFailure { e ->
            log.warn("failed to get local inet address", e)
        }
        return null
    }

    /**
     * Utility method to convert an IPv4 address in integer representation to [InetAddress].
     */
    @JvmStatic
    fun intToInet(value: Int): InetAddress? {
        val bytes = ByteArray(4)
        for (i in 0..3) {
            bytes[i] = byteOfInt(value, i)
        }
        return try {
            InetAddress.getByAddress(bytes)
        } catch (e: UnknownHostException) {
            // This only happens if the byte array has a bad length
            null
        }
    }

    private fun byteOfInt(
        value: Int,
        which: Int,
    ): Byte {
        val shift = which * 8
        return (value shr shift).toByte()
    }
}
