package com.amaze.filemanager.utils.smb

import com.amaze.filemanager.utils.ComputerParcelable
import com.amaze.filemanager.utils.smb.SmbDeviceScannerObservable.DiscoverDeviceStrategy
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.InetAddress

/**
 * Observable to discover reachable SMB nodes on the network.
 *
 * Uses a series of [DiscoverDeviceStrategy] instances to discover nodes.
 */
class SmbDeviceScannerObservable : Observable<ComputerParcelable>() {
    /**
     * Device discovery strategy interface.
     */
    interface DiscoverDeviceStrategy {
        /**
         * Implement this method to return list of [InetAddress] which has SMB service running.
         */
        fun discoverDevices(callback: (ComputerParcelable) -> Unit)

        /**
         * Implement this method to cleanup resources
         */
        fun onCancel()
    }

    private var discoverDeviceStrategies: Array<DiscoverDeviceStrategy> =
        arrayOf(
            WsddDiscoverDeviceStrategy(),
            SameSubnetDiscoverDeviceStrategy(),
        )

    private lateinit var observer: Observer<in ComputerParcelable>

    private lateinit var disposable: Disposable

    /**
     * Stop discovering hosts. Notify containing strategies to stop, then stop the created
     * [Observer] obtained at [subscribeActual].
     */
    fun stop() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        observer.onComplete()
    }

    /**
     * Call all strategies one by one to discover nodes.
     *
     * Given observer must be able to drop duplicated entries (which ComputerParcelable already
     * has implemented equals() and hashCode()).
     */
    override fun subscribeActual(observer: Observer<in ComputerParcelable>) {
        this.observer = observer
        this.disposable =
            merge(
                discoverDeviceStrategies.map { strategy ->
                    fromCallable {
                        strategy.discoverDevices { addr ->
                            observer.onNext(ComputerParcelable(addr.address, addr.name))
                        }
                    }.subscribeOn(Schedulers.io())
                },
            ).observeOn(Schedulers.computation()).doOnComplete {
                discoverDeviceStrategies.forEach { strategy ->
                    strategy.onCancel()
                }
            }.subscribe()
    }
}
