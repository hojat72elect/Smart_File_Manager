package com.amaze.filemanager.filesystem.smb

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jcifs.CIFSException
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.context.SingletonContext
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap

object CifsContexts {
    const val SMB_URI_PREFIX = "smb://"

    private val TAG = CifsContexts::class.java.simpleName

    private val defaultProperties: Properties =
        Properties().apply {
            setProperty("jcifs.resolveOrder", "BCAST")
            setProperty("jcifs.smb.client.responseTimeout", "30000")
            setProperty("jcifs.netbios.retryTimeout", "5000")
            setProperty("jcifs.netbios.cachePolicy", "-1")
        }

    private val contexts: MutableMap<String, BaseContext> = ConcurrentHashMap()

    @JvmStatic
    fun clearBaseContexts() {
        contexts.forEach {
            try {
                it.value.close()
            } catch (e: CIFSException) {
                Log.w(TAG, "Error closing SMB connection", e)
            }
        }
        contexts.clear()
    }

    @JvmStatic
    fun createWithDisableIpcSigningCheck(
        basePath: String,
        disableIpcSigningCheck: Boolean,
    ): BaseContext {
        return if (disableIpcSigningCheck) {
            val extraProperties = Properties()
            extraProperties["jcifs.smb.client.ipcSigningEnforced"] = "false"
            create(basePath, extraProperties)
        } else {
            create(basePath, null)
        }
    }

    @JvmStatic
    fun create(
        basePath: String,
        extraProperties: Properties?,
    ): BaseContext {
        val basePathKey: String =
            Uri.parse(basePath).run {
                val prefix = "$scheme://$authority"
                val suffix = if (TextUtils.isEmpty(query)) "" else "?$query"
                "$prefix$suffix"
            }
        return if (contexts.containsKey(basePathKey)) {
            contexts.getValue(basePathKey)
        } else {
            val context =
                Single.fromCallable {
                    try {
                        val p = Properties(defaultProperties)
                        if (extraProperties != null) p.putAll(extraProperties)
                        BaseContext(PropertyConfiguration(p))
                    } catch (e: CIFSException) {
                        Log.e(TAG, "Error initialize jcifs BaseContext, returning default", e)
                        SingletonContext.getInstance()
                    }
                }.subscribeOn(Schedulers.io())
                    .blockingGet()
            contexts[basePathKey] = context
            context
        }
    }
}
