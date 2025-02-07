package com.amaze.filemanager.utils.smb

import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.networktools.PortScan
import com.amaze.filemanager.utils.ComputerParcelable
import com.amaze.filemanager.utils.NetworkUtil
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.Inet6Address
import java.net.InetAddress

/**
 * [SmbDeviceScannerObservable.DiscoverDeviceStrategy] to just loop through other addresses within
 * same subnet (/24 netmask) and knock their SMB service ports for reachability.
 *
 * Will bypass [Inet6Address] device addresses. They may have much bigger neighourhood host count;
 * also for devices using IPv6, they shall be covered by [WsddDiscoverDeviceStrategy] anyway.
 *
 * TODO: if we can get the gateway using __legit__ API, may swarm the network in broader netmasks
 */
class SameSubnetDiscoverDeviceStrategy : SmbDeviceScannerObservable.DiscoverDeviceStrategy {
    private lateinit var worker: Disposable

    companion object {
        private const val HOST_UP_TIMEOUT = 1000
        private const val PARALLELISM = 10
        private val TCP_PORTS = arrayListOf(139, 445)
    }

    /**
     * No need to cleanup resources
     */
    override fun onCancel() {
        if (!worker.isDisposed) {
            worker.dispose()
        }
    }

    override fun discoverDevices(callback: (ComputerParcelable) -> Unit) {
        val neighbourhoods = getNeighbourhoodHosts()
        worker =
            Flowable.fromIterable(neighbourhoods)
                .parallel(PARALLELISM)
                .runOn(Schedulers.io())
                .map { addr ->
                    if (addr.isReachable(HOST_UP_TIMEOUT)) {
                        val portsReachable =
                            listOf(
                                PortScan.onAddress(addr).setPorts(TCP_PORTS).setMethodTCP()
                                    .doScan(),
                            ).flatten()
                        if (portsReachable.isNotEmpty()) {
                            addr
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }.filter {
                    it is InetAddress
                }.doOnNext { address ->
                    address as InetAddress
                    callback.invoke(
                        ComputerParcelable(
                            address.hostAddress!!,
                            if (address.hostName == address.hostAddress) {
                                address.canonicalHostName
                            } else {
                                address.hostName
                            },
                        ),
                    )
                }.sequential().subscribe()
    }

    private fun getNeighbourhoodHosts(): List<InetAddress> {
        val deviceAddress = NetworkUtil.getLocalInetAddress(AmazeFileManagerApplication.getInstance())
        return deviceAddress?.let { address ->
            if (address is Inet6Address) {
                // IPv6 neigbourhood hosts can be very big - that should use wsdd instead; hence
                // empty list here
                emptyList()
            } else {
                val networkPrefix: String = address.hostAddress!!.substringBeforeLast('.')
                (1..254).map {
                    InetAddress.getByName("$networkPrefix.$it")
                }
            }
        } ?: emptyList()
    }
}
