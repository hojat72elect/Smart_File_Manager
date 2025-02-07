package com.amaze.filemanager.asynchronous.services.ftp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.amaze.filemanager.BuildConfig.DEBUG
import com.amaze.filemanager.asynchronous.services.ftp.FtpService.Companion.isRunning

class FtpReceiver : BroadcastReceiver() {


    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (DEBUG) {
            Log.v(TAG, "Received: ${intent.action}")
        }
        val service = Intent(context, FtpService::class.java)
        service.putExtras(intent)
        runCatching {
            if (intent.action == FtpService.ACTION_START_FTPSERVER && !isRunning()) {
                ContextCompat.startForegroundService(context, service)
            } else if (intent.action == FtpService.ACTION_STOP_FTPSERVER) {
                context.stopService(service)
            } else {
                Unit
            }
        }.onFailure {
            Log.e(TAG, "Failed to start/stop on intent ${it.message}")
        }
    }

    companion object {
        private val TAG = FtpReceiver::class.java.simpleName
    }
}
